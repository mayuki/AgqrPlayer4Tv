package org.misuzilla.agqrplayer4tv.model

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.VectorDrawable
import android.support.v4.content.ContextCompat
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import com.microsoft.appcenter.analytics.Analytics
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import org.misuzilla.agqrplayer4tv.R
import org.misuzilla.agqrplayer4tv.component.activity.MainActivity
import org.misuzilla.agqrplayer4tv.component.activity.PlayConfirmationActivity
import org.misuzilla.agqrplayer4tv.infrastracture.OkHttpClientHelper
import org.misuzilla.agqrplayer4tv.infrastracture.extension.*
import org.misuzilla.agqrplayer4tv.model.preference.ApplicationPreference
import rx.Completable
import rx.Observable
import rx.Single

class UpdateRecommendation(private val context: Context) {
    /**
     * 番組とそのタイトル画像のマッピングファイルを取得、解析します。
     */
    fun getProgramImageMappingAsync(): Single<Map<String, String>> {
        val imageMappingUrl = context.resources.getString(R.string.url_image_mapping)
        val client = OkHttpClientHelper.create(context)
        return client.get(imageMappingUrl).enqueueAndToSingle()
                .map { it.body().string() }
                .onErrorResumeNext {
                    Analytics.trackEvent("Exception", mapOf("caller" to "UpdateRecommendation.getProgramImageMappingAsync", "name" to it.javaClass.name, "message" to it.message.toString()))
                    Single.just("")
                }
                .map {
                    // カンマ区切りファイルを雑にパースする
                    it.split('\n')
                        .map { it.trim() }
                        .filter { !it.startsWith("#") }
                        .filter { !it.isNullOrBlank() }
                        .map { it.split(',') }
                        .filter { it.size >= 3 }
                        .flatMap { listOf(Pair(it[0], it[2]), Pair(if (it[1].isNullOrBlank()) it[0] + "-EmailAddress" else it[1], it[2])) } // メールアドレスがないとき用に適当に返す
                        .toMap()
                }
                .cache()
    }

    /**
     * タイムテーブルの更新 & マッピングの取得、その後画像も取得
     */
    fun getCurrentAndNextProgramAndIcon(timetable: Timetable): Single<List<ProgramWithIcon>> {
        val mappingTask = this.getProgramImageMappingAsync()
        return Single.zip(timetable.getDatasetAsync(), mappingTask, { l, r -> l })
                .flatMap { timetableDataset ->
                    val now = LogicalDateTime.now

                    // 現在と次の番組を取得する
                    val currentAndNext = timetableDataset.data[now.dayOfWeek]!!.filter { it.end >= now.time }.take(2)

                    // マッピングから画像を取得する
                    currentAndNext.map { program ->
                        getCardImageFromProgramAsync(mappingTask, program).map { ProgramWithIcon(program, it) }
                    }.whenAll()
                }
    }

    fun getCardImageFromProgramAsync(mappingTask: Single<Map<String, String>>, program: TimetableProgram): Single<Bitmap> {
        val width = context.resources.displayMetrics.toDevicePixel(context.resources.getDimension(R.dimen.recommendation_empty_width))
        val height = context.resources.displayMetrics.toDevicePixel(context.resources.getDimension(R.dimen.recommendation_empty_height))

        return mappingTask
                .flatMap {
                    when {
                        it.containsKey(program.mailAddress) -> Picasso.with(context).load(it[program.mailAddress]).centerInside().resize(width, height).toSingle()
                        it.containsKey(program.title) -> Picasso.with(context).load(it[program.title]).centerInside().resize(width, height).toSingle()
                        else -> Single.just(createNotificationImageFromText(program))
                    }
                }
                .onErrorReturn {
                    Log.e(TAG, it.message, it)
                    createNotificationImageFromText(program)
                }
                .map { bitmap ->
                    if (Reservation.instance.isScheduled(program)) {
                        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                        val canvas = Canvas(newBitmap)

                        canvas.drawBitmap(bitmap, (width - bitmap.width) / 2f, (height - bitmap.height) / 2f, Paint())

                        (ContextCompat.getDrawable(context, R.drawable.ic_timer_white) as VectorDrawable).let {
                            it.bounds = Rect(16, 16, 128 + 16, 128 + 16)

                            canvas.drawPath(
                                    Path().apply {
                                        moveTo(0f, 0f)
                                        lineTo(0f, it.bounds.height() * 2f)
                                        lineTo(it.bounds.width() * 2f, 0f)
                                        close()
                                    },
                                    Paint().apply {
                                        color = ContextCompat.getColor(context, R.color.accent_dark)
                                        style = Paint.Style.FILL
                                    }
                            )
                            it.draw(canvas)
                        }

                        newBitmap
                    } else {
                        bitmap
                    }
                }
                .cache()
    }

    private fun createNotificationImageFromText(program: TimetableProgram): Bitmap {
        val colorAccent = ContextCompat.getColor(context, R.color.accent)
        val colorAccentDark = ContextCompat.getColor(context, R.color.accent_dark)
        val width = context.resources.displayMetrics.toDevicePixel(context.resources.getDimension(R.dimen.recommendation_empty_width))
        val height = context.resources.displayMetrics.toDevicePixel(context.resources.getDimension(R.dimen.recommendation_empty_height))
        val textSizeRec = context.resources.displayMetrics.toDevicePixel(context.resources.getDimension(R.dimen.recommendation_text_size))
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val paint = Paint().apply {
            textSize = textSizeRec.toFloat()
            color = Color.WHITE
            isAntiAlias = true
        }

        val textPaint = TextPaint().apply {
            textSize = textSizeRec.toFloat()
            color = Color.WHITE
            isAntiAlias = true
        }

        val canvas = Canvas(bitmap)
        canvas.save()

        // Fill: background rectangle
        paint.color = colorAccent
        canvas.drawRect(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat(), paint)

        // Draw: Program title
        paint.color = Color.WHITE
        val textWidth = paint.measureText(program.title)
        val margin = 16f
        val textLayout = StaticLayout(program.title, textPaint, canvas.width - (margin.toInt() * 2), Layout.Alignment.ALIGN_CENTER, 1f, 0f, false)
        canvas.translate(margin, (canvas.height / 2f) - (textLayout.height / 2f))
        textLayout.draw(canvas)

        canvas.restore()

        return bitmap
    }

    /**
     * プログラムとそのアイコン(大きな画像)のセットです。
     */
    class ProgramWithIcon(val program: TimetableProgram, val icon: Bitmap) {
        /**
         * 通知用のオブジェクトを生成します。
         */
        fun createNotification(context: Context): Notification {
            val colorAccentDark = ContextCompat.getColor(context, R.color.accent_dark)

            val isNowPlaying = program.isPlaying

            // 現在放送中かどうかで起動するものが違う
            val intent = if (isNowPlaying) {
                Intent(context, MainActivity::class.java)
            } else {
                PlayConfirmationActivity.createIntent(context, program, true)
            }

            val builder = Notification.Builder(context)
                    .setContentTitle(program.title)
                    .setContentText(
                            if (isNowPlaying)
                                String.format(context.resources.getString(R.string.recommendation_current), program.end.toShortString())
                            else
                                String.format(context.resources.getString(R.string.recommendation_upcoming), program.start.toShortString())
                    )
                    .setPriority(if (isNowPlaying) Notification.PRIORITY_MAX else Notification.PRIORITY_DEFAULT)
                    .setLocalOnly(true)
                    .setOngoing(true)
                    .setCategory(Notification.CATEGORY_RECOMMENDATION)
                    .setLargeIcon(icon)
                    .setSmallIcon(R.mipmap.small_icon)
                    .setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                    .setColor(colorAccentDark)

            val builderBigPicture = Notification.BigPictureStyle()
            builderBigPicture.setBuilder(builder)

            return builder.build()
        }
    }

    companion object {
        const val TAG = "UpdateRecommendation"
    }
}