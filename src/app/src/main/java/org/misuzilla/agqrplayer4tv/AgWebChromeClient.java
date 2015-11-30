package org.misuzilla.agqrplayer4tv;

import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by Tomoyo on 2015/08/30.
 */
public class AgWebChromeClient extends WebChromeClient {
    @Override
    public void onReceivedTitle (WebView view, String title) {
        // titleが確定したときはdocumentが使えるようになってるらしい。どう考えても裏技です。本当にありがとうございました。
        // http://code.google.com/p/webpagetest/source/browse/trunk/agent/browser/android/src/com/google/wireless/speed/velodrome/Browser.java?r=1123#221

        // アンケートが出てきて詰むのを回避できることがある(タイミングによるやも)
        // Android 5.x でconfirmが死んでることがあるので…。
        view.loadUrl("javascript:(function(){ document.cookie = 'joqr='; window.confirm = function () { return true; }; })()");
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage cm) {
        Log.d("AgWebChromeClient", cm.sourceId() + " (" + cm.lineNumber() + "): " + cm.message());
        return true;
    }
}
