package org.misuzilla.agqrplayer4tv.model

import org.threeten.bp.LocalDate
import java.io.Serializable

class TimetableProgramTime(hours: Int, minutes: Int, seconds: Int) : Comparable<TimetableProgramTime>, Serializable {
    var totalSeconds: Int = 0
        private set
    val hours: Int
        get() = Math.floor(totalSeconds / (60*60.0)).toInt()
    val minutes: Int
        get() = Math.floor(totalSeconds % (60*60.0) / 60).toInt()
    val seconds: Int
        get() = Math.floor(totalSeconds % 60.0).toInt()

    constructor(totalSeconds: Int) : this(0, 0, 0) {
        this.totalSeconds = totalSeconds
    }

    init {
        totalSeconds = (hours * 60 * 60) + (minutes * 60) + seconds
    }

    fun plusSeconds(seconds: Int): TimetableProgramTime {
        return TimetableProgramTime(totalSeconds + seconds)
    }

    override fun compareTo(other: TimetableProgramTime): Int {
        return this.totalSeconds.compareTo(other.totalSeconds)
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is TimetableProgramTime) return false
        return other.totalSeconds == this.totalSeconds
    }

    override fun hashCode(): Int {
        return totalSeconds.hashCode()
    }

    override fun toString() = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    fun toShortString() = String.format("%02d:%02d", hours, minutes, seconds)

    companion object {
        /**
         * 24時間制の時刻から生成します。
         */
        fun fromStandardTime(hours: Int, minutes: Int, seconds: Int): TimetableProgramTime {
            val logicalHours = if ((hours >= 0) && (hours <= 5)) 24 + hours else hours // 5時台までは1日をあらわす
            return TimetableProgramTime(logicalHours, minutes, seconds)
        }

        /**
         * 30時間形式で文字列をパースして秒数を返します。
         */
        fun parse(value: String): TimetableProgramTime {
            val match = Regex("^(\\d{1,2}):(\\d{1,2})").matchEntire(value)
            if (match == null) throw IllegalArgumentException("invalid time format")

            val hours = match.groupValues[1].toInt()
            val minutes = match.groupValues[2].toInt()

            return TimetableProgramTime.fromStandardTime(hours, minutes, 0)
        }
    }
}