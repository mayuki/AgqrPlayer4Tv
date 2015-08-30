package org.misuzilla.agqrplayer4tv;

import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Tomoyo on 2015/08/30.
 */
public class AgWebViewClient extends WebViewClient {
    public void  onPageFinished (WebView view, String url) {
        Log.d("AgWebViewClient", "onPageFinished");

        // 適当にスクリプトを実行してなんか良しなに
        // 自動で勝手に始まるようにする(雑)
        // タイミングによっては実行できないっぽい。詳しく調べる気がないのでなんかキーを押したら再生しようという適当な気持ち
        view.evaluateJavascript("(function(){ document.querySelector('video').play(); })()", null);
        view.evaluateJavascript("(function(){ document.addEventListener('keydown', function () { document.querySelector('video').play(); }); })()", null);
    }
}
