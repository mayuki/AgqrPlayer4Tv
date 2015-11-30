package org.misuzilla.agqrplayer4tv;

import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Tomoyo on 2015/08/30.
 */
public class AgWebViewClient extends WebViewClient {
    private String _injectScriptOnPageFinished;

    public AgWebViewClient(String injectScriptOnPageFinished) {
        this._injectScriptOnPageFinished = injectScriptOnPageFinished;
    }

    @Override
    public void onPageFinished (WebView view, String url) {
        Log.d("AgWebViewClient", "onPageFinished");

        // 適当にスクリプトを実行してなんか良しなに
        // 自動で勝手に始まるようにする(雑)
        view.evaluateJavascript(this._injectScriptOnPageFinished, null);
    }
}
