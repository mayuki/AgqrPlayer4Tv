package org.misuzilla.agqrplayer4tv.model

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import org.misuzilla.agqrplayer4tv.component.activity.MainActivity
import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import rx.Observable
import rx.subjects.BehaviorSubject
import rx.subjects.Subject
import java.io.*


/**
 * Created by Tomoyo on 1/9/2017.
 */
class Reservation(val context: Context) {
    private var schedules = Schedules()
    private val onChangeSubject = BehaviorSubject.create<Reservation>()
    private val alarmManager: AlarmManager
        get() = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val onChangeAsObservable: Observable<Reservation>
        get() = onChangeSubject.asObservable()
    val scheduledCount: Int
        get() = getAllScheduledEntries().size

    init {
        load()
    }

    /**
     * 指定した番組が予約済みかどうかを返します。
     */
    fun isScheduled(program: TimetableProgram): Boolean {
        val entry = getAllScheduledEntries().firstOrNull { it.program.title == program.title }
        if (entry == null || entry.expired) return false

        return true
    }

    /**
     * 指定した番組の視聴予約を登録します。
     */
    fun schedule(program: TimetableProgram): Unit {
        if (isScheduled(program)) return

        val requestCode = getUnusedRequestCode()
        if (requestCode == null) throw ReservationException("Can't schedule a program.")

        val dueTime = ((program.start.totalSeconds - LogicalDateTime.now.time.totalSeconds - PRESTART_TIME).toLong() * 1000) // 次までの時間(ms)、ただし1分前に始まる
        val pendingIntent = createPendingIntent(requestCode)
        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(System.currentTimeMillis() + dueTime, pendingIntent), pendingIntent)
        schedules.schedules.add(ScheduleEntry(requestCode, program, System.currentTimeMillis() + dueTime))
        save()
    }

    /**
     * 指定した番組の視聴予約をキャンセルします。
     */
    fun cancel(program: TimetableProgram): Unit {
        val entry = getAllScheduledEntries().firstOrNull { !it.expired && it.program.title == program.title }
        if (entry == null) return

        alarmManager.cancel(createPendingIntent(entry.requestCode))
        schedules.schedules.remove(entry)
        save()
    }
    /**
     * 指定した番組の視聴予約をキャンセルします。
     */
    fun cancel(entry: ScheduleEntry): Unit {
        val entry = getAllScheduledEntries().firstOrNull { !it.expired && it.program.title == entry.program.title }
        if (entry == null) return

        alarmManager.cancel(createPendingIntent(entry.requestCode))
        schedules.schedules.remove(entry)
        save()
    }

    /**
     * 視聴予約をしている番組の一覧を取得します。
     */
    fun getAllScheduledPrograms(timetable: Timetable): Collection<TimetableProgram> {
        var timetableData = timetable.cachedTimetableStoredDataset.data
        return getAllScheduledEntries().map { entry ->
            timetableData[LogicalDateTime.now.dayOfWeek]?.firstOrNull { it.title == entry.program.title } ?: entry.program // 最新の情報にしておく
        }
    }

    /**
     * 期限切れの予約をすべて削除します。
     */
    fun removeAllExpired() {
        schedules.schedules.removeAll { it.expired } // cleanup
    }

    /**
     * すべての視聴予約を削除します。
     */
    fun cancelAll() {
        // 念のため全部のRequestCodeに対応するPendingIntentを削除してみる
        PENDING_INTENT_REQUEST_CODE_RANGE.forEach {
            alarmManager.cancel(createPendingIntent(it))
        }
        schedules.schedules.clear()
        save()
    }

    private fun save() {
        removeAllExpired()

        context.openFileOutput(SCHEDULES_JSON_NAME, MODE_PRIVATE).bufferedWriter().use {
            Log.d(TAG, "Save: " + Gson().toJson(schedules))
            Gson().toJson(schedules, it)
        }

        onChangeSubject.onNext(this)
    }

    private fun load() {
        try {
            context.openFileInput(SCHEDULES_JSON_NAME).bufferedReader().use {
                val json = it.readText()
                Log.d(TAG, "Load: " + json)
                schedules = Gson().fromJson<Schedules?>(json, Schedules::class.java) ?: Schedules()
            }
        } catch (ex : IOException) {
            schedules = Schedules()
        } catch (ex : JsonSyntaxException) {
            schedules = Schedules()
        }
    }

    private fun getAllScheduledEntries(): Collection<ScheduleEntry> {
        return schedules.schedules.filter {
            !it.expired
        }
    }

    private fun getUnusedRequestCode(): Int? {
        val scheduledIds = getAllScheduledEntries().map { it.requestCode }
        return PENDING_INTENT_REQUEST_CODE_RANGE.subtract(scheduledIds).firstOrNull()
    }

    private fun createPendingIntent(requestCode: Int): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    companion object {
        val PENDING_INTENT_REQUEST_CODE_RANGE = (100000..100099)
        const val PRESTART_TIME: Int = 60 // seconds
        const val SCHEDULES_JSON_NAME = "schedules.json"
        const val TAG = "Reservation"

        private var instance_: Reservation? = null
        val instance: Reservation get() = instance_!!

        fun initialize(context: Context) {
            instance_ = Reservation(context)
        }
    }

    class ReservationException(message: String) : RuntimeException(message) {
    }
}