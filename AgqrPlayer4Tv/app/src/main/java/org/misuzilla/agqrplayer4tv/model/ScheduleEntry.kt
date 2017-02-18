package org.misuzilla.agqrplayer4tv.model

import java.io.Serializable

/**
 * Created by Tomoyo on 1/10/2017.
 */
class ScheduleEntry(val requestCode: Int, val program: TimetableProgram, val dueTime: Long) : Serializable, Comparable<ScheduleEntry> {
    override fun compareTo(other: ScheduleEntry): Int {
        return this.requestCode - other.requestCode
    }

    val expired: Boolean
        get () = System.currentTimeMillis() > dueTime
}