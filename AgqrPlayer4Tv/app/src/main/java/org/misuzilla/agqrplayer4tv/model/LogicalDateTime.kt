package org.misuzilla.agqrplayer4tv.model

import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDateTime
import org.threeten.bp.Duration
import java.util.*

/**
 * 論理時刻と曜日を保持するクラスです。水曜25時(=木曜1時)といった値を扱います。
 */
class LogicalDateTime(val dayOfWeek: DayOfWeek, val time: TimetableProgramTime) {

    override fun toString() = "${time.hours}:${time.minutes}:${time.seconds} ($dayOfWeek)"

    companion object {
        /**
         * 論理時刻と曜日を取得します。
         */
        val now: LogicalDateTime get() {
            val now = LocalDateTime.now()
            var dayOfWeek = now.dayOfWeek
            val hours = now.hour
            if (hours >= 0 && hours <= 5) {
                // 24時間を過ぎてカウントできるように
                // 前の曜日に戻す必要がある
                dayOfWeek = if (dayOfWeek == DayOfWeek.SUNDAY) DayOfWeek.SATURDAY else dayOfWeek - 1;
            }

            return LogicalDateTime(dayOfWeek, TimetableProgramTime.fromStandardTime(hours, now.minute, now.second))
        }
    }
}