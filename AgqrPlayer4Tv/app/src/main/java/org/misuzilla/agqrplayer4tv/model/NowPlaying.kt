package org.misuzilla.agqrplayer4tv.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.*

class NowPlaying(private val timetable: Timetable) : CoroutineScope by CoroutineScope(Dispatchers.Main) {
    private val program: MutableLiveData<TimetableProgram> = MutableLiveData()
    private val runningJob: Job

    fun getProgram(): LiveData<TimetableProgram> {
        return program
    }

    fun getTitle(): LiveData<String> {
        return Transformations.map(program, { it.title })
    }

    fun getSubTitle(): LiveData<String> {
        return Transformations.map(program, { (it.personality ?: "") + (it.mailAddress?.let { " <${it}>" } ?: "") })
    }

    fun getBody(): LiveData<String> {
        return Transformations.map(program, { "${it.start.toShortString()}～${it.end.toShortString()}" })
    }

    init {
        runningJob = launch {
            runLoop()
        }
    }

    suspend fun runLoop() {
        while (currentCoroutineContext().isActive) {
            val timetableProgram = async(Dispatchers.IO) {
                try {
                    val dataset = timetable.getDatasetAsync()
                    val currentProgram = dataset.data[LogicalDateTime.now.dayOfWeek]?.firstOrNull { it.isPlaying }

                    return@async currentProgram ?: TimetableProgram.DEFAULT
                } catch (e: Exception) { }

                return@async TimetableProgram.DEFAULT
            }.await()

            program.value = timetableProgram

            delay(1000 * 10) // 10秒
        }
    }

    companion object {
        lateinit var now: NowPlaying

        fun initialize(timetable: Timetable) {
            now = NowPlaying(timetable)
        }
    }
}