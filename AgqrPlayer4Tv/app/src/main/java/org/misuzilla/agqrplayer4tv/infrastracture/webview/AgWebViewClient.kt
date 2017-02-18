package org.misuzilla.agqrplayer4tv.infrastracture.webview

import android.webkit.WebView
import android.webkit.WebViewClient

class AgWebViewClient(injectScriptOnPageFinished: String) : WebViewClient() {
    val injectScriptOnPageFinished: String = injectScriptOnPageFinished

    override fun onPageFinished(view: WebView, url: String) {
        view.evaluateJavascript(injectScriptOnPageFinished, null)

        super.onPageFinished(view, url)
    }
}