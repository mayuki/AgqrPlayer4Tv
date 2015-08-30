/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.misuzilla.agqrplayer4tv;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView webView = ((WebView)this.findViewById(R.id.webView));
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.3; Nexus 7 Build/JSS15Q) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2307.2 Safari/537.36");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new AgWebChromeClient());
        webView.setWebViewClient(new AgWebViewClient());
        webView.loadUrl("http://www.uniqueradio.jp/agplayerf/newplayerf2-sp.php");
    }
}
