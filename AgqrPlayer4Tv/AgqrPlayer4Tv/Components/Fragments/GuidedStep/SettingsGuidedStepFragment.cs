using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using AgqrPlayer4Tv.Activities;
using AgqrPlayer4Tv.Infrastracture.Extensions;
using AgqrPlayer4Tv.Model.Platform;
using Android.App;
using Android.Content.PM;
using Android.OS;
using Android.Support.V17.Leanback.App;
using Android.Support.V17.Leanback.Widget;

namespace AgqrPlayer4Tv.Components.Fragments.GuidedStep
{
    /// <summary>
    /// 設定画面のトップとなるGuidedStepクラスです。
    /// </summary>
    public class SettingsGuidedStepFragment : GuidedStepFragment
    {
        public override GuidanceStylist.Guidance OnCreateGuidance(Bundle savedInstanceState)
        {
            return new GuidanceStylist.Guidance("設定", "アプリケーションの各種設定を行います", "AgqrPlayer for Android TV", null);
        }

        public override void OnCreateActions(IList<GuidedAction> actions, Bundle savedInstanceState)
        {
            actions.AddAction(0, "配信形式", "動画の配信形式を設定します");
            actions.AddAction(1, "プレイヤー", "動画のプレイヤーを設定します");
            actions.AddAction(2, "このアプリケーションについて", "アプリケーションについての情報を表示します");
        }

        public override void OnGuidedActionClicked(GuidedAction action)
        {
            switch (action.Id)
            {
                case 0: Add(FragmentManager, new StreamingSettingGuidedStepFragment()); break;
                case 1: Add(FragmentManager, new PlayerSettingGuidedStepFragment()); break;
                case 2: Add(FragmentManager, new AboutSettingGuidedStepFragment()); break;
            }
        }
    }


    /// <summary>
    /// 設定: 配信形式設定のGuidedStepクラスです。
    /// </summary>
    public class StreamingSettingGuidedStepFragment : GuidedStepFragment
    {
        public override GuidanceStylist.Guidance OnCreateGuidance(Bundle savedInstanceState)
        {
            return new GuidanceStylist.Guidance("配信形式", "動画の配信形式を設定します。この設定はExoPlayerを利用している際にのみ有効です", "設定", null);
        }

        public override void OnCreateActions(IList<GuidedAction> actions, Bundle savedInstanceState)
        {
            var appPrefs = ApplicationMain.ServiceLocator.GetInstance<ApplicationPreference>();
            actions.AddCheckAction(1, "RTMP", "RTMP形式を利用します。PC向けの配信形式で遅延が小さめです。通信が不安定になるとアプリケーションがクラッシュする場合があります。", appPrefs.StreamingType.Value == StreamingType.Rtmp);
            actions.AddCheckAction(2, "HLS", "HTTP Live Streaming形式を利用します。スマートフォン向けの配信形式で遅延が大き目です", appPrefs.StreamingType.Value == StreamingType.Hls);
        }

        public override void OnGuidedActionClicked(GuidedAction action)
        {
            foreach (var a in this.Actions)
            {
                a.Checked = (a == action);
            }

            var appPrefs = ApplicationMain.ServiceLocator.GetInstance<ApplicationPreference>();
            switch (action.Id)
            {
                case 1: appPrefs.StreamingType.Value = StreamingType.Rtmp; break;
                case 2: appPrefs.StreamingType.Value = StreamingType.Hls; break;
            }
            this.FragmentManager.PopBackStack();
        }
    }

    /// <summary>
    /// 設定: プレイヤー設定のGuidedStepクラスです。
    /// </summary>
    public class PlayerSettingGuidedStepFragment : GuidedStepFragment
    {
        public override GuidanceStylist.Guidance OnCreateGuidance(Bundle savedInstanceState)
        {
            return new GuidanceStylist.Guidance("プレイヤー", "動画のプレイヤーを設定します", "設定", null);
        }

        public override void OnCreateActions(IList<GuidedAction> actions, Bundle savedInstanceState)
        {
            var appPrefs = ApplicationMain.ServiceLocator.GetInstance<ApplicationPreference>();
            actions.AddCheckAction(1, "ExoPlayer", "ExoPlayerを利用します。このプレイヤーではRTMPを利用可能です。", appPrefs.PlayerType.Value == PlayerType.ExoPlayer);
            actions.AddCheckAction(2, "MediaPlayer", "Android標準のMediaPlayerを利用します。このプレイヤーではRTMPを利用できません。", appPrefs.PlayerType.Value == PlayerType.AndroidDefault);
            actions.AddCheckAction(3, "WebView", "WebViewを利用します。このプレイヤーではRTMPを利用できません。", appPrefs.PlayerType.Value == PlayerType.WebView);
        }

        public override void OnGuidedActionClicked(GuidedAction action)
        {
            foreach (var a in this.Actions)
            {
                a.Checked = (a == action);
            }

            var appPrefs = ApplicationMain.ServiceLocator.GetInstance<ApplicationPreference>();
            switch (action.Id)
            {
                case 1: appPrefs.PlayerType.Value = PlayerType.ExoPlayer; break;
                case 2: appPrefs.PlayerType.Value = PlayerType.AndroidDefault; break;
                case 3: appPrefs.PlayerType.Value = PlayerType.WebView; break;
            }
            this.FragmentManager.PopBackStack();
        }
    }

    /// <summary>
    /// 設定: このアプリケーションについて
    /// </summary>
    public class AboutSettingGuidedStepFragment : GuidedStepFragment
    {
        public override GuidanceStylist.Guidance OnCreateGuidance(Bundle savedInstanceState)
        {
            return new GuidanceStylist.Guidance("AgqrPlayer for Android TVについて", "アプリケーションについての情報を表示します", "設定", null);
        }

        public override void OnCreateActions(IList<GuidedAction> actions, Bundle savedInstanceState)
        {
            var version = typeof(MainActivity).Assembly.GetName().Version;
            var buildDateTime = new DateTime(2000, 1, 1).Add(new TimeSpan(TimeSpan.TicksPerDay * version.Build + TimeSpan.TicksPerSecond * 2 * version.Revision)); // http://stackoverflow.com/questions/1600962/displaying-the-build-date
            var packageInfo = Application.Context.PackageManager.GetPackageInfo(Application.Context.PackageName, PackageInfoFlags.MetaData);

            var index = 0;
            actions.AddInfo(++index, "バージョン情報", $"{packageInfo.VersionName} (VersionCode {packageInfo.VersionCode})");
            actions.AddInfo(++index, "ビルド時刻", buildDateTime.ToString());
            actions.AddInfo(++index, "デバイス", $"{Build.Manufacturer} {Build.Model}");
            actions.AddInfo(++index, "Android OS", $"{Build.VERSION.Release} (API {Build.VERSION.Sdk})");
        }
    }
}