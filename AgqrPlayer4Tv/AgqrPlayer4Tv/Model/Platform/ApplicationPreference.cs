using System;
using AgqrPlayer4Tv.Infrastracture;
using Android.App;
using Android.Content;
using Reactive.Bindings;
using Reactive.Bindings.Extensions;

namespace AgqrPlayer4Tv.Model.Platform
{
    public class ApplicationPreference : DisposableModelBase
    {
        public ReactiveProperty<PlayerType> PlayerType { get; } = new ReactiveProperty<PlayerType>((PlayerType)SharedPreferences.GetInt("PlayerType", (int)Platform.PlayerType.ExoPlayer));
        public ReactiveProperty<StreamingType> StreamingType { get; } = new ReactiveProperty<StreamingType>((StreamingType)SharedPreferences.GetInt("StreamingType", (int)Platform.StreamingType.Hls));
        public ReactiveProperty<bool> IsLastShutdownCorrectly { get; } = new ReactiveProperty<bool>(SharedPreferences.GetBoolean("IsLastShutdownCorrectly", true));

        private static ISharedPreferences SharedPreferences
        {
            get { return Application.Context.GetSharedPreferences("Preferences", FileCreationMode.Private); }
        }

        public ApplicationPreference()
        {
            this.PlayerType.Subscribe(x => SharedPreferences.Edit().PutInt("PlayerType", (int)x).Commit()).AddTo(this);
            this.StreamingType.Subscribe(x => SharedPreferences.Edit().PutInt("StreamingType", (int)x).Commit()).AddTo(this);
            this.IsLastShutdownCorrectly.Subscribe(x => SharedPreferences.Edit().PutBoolean("IsLastShutdownCorrectly", x).Commit()).AddTo(this);
        }
    }

    public enum PlayerType
    {
        ExoPlayer = 0,
        AndroidDefault = 1,
        WebView = 2,
    }

    public enum StreamingType
    {
        Rtmp = 0,
        Hls = 1,
    }

}