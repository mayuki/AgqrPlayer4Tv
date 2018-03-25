package org.misuzilla.agqrplayer4tv.component.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import org.misuzilla.agqrplayer4tv.R
import jp.keita.kagurazaka.rxproperty.ReadOnlyRxProperty
import jp.keita.kagurazaka.rxproperty.RxProperty
import org.misuzilla.agqrplayer4tv.component.activity.SettingsActivity
import org.misuzilla.agqrplayer4tv.infrastracture.webview.AgWebChromeClient
import org.misuzilla.agqrplayer4tv.infrastracture.webview.AgWebViewClient
import rx.Observable

class PlaybackWebViewFragment : PlaybackPlayerFragmentBase() {
    private val URL_WEB: String get () = context!!.getString(R.string.url_web)

    override var isPlaying: ReadOnlyRxProperty<Boolean> = RxProperty(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.playback_webview, container, false)
        val webView = view!!.findViewById<WebView>(R.id.web_view)

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
        buttonSetting.setOnClickListener { startActivity(Intent(activity!!.applicationContext, SettingsActivity::class.java)) }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        subscriptions.clear()
        super.onDetach()
    }

    override fun stop() {
    }

    override fun play() {
    }
}
