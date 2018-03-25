package org.misuzilla.agqrplayer4tv.component.fragment

import android.content.Intent
import android.support.v4.app.Fragment
import android.util.Log
import org.misuzilla.agqrplayer4tv.R
import org.misuzilla.agqrplayer4tv.component.activity.ErrorActivity
import org.misuzilla.agqrplayer4tv.infrastracture.extension.addTo
import org.misuzilla.agqrplayer4tv.model.LogicalDateTime
import org.misuzilla.agqrplayer4tv.model.NowPlaying
import org.misuzilla.agqrplayer4tv.model.TimetableProgram
import jp.keita.kagurazaka.rxproperty.ReadOnlyRxProperty
import jp.keita.kagurazaka.rxproperty.RxProperty
import jp.keita.kagurazaka.rxproperty.toRxProperty
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subjects.BehaviorSubject
import rx.subjects.Subject
import rx.subscriptions.CompositeSubscription
import java.util.*
import java.util.concurrent.TimeUnit

abstract class PlaybackPlayerFragmentBase : Fragment() {
    private val onError = BehaviorSubject.create<Unit>()

    protected val URL_HLS: String get () = context!!.getString(R.string.url_hls)
    protected val URL_RTMP: String get () = context!!.getString(R.string.url_rtmp)
    protected val USER_AGENT: String get () = context!!.getString(R.string.user_agent)

    protected val subscriptions = CompositeSubscription()

    abstract var isPlaying: ReadOnlyRxProperty<Boolean>
        protected set
    val progress: ReadOnlyRxProperty<Int>
    val elapsedSeconds: ReadOnlyRxProperty<Int>

    init {
        val rxPropMode = EnumSet.of(RxProperty.Mode.RAISE_LATEST_VALUE_ON_SUBSCRIBE, RxProperty.Mode.DISTINCT_UNTIL_CHANGED)
        val refreshInterval = Observable.interval(1, TimeUnit.SECONDS)
                .map { NowPlaying.now?.program?.get() ?: TimetableProgram.DEFAULT }
                .publish()
                .refCount()

        elapsedSeconds = refreshInterval
                .map { LogicalDateTime.now.time.totalSeconds - it.start.totalSeconds  }
                .toRxProperty(rxPropMode)

        progress = refreshInterval
                .map {
                    val duration = (it.end.totalSeconds - it.start.totalSeconds)
                    val elapsed = elapsedSeconds.get()
                    val percent = Math.floor(elapsed / duration * 100.0).toInt()

                    Math.max(100, Math.min(0, percent))
                }
                .toRxProperty(rxPropMode)

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
                .addTo(subscriptions)
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
        subscriptions.unsubscribe()
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