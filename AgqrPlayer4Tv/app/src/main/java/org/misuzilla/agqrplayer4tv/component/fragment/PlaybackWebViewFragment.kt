package org.misuzilla.agqrplayer4tv.component.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import org.misuzilla.agqrplayer4tv.R
import org.misuzilla.agqrplayer4tv.component.activity.SettingsActivity
import org.misuzilla.agqrplayer4tv.infrastracture.webview.AgWebChromeClient
import org.misuzilla.agqrplayer4tv.infrastracture.webview.AgWebViewClient

class PlaybackWebViewFragment : PlaybackPlayerFragmentBase() {
    private val URL_WEB: String get () = requireContext().getString(R.string.url_web)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.playback_webview, container, false)
        val webView = view.findViewById<WebView>(R.id.web_view)

        with (webView) {
            settings.userAgentString = USER_AGENT
            settings.javaScriptEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false // ユーザーの操作なしに再生を始める指定

            setWebChromeClient(AgWebChromeClient())
            setWebViewClient(AgWebViewClient(context.resources.openRawResource(R.raw.inject_script_on_page_finished).reader().readText()))

            loadUrl(URL_WEB)
        }

        val buttonReload = view.findViewById<Button>(R.id.button_reload)
        val buttonSetting = view.findViewById<Button>(R.id.button_settings)
        buttonReload.setOnClickListener { webView.reload() }
        buttonSetting.setOnClickListener { startActivity(Intent(requireActivity().applicationContext, SettingsActivity::class.java)) }

        return view
    }

    override fun stop() {
    }

    override fun play() {
    }
}
