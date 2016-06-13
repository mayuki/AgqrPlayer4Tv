using Android.Util;
using Android.Webkit;

namespace AgqrPlayer4Tv.Components
{
    class AgWebChromeClient : WebChromeClient
    {
        public override void OnReceivedTitle(WebView view, string title)
        {
            // titleが確定したときはdocumentが使えるようになってるらしい。どう考えても裏技です。本当にありがとうございました。
            // http://code.google.com/p/webpagetest/source/browse/trunk/agent/browser/android/src/com/google/wireless/speed/velodrome/Browser.java?r=1123#221

            // アンケートが出てきて詰むのを回避できることがある(タイミングによるやも)
            // Android 5.x でconfirmが死んでることがあるので…。
            view.LoadUrl("javascript:(function(){ document.cookie = 'joqr='; window.confirm = function () { return true; }; })()");
        }

        public override bool OnConsoleMessage(ConsoleMessage consoleMessage)
        {
            Log.Debug("AgWebChromeClient", consoleMessage.SourceId() + " (" + consoleMessage.LineNumber() + "): " + consoleMessage.Message());
            return true;
        }
    }
}