package org.misuzilla.agqrplayer4tv.component.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.misuzilla.agqrplayer4tv.R
import org.misuzilla.agqrplayer4tv.infrastracture.exoplayer.FlvExtractor2
import org.misuzilla.agqrplayer4tv.infrastracture.exoplayer.RtmpDataSource
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import org.misuzilla.agqrplayer4tv.model.preference.ApplicationPreference
import org.misuzilla.agqrplayer4tv.model.preference.StreamingType

class PlaybackExoPlayerFragment : PlaybackPlayerFragmentBase(), Player.EventListener {
    private lateinit var exoPlayerView: PlayerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.playback_exoplayer, container, false)

        exoPlayerView = view.findViewById(R.id.surface_view)

        return view
    }

    override fun play() {
        val trackSelector = DefaultTrackSelector(requireContext(), AdaptiveTrackSelection.Factory())
        val loadControl = DefaultLoadControl()
        val exoPlayer = SimpleExoPlayer.Builder(requireContext())
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
            .build()
        val mediaSource = when (ApplicationPreference.getStreamingType().value) {
            StreamingType.HLS -> HlsMediaSource.Factory(DefaultDataSourceFactory(this.context, USER_AGENT)).createMediaSource(Uri.parse(URL_HLS))
            StreamingType.RTMP -> ProgressiveMediaSource.Factory({ RtmpDataSource() }, { arrayOf(FlvExtractor2()) }).createMediaSource(Uri.parse(URL_RTMP))
            else -> throw NotImplementedError()
        }

        exoPlayerView.apply {
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
        exoPlayerView.player?.let {
            it.stop()
            it.release()
        }
        exoPlayerView.player = null
    }

    // ExoPlayer.EventListener
    override fun onPlayerError(error: ExoPlaybackException) {
        reportError()
    }

    override fun onLoadingChanged(isLoading: Boolean) {
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        val player = exoPlayerView.player;
        if (player != null) {
            viewModel.setIsPlaying(player.playbackState == ExoPlayer.STATE_READY && player.playWhenReady)
        }
    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
    }

    override fun onSeekProcessed() {
    }

    override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {
    }

    override fun onPositionDiscontinuity(reason: Int) {
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
    }

    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
    }
}