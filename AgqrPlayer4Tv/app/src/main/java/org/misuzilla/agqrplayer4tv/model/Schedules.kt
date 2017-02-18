package org.misuzilla.agqrplayer4tv.model

import java.io.Serializable

/**
 * Created by Tomoyo on 1/11/2017.
 */
class Schedules : Serializable {
    val version = 1
    val schedules = mutableListOf<ScheduleEntry>()
}