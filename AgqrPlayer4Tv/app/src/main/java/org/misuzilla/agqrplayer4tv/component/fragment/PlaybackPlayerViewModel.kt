package org.misuzilla.agqrplayer4tv.component.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.*
import org.misuzilla.agqrplayer4tv.infrastracture.extension.addTo
import org.misuzilla.agqrplayer4tv.model.LogicalDateTime
import org.misuzilla.agqrplayer4tv.model.NowPlaying
import org.misuzilla.agqrplayer4tv.model.TimetableProgram
import java.util.concurrent.TimeUnit

class PlaybackPlayerViewModel : ViewModel(), CoroutineScope by MainScope() {
    private val isPlaying: MutableLiveData<Boolean> by lazy {
        MutableLiveData(false)
    }
    private val progress: MutableLiveData<Int> by lazy {
        MutableLiveData(0)
    }
    private val elapsedSeconds: MutableLiveData<Int> by lazy {
        MutableLiveData(0)
    }

    private val runLoopJob: Job

    init {
        runLoopJob = launch {
            runLoop()
        }
    }

    suspend fun runLoop() {
        while (currentCoroutineContext().isActive) {
            val currentProgram = NowPlaying.now.getProgram().value ?: TimetableProgram.DEFAULT

            elapsedSeconds.value = LogicalDateTime.now.time.totalSeconds - currentProgram.start.totalSeconds

            val duration = (currentProgram.end.totalSeconds - currentProgram.start.totalSeconds)
            val elapsed = elapsedSeconds.value!!
            val percent = Math.floor(elapsed / duration * 100.0).toInt()
            progress.value = Math.max(100, Math.min(0, percent))

            delay(1000)
        }
    }

    override fun onCleared() {
        super.onCleared()
        runLoopJob.cancel()
    }

    fun setIsPlaying(value: Boolean) {
        isPlaying.value = value
    }

    fun isPlaying(): LiveData<Boolean> {
        return isPlaying
    }

    fun getProgress(): LiveData<Int> {
        return progress
    }

    fun getElapsedSeconds(): LiveData<Int> {
        return elapsedSeconds
    }
}