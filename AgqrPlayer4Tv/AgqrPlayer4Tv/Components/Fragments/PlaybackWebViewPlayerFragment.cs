using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using AgqrPlayer4Tv.Activities;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Webkit;
using Android.Widget;

namespace AgqrPlayer4Tv.Components.Fragments
{
    public class PlaybackWebViewPlayerFragment : PlaybackPlayerFragmentBase
    {
        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            var view = inflater.Inflate(Resource.Layout.PlaybackWebView, container, false);
            var webView = view.FindViewById<WebView>(Resource.Id.WebView);

            webView.Settings.UserAgentString = UserAgent;
            webView.Settings.JavaScriptEnabled = true;
            webView.Settings.MediaPlaybackRequiresUserGesture = false; // ユーザーの操作なしに再生を始める指定

            webView.SetWebChromeClient(new AgWebChromeClient());
            webView.SetWebViewClient(new AgWebViewClient(new StreamReader(Resources.OpenRawResource(Resource.Raw.InjectScriptOnPageFinished)).ReadToEnd()));

            webView.LoadUrl("http://www.uniqueradio.jp/agplayerf/newplayerf2-sp.php");

            var buttonReload = view.FindViewById<Button>(Resource.Id.ButtonReload);
            buttonReload.Click += (sender, e) => webView.Reload();

            var buttonSettings = view.FindViewById<Button>(Resource.Id.ButtonSettings);
            buttonSettings.Click += (sender, e) =>
            {
                this.StartActivity(new Intent(this.Activity.ApplicationContext, typeof(SettingsActivity)));
            };

            return view;
        }

        public override void Play()
        {
        }

        public override void Stop()
        {
        }
    }
}