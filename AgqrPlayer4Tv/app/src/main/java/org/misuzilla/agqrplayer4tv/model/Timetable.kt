package org.misuzilla.agqrplayer4tv.model

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.microsoft.appcenter.analytics.Analytics
import kotlinx.coroutines.*
import okhttp3.*
import org.misuzilla.agqrplayer4tv.R
import org.misuzilla.agqrplayer4tv.infrastracture.OkHttpClientHelper
import org.misuzilla.agqrplayer4tv.infrastracture.extension.get
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDateTime
import java.io.IOException

class Timetable(private val context: Context) : CoroutineScope by MainScope() {
    var cachedTimetableStoredDataset: TimetableStoredDataset = TimetableStoredDataset.empty

    suspend fun getDatasetAsync(): TimetableStoredDataset {
        if (cachedTimetableStoredDataset.isExpired) {
            try {
                cachedTimetableStoredDataset = fetchAndParseAsync().await()
            } catch (e: Exception) {
                Analytics.trackEvent("Exception", mapOf(
                    "caller" to "Timetable.getDataAsync",
                    "name" to e.javaClass.name,
                    "message" to e.message.toString()
                ))
            }
        }

        return cachedTimetableStoredDataset
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

    private fun fetchAndParseAsync(): Deferred<TimetableStoredDataset> {
        val completableDeferred = CompletableDeferred<TimetableStoredDataset>()

        val jsonTimetableUrl = context.resources.getString(R.string.url_json_timetable)
        val client = OkHttpClientHelper.create(context)

        client.get(jsonTimetableUrl).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                completableDeferred.completeExceptionally(e!!)
            }

            override fun onResponse(call: Call?, response: Response?) {
                launch {
                    // 別スレッドでこねる
                    val dataset = async(Dispatchers.Default) {
                        val timetableEntriesByDayOfWeek = Gson().fromJson<Map<String, Collection<TimetableProgramEntry>>>(
                            response!!.body().charStream(), object : TypeToken<Map<String, Collection<TimetableProgramEntry>>>() {}.type
                        )
                        val dataset = TimetableStoredDataset(LocalDateTime.now())
                        for (dayOfWeek in DayOfWeek.values()) {
                            // .NETは日曜始まり、Javaは月曜始まり…
                            timetableEntriesByDayOfWeek[((dayOfWeek.ordinal + 1) % 7).toString()]?.forEach {
                                dataset.data[dayOfWeek]?.add(it.toTimetableProgram())
                            }
                        }
                        dataset
                    }.await()

                    completableDeferred.complete(dataset)
                }
            }
        })

        return completableDeferred
    }

    companion object {
        const val TAG = "Timetable"
    }
}