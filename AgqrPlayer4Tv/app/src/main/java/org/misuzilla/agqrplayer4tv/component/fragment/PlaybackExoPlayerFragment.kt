package org.misuzilla.agqrplayer4tv.component.fragment

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.misuzilla.agqrplayer4tv.R
import org.misuzilla.agqrplayer4tv.infrastracture.exoplayer.FlvExtractor2
import org.misuzilla.agqrplayer4tv.infrastracture.exoplayer.RtmpDataSource
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ExoPlayer.EventListener
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.flv.FlvExtractor
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import jp.keita.kagurazaka.rxproperty.ReadOnlyRxProperty
import jp.keita.kagurazaka.rxproperty.RxProperty
import jp.keita.kagurazaka.rxproperty.toRxProperty
import org.misuzilla.agqrplayer4tv.model.preference.ApplicationPreference
import org.misuzilla.agqrplayer4tv.model.preference.StreamingType
import rx.subjects.BehaviorSubject

class PlaybackExoPlayerFragment : PlaybackPlayerFragmentBase(), EventListener {

    override var isPlaying: ReadOnlyRxProperty<Boolean> = RxProperty(false)
    private var exoPlayerView: SimpleExoPlayerView? = null
    private val stateChanged = BehaviorSubject.create(Unit)

    init {
        isPlaying = stateChanged
                .map { exoPlayerView?.player }
                .filter { it != null }
                .map { it!!.playbackState == ExoPlayer.STATE_READY && it.playWhenReady }
                .toRxProperty()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.playback_exoplayer, container, false)

        exoPlayerView = view.findViewById(R.id.surface_view) as SimpleExoPlayerView

        return view
    }

    override fun play() {
        val trackSelector = DefaultTrackSelector(Handler(), AdaptiveVideoTrackSelection.Factory(DefaultBandwidthMeter()))
        val loadControl = DefaultLoadControl()
        val exoPlayer = ExoPlayerFactory.newSimpleInstance(this.context, trackSelector, loadControl)
        val mediaSource = when (ApplicationPreference.streamingType.get()) {
            StreamingType.HLS -> HlsMediaSource(Uri.parse(URL_HLS), DefaultDataSourceFactory(this.context, USER_AGENT), null, null)
            StreamingType.RTMP -> ExtractorMediaSource(Uri.parse(URL_RTMP), { RtmpDataSource() }, { arrayOf(FlvExtractor2()) }, null, null)
        }

        exoPlayerView?.apply {
            player = exoPlayer
            useController = false
        }

        exoPlayer.apply {
            addListener(this@PlaybackExoPlayerFragment)
            prepare(mediaSource)
            playWhenReady = true
        }
    }

    override fun stop() {
        exoPlayerView?.player?.let {
            it.stop()
            it.release()
        }
        exoPlayerView?.player = null
    }

    // ExoPlayer.EventListener
    override fun onPlayerError(error: ExoPlaybackException?) {
        this.reportError()
    }

    override fun onLoadingChanged(isLoading: Boolean) {
    }

    override fun onPositionDiscontinuity() {
    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        stateChanged.onNext(Unit)
    }
}