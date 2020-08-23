package org.misuzilla.agqrplayer4tv.component.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import org.misuzilla.agqrplayer4tv.R

class PlaybackDefaultVideoViewFragment : PlaybackPlayerFragmentBase() {
    private lateinit var videoView: VideoView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")

        val view = inflater.inflate(R.layout.playback_controls, container, false)
        val videoView = view.findViewById<VideoView>(R.id.video_view)
        videoView.apply {
            setOnCompletionListener { updateStatus()  }
            setOnPreparedListener { updateStatus() }
            setOnInfoListener { _, _, _ ->
                updateStatus()
                false
            }
            setOnErrorListener{ _, _, _ ->
                updateStatus()
                reportError()
                true
            }
        }

        return view
    }

    private fun updateStatus() {
        viewModel.setIsPlaying(videoView.isPlaying)
    }

    override fun play() {
        Log.d(TAG, "play")
        videoView.stopPlayback()
        videoView.setVideoURI(Uri.parse(URL_HLS), mapOf("User-Agent" to USER_AGENT))
        videoView.start()
        updateStatus()
    }

    override fun stop() {
        Log.d(TAG, "stop")
        videoView.stopPlayback()
        updateStatus()
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