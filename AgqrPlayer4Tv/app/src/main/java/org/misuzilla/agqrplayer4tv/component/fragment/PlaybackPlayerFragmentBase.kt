package org.misuzilla.agqrplayer4tv.component.fragment

import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.misuzilla.agqrplayer4tv.R
import org.misuzilla.agqrplayer4tv.component.activity.ErrorActivity
import org.misuzilla.agqrplayer4tv.infrastracture.extension.*
import java.util.concurrent.TimeUnit


abstract class PlaybackPlayerFragmentBase : Fragment() {
    private val onError = BehaviorSubject.create<Unit>()
    public val viewModel: PlaybackPlayerViewModel by viewModels()

    protected val URL_HLS: String get () = requireContext().getString(R.string.url_hls)
    protected val URL_RTMP: String get () = requireContext().getString(R.string.url_rtmp)
    protected val USER_AGENT: String get () = requireContext().getString(R.string.user_agent)

    protected val disposables = CompositeDisposable()

    init {
        observeError()
    }

    protected fun reportError() {
        onError.onNext(Unit)
    }

    private fun observeError() {
        onError.delay(1, TimeUnit.SECONDS)
                .doOnNext { Log.d(TAG, "ERROR!") }
                .takeUntil(onError.buffer(30, TimeUnit.SECONDS, 5).filter { it.count() > 5 })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ play() }, { Log.e(TAG, it.toString()) }, { onRetryThresholdReached() })
                .addTo(disposables)
    }

    private fun onRetryThresholdReached() {
        Log.d(TAG, "OnRetryThresholdReached")
        startActivity(Intent(this.context, ErrorActivity::class.java))
    }

    abstract fun play()
    abstract fun stop()

    override fun onDetach() {
        super.onDetach()
        stop()
        disposables.clear()
    }

    override fun onResume() {
        super.onResume()
        play()
    }

    override fun onPause() {
        super.onPause()
        stop()
    }

    override fun onStop() {
        super.onStop()
        stop()
    }

    companion object {
        const val TAG = "PlaybackPlayerFragment"
    }
}