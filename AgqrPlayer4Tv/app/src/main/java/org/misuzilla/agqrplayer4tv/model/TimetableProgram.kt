package org.misuzilla.agqrplayer4tv.model

import java.io.Serializable

class TimetableProgram(
        var title: String,
        var mailAddress: String?,
        var personality: String?,
        var start: TimetableProgramTime,
        var end: TimetableProgramTime = TimetableProgramTime(0, 0, 0)
) : Serializable {

    val isPlaying: Boolean
        get () = start <= LogicalDateTime.now.time && end >= LogicalDateTime.now.time

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is TimetableProgram) return false

        return (this.title == other.title &&
                this.mailAddress == other.mailAddress &&
                this.personality == other.personality &&
                this.start == other.start &&
                this.end == other.end)
    }

    override fun hashCode(): Int {
        return this.title.hashCode()
    }

    override fun toString() = "TimetableProgram: ${title} <${mailAddress}> (${personality}) ${start}-${end}"

    companion object {
        val DEFAULT = TimetableProgram("超A&G+", "", "", TimetableProgramTime(0, 0, 0), TimetableProgramTime(5, 59, 59))
    }
}
