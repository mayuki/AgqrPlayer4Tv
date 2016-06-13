using Android.Webkit;

namespace AgqrPlayer4Tv.Components
{
    class AgWebViewClient : WebViewClient
    {
        public string InjectScriptOnPageFinished { get; }
        
        public AgWebViewClient(string injectScriptOnPageFinished)
        {
            this.InjectScriptOnPageFinished = injectScriptOnPageFinished;
        }

        public override void OnPageFinished(WebView view, string url)
        {
            // �K���ɃX�N���v�g����s���ĂȂ񂩗ǂ��Ȃ�
            // �����ŏ���Ɏn�܂�悤�ɂ���(�G)
            view.EvaluateJavascript(this.InjectScriptOnPageFinished, null);
        }
    }
}