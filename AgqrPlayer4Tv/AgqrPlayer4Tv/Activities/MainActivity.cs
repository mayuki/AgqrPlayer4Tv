using System;
using AgqrPlayer4Tv.Components.Fragments;
using AgqrPlayer4Tv.Model.Platform;
using AgqrPlayer4Tv.Services;
using Android.App;
using Android.Content;
using Android.Content.PM;
using Android.OS;
using Android.Runtime;
using Android.Views;
using HockeyApp;
using Reactive.Bindings;

namespace AgqrPlayer4Tv.Activities
{
    [Activity(Label = "@string/ApplicationName", MainLauncher = true, Icon = "@drawable/Icon", LaunchMode = LaunchMode.SingleTop)]
    [IntentFilter(new[] { Intent.ActionMain }, Categories = new[] { Intent.CategoryLeanbackLauncher })]
    public class MainActivity : Activity
    {
        private PlaybackPlayerFragmentBase _playerFragment;

        protected override void OnCreate(Bundle bundle)
        {
            CrashManager.Register(this);

            BootupActivity.ScheduleRecommendationUpdate(ApplicationContext);
            StartService(new Intent(this, typeof(UpdateRecommendationService)));

            // DayDream(スクリーンセーバー)を抑制する
            this.Window.SetFlags(WindowManagerFlags.KeepScreenOn, WindowManagerFlags.KeepScreenOn);

            SetContentView(Resource.Layout.Main);

            UIDispatcherScheduler.Initialize();

            base.OnCreate(bundle);
        }

        protected override void OnResume()
        {
            // プレイヤー設定が変わってるかもしれないので毎度作り直す
            this.SetupPlayerFragment();

            base.OnResume();
        }


        protected override void OnPause()
        {
            ApplicationMain.ServiceLocator.GetInstance<ApplicationPreference>().IsLastShutdownCorrectly.Value = true; // 正常シャットダウンフラグを立てる

            base.OnPause();
        }

        protected override void OnDestroy()
        {
            base.OnDestroy();
        }

        private void SetupPlayerFragment()
        {
            var appPrefs = ApplicationMain.ServiceLocator.GetInstance<ApplicationPreference>();

            var transaction = this.FragmentManager.BeginTransaction();
            var playerType = appPrefs.PlayerType.Value;

            // RTMPは突然の死を迎えることがある…
            if (!appPrefs.IsLastShutdownCorrectly.Value && appPrefs.StreamingType.Value == StreamingType.Rtmp)
            {
                appPrefs.StreamingType.Value = StreamingType.Hls;
            }

            var newFragment = (playerType == PlayerType.ExoPlayer)
                ? (PlaybackPlayerFragmentBase)new PlaybackExoPlayerFragment()
                    : (playerType == PlayerType.AndroidDefault)
                        ? (PlaybackPlayerFragmentBase)new PlaybackDefaultVideoViewFragment()
                        : (PlaybackPlayerFragmentBase)new PlaybackWebViewPlayerFragment();

            if (this._playerFragment != null)
            {
                transaction.Replace(Resource.Id.MainFrame, newFragment, newFragment.GetType().Name);
            }
            else
            {
                transaction.Add(Resource.Id.MainFrame, newFragment, newFragment.GetType().Name);
            }
            transaction.Commit();

            this._playerFragment = newFragment;
            this._playerFragment.Play();

            ApplicationMain.ServiceLocator.GetInstance<ApplicationPreference>().IsLastShutdownCorrectly.Value = false; // 正常シャットダウンフラグを折っておく
        }

    }
}

