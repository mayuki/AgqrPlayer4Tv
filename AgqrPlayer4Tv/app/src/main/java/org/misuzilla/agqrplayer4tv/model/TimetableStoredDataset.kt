package org.misuzilla.agqrplayer4tv.model

import org.threeten.bp.DayOfWeek
import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import java.util.*

class TimetableStoredDataset(updatedAt: LocalDateTime) {
    val updatedAt = updatedAt.toEpochSecond(ZoneOffset.UTC)
    val version = 2

    val data = DayOfWeek.values().associate { it to mutableListOf<TimetableProgram>() }

    val isExpired: Boolean
        get() = LocalDateTime.ofEpochSecond(updatedAt + EXPIRE, 0, ZoneOffset.UTC) < LocalDateTime.now()

    companion object {
        val EXPIRE = Duration.ofHours(1).seconds

        val empty: TimetableStoredDataset by lazy {
            TimetableStoredDataset(LocalDateTime.MIN)
        }
    }
}