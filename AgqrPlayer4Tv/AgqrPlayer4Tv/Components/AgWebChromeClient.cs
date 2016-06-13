using Android.Util;
using Android.Webkit;

namespace AgqrPlayer4Tv.Components
{
    class AgWebChromeClient : WebChromeClient
    {
        public override void OnReceivedTitle(WebView view, string title)
        {
            // title���m�肵���Ƃ���document���g����悤�ɂȂ��Ă�炵���B�ǂ��l���Ă����Z�ł��B�{���ɂ��肪�Ƃ��������܂����B
            // http://code.google.com/p/webpagetest/source/browse/trunk/agent/browser/android/src/com/google/wireless/speed/velodrome/Browser.java?r=1123#221

            // �A���P�[�g���o�Ă��ċl�ނ̂�����ł��邱�Ƃ�����(�^�C�~���O�ɂ����)
            // Android 5.x ��confirm������ł邱�Ƃ�����̂Łc�B
            view.LoadUrl("javascript:(function(){ document.cookie = 'joqr='; window.confirm = function () { return true; }; })()");
        }

        public override bool OnConsoleMessage(ConsoleMessage consoleMessage)
        {
            Log.Debug("AgWebChromeClient", consoleMessage.SourceId() + " (" + consoleMessage.LineNumber() + "): " + consoleMessage.Message());
            return true;
        }
    }
}