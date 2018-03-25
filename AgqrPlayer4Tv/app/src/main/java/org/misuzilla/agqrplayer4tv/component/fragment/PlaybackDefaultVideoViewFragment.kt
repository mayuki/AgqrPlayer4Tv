package org.misuzilla.agqrplayer4tv.component.fragment

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import org.misuzilla.agqrplayer4tv.R
import org.misuzilla.agqrplayer4tv.infrastracture.extension.addTo
import jp.keita.kagurazaka.rxproperty.ReadOnlyRxProperty
import jp.keita.kagurazaka.rxproperty.RxProperty
import jp.keita.kagurazaka.rxproperty.toRxProperty
import rx.Observable
import rx.subjects.BehaviorSubject

class PlaybackDefaultVideoViewFragment : PlaybackPlayerFragmentBase() {
    override var isPlaying: ReadOnlyRxProperty<Boolean> = RxProperty(true)
    val videoView = RxProperty<VideoView?>(null)

    private val updateIsPlaying = BehaviorSubject.create(Unit)

    init {
        Log.d(TAG, "init")
        isPlaying = videoView.asObservable()
                .filter({ it != null })
                .switchMap {
                    Observable.merge(
                            updateIsPlaying,
                            Observable.create<Unit>() { observer ->
                                it?.setOnCompletionListener { observer.onNext(Unit) }
                                it?.setOnPreparedListener { observer.onNext(Unit) }
                                it?.setOnInfoListener { mediaPlayer, i1, i2 -> observer.onNext(Unit); false }
                                it?.setOnErrorListener { mediaPlayer, i1, i2 -> observer.onNext(Unit); false }
                            }
                    )
                }
                .map { videoView.get()?.isPlaying ?: false }
                .toRxProperty()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")
        val view = inflater.inflate(R.layout.playback_controls, container, false)
        videoView.set(view.findViewById(R.id.video_view))

        videoView.asObservable()
                .filter { it != null }
                .switchMap {
                    Observable.create<Unit> { observer ->
                        it?.setOnErrorListener { mediaPlayer, i1, i2 -> observer.onNext(Unit); true }
                    }
                }
                .subscribe({ reportError() })
                .addTo(subscriptions)

        return view
    }


    override fun play() {
        Log.d(TAG, "play")

        videoView.get()?.let {
            Log.d(TAG, "play2")
            it.stopPlayback()
            it.setVideoURI(Uri.parse(URL_HLS), mapOf("User-Agent" to USER_AGENT))
            it.start()
        }
    }

    override fun stop() {
        videoView.get()?.let {
            it.stopPlayback()
            updateIsPlaying.onNext(Unit)
        }
    }

    companion object {
        const val TAG = "PlaybackDefaultVideo"

        fun newInstance(): PlaybackPlayerFragmentBase {
            val fragment = PlaybackDefaultVideoViewFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}