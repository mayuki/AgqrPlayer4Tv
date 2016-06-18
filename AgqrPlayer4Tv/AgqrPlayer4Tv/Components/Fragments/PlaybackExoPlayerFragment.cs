using System;
using System.Reactive;
using System.Reactive.Linq;
using System.Reactive.Subjects;
using AgqrPlayer4Tv.Components.Player;
using AgqrPlayer4Tv.Model.Platform;
using Android.OS;
using Android.Util;
using Android.Views;
using Com.Google.Android.Exoplayer;
using Reactive.Bindings;
using Reactive.Bindings.Extensions;

namespace AgqrPlayer4Tv.Components.Fragments
{
    public class PlaybackExoPlayerFragment : PlaybackPlayerFragmentBase, VideoPlayer.IListener
    {
        private const string Tag = "PlaybackExoPlayerFragment";

        private SurfaceView _surfaceView;
        private Subject<Tuple<bool, int>> _stateChanged = new Subject<Tuple<bool, int>>();
        private ReactiveProperty<VideoPlayer> _videoPlayer = new ReactiveProperty<VideoPlayer>();

        public IReadOnlyReactiveProperty<VideoPlayer> VideoPlayer { get { return this._videoPlayer; } }

        public PlaybackExoPlayerFragment()
        {
            this.IsPlaying = this.VideoPlayer
                    .SelectMany(_ => _stateChanged.Select(x => Unit.Default))
                    .Where(x => this.VideoPlayer.Value != null)
                    .Select(x => this.VideoPlayer.Value.PlaybackState == ExoPlayer.StateReady && this.VideoPlayer.Value.PlayWhenReady)
                    .ToReadOnlyReactiveProperty(false)
                    .AddTo(this.LifetimeDisposable);
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            var view = inflater.Inflate(Resource.Layout.PlaybackExoPlayer, container, false);

            this._surfaceView = (SurfaceView)view.FindViewById(Resource.Id.SurfaceView);
            return view;
        }

        public override void Play()
        {
            Log.Debug(Tag, $"Thread: {System.Threading.Thread.CurrentThread.ManagedThreadId}");
            if (this._surfaceView == null) return;

            this.Stop();

            var useRtmp = ApplicationMain.ServiceLocator.GetInstance<ApplicationPreference>().StreamingType.Value == StreamingType.Rtmp;
            var url = useRtmp
                ? UrlRtmp
                : UrlHls;
            var rendererBuilder = useRtmp
                ? (VideoPlayer.IRendererBuilder)new ExtractorRendererBuilder(this._surfaceView.Context, "App/1.0", Android.Net.Uri.Parse(url))
                : (VideoPlayer.IRendererBuilder)new HlsRendererBuilder(this._surfaceView.Context, UserAgent, url);
            Android.Util.Log.Debug(Tag, "Play: " + url);

            this._videoPlayer.Value = new VideoPlayer(rendererBuilder);
            this._videoPlayer.Value.Surface = this._surfaceView.Holder.Surface;
            this._videoPlayer.Value.AddListener(this);
            this._videoPlayer.Value.Prepare();

            this._videoPlayer.Value.PlayWhenReady = true;
        }

        public override void Stop()
        {
            if (this._videoPlayer.Value == null) return;

            this._videoPlayer.Value.PlayWhenReady = false;
            this._videoPlayer.Value.BlockingClearSurface();
            this._videoPlayer.Value.Release();
            this._videoPlayer.Value = null;
        }

        #region VideoPlayer.IListener Implementation

        void VideoPlayer.IListener.OnStateChanged(bool playWhenReady, int playbackState)
        {
            this._stateChanged.OnNext(Tuple.Create(playWhenReady, playbackState));
        }

        void VideoPlayer.IListener.OnError(Java.Lang.Exception e)
        {
            this.ReportError();
        }

        void VideoPlayer.IListener.OnVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio)
        {
        }
        #endregion
    }
}