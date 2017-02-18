package org.misuzilla.agqrplayer4tv.infrastracture.webview

import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView

class AgWebChromeClient : WebChromeClient() {
    override fun onReceivedTitle(view: WebView, title: String?) {
        // titleが確定したときはdocumentが使えるようになってるらしい。どう考えても裏技です。本当にありがとうございました。
        // http://code.google.com/p/webpagetest/source/browse/trunk/agent/browser/android/src/com/google/wireless/speed/velodrome/Browser.java?r=1123#221

        // アンケートが出てきて詰むのを回避できることがある(タイミングによるやも)
        // Android 5.x でconfirmが死んでることがあるので…。
        view.loadUrl("javascript:(function(){ document.cookie = 'joqr='; window.confirm = function () { return true; }; })()");
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        Log.d("AgWebChromeClient", "${consoleMessage?.sourceId()} (${consoleMessage?.lineNumber()}): ${consoleMessage?.message()}");
        return true;
    }
}