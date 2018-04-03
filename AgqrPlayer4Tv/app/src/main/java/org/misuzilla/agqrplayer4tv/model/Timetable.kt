package org.misuzilla.agqrplayer4tv.model

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.microsoft.appcenter.analytics.Analytics
import org.misuzilla.agqrplayer4tv.R
import org.misuzilla.agqrplayer4tv.infrastracture.extension.enqueueAndToSingle
import org.misuzilla.agqrplayer4tv.infrastracture.extension.observeOnUIThread
import okhttp3.OkHttpClient
import okhttp3.Request
import org.misuzilla.agqrplayer4tv.infrastracture.OkHttpClientHelper
import org.misuzilla.agqrplayer4tv.infrastracture.extension.get
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import rx.Completable
import rx.Observable
import rx.Single
import java.util.*

class Timetable(private val context: Context) {
    var cachedTimetableStoredDataset: TimetableStoredDataset = TimetableStoredDataset.empty

    fun getDatasetAsync(): Single<TimetableStoredDataset> {
        if (cachedTimetableStoredDataset.isExpired) {
            return fetchAndParseAsync()
                    .doOnSuccess { cachedTimetableStoredDataset = it }
                    .onErrorResumeNext {
                        Analytics.trackEvent("Exception", mapOf("caller" to "Timetable.getDataAsync", "name" to it.javaClass.name, "message" to it.message.toString()))
                        Single.just(cachedTimetableStoredDataset)
                    }
        } else {
            return Observable.just(cachedTimetableStoredDataset).toSingle()
        }
    }

    class TimetableProgramEntry() {
        @SerializedName("Start")
        var start: Int = 0
        @SerializedName("End")
        var end: Int = 0
        @SerializedName("Title")
        var title: String? = null
        @SerializedName("MailAddress")
        var mailAddress: String? = null
        @SerializedName("Personality")
        var personality: String? = null

        fun toTimetableProgram(): TimetableProgram {
            return TimetableProgram(this.title ?: "", this.mailAddress, this.personality, TimetableProgramTime(this.start), TimetableProgramTime(this.end))
        }
    }

    private fun fetchAndParseAsync(): Single<TimetableStoredDataset> {
        val jsonTimetableUrl = context.resources.getString(R.string.url_json_timetable)
        val client = OkHttpClientHelper.create(context)

        return client.get(jsonTimetableUrl).enqueueAndToSingle()
                .map { Gson().fromJson<Map<String, Collection<TimetableProgramEntry>>>(it.body().charStream(), object : TypeToken<Map<String, Collection<TimetableProgramEntry>>>() {}.type) }
                .map { timetableEntriesByDayOfWeek ->
                    val dataset = TimetableStoredDataset(LocalDateTime.now())
                    for (dayOfWeek in DayOfWeek.values()) {
                        // .NETは日曜始まり、Javaは月曜始まり…
                        timetableEntriesByDayOfWeek[((dayOfWeek.ordinal + 1) % 7).toString()]?.forEach {
                            dataset.data[dayOfWeek]?.add(it.toTimetableProgram())
                        }
                    }
                    dataset
                }
                .observeOnUIThread()
    }

    companion object {
        const val TAG = "Timetable"
    }
}