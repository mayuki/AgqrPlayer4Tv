using System;
using System.Collections.Generic;
using System.Reactive;
using System.Reactive.Linq;
using System.Reactive.Subjects;
using Android.Media;
using Android.OS;
using Android.Util;
using Android.Views;
using Android.Widget;
using Reactive.Bindings;
using Reactive.Bindings.Extensions;

namespace AgqrPlayer4Tv.Components.Fragments
{
    public class PlaybackDefaultVideoViewFragment : PlaybackPlayerFragmentBase
    {
        private const string Tag = "PlaybackDefaultVideoViewFragment";

        private ISubject<Unit> _updateIsPlaying = new Subject<Unit>();

        private ReactiveProperty<VideoView> _videoView = new ReactiveProperty<VideoView>();
        public IReadOnlyReactiveProperty<VideoView> VideoView { get { return this._videoView; } }

        public PlaybackDefaultVideoViewFragment()
        {
            this.IsPlaying = this.VideoView
                .Where(x => x != null)
                .SelectMany(_ =>
                    Observable.Merge(
                        _updateIsPlaying,
                        Observable.FromEventPattern<MediaPlayer.InfoEventArgs>(h => this.VideoView.Value.Info += h, h => this.VideoView.Value.Info -= h).Select(x => Unit.Default),
                        Observable.FromEventPattern<MediaPlayer.ErrorEventArgs>(h => this.VideoView.Value.Error += h, h => this.VideoView.Value.Error -= h).Select(x => Unit.Default),
                        Observable.FromEventPattern(h => this.VideoView.Value.Prepared += h, h => this.VideoView.Value.Prepared -= h).Select(x => Unit.Default),
                        Observable.FromEventPattern(h => this.VideoView.Value.Completion += h, h => this.VideoView.Value.Completion -= h).Select(x => Unit.Default)
                    )
                )
                .Select(x => this.VideoView.Value.IsPlaying)
                .ToReadOnlyReactiveProperty(false)
                .AddTo(this.LifetimeDisposable);
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            var view = inflater.Inflate(Resource.Layout.Playback, container, false);
            this._videoView.Value = view.FindViewById<VideoView>(Resource.Id.VideoView);

            this._videoView
                .Where(x => x != null)
                .Select(x => Observable.FromEventPattern<MediaPlayer.ErrorEventArgs>(h => x.Error += h, h => x.Error -= h))
                .Switch()
                .Do(x =>
                {
                    Log.Debug(Tag, "Error");
                    x.EventArgs.Handled = true;
                })
                .Subscribe(x =>
                {
                    this.ReportError();
                })
                .AddTo(this.LifetimeDisposable);

            return view;
        }

        public override void Play()
        {
            if (this.VideoView.Value == null) return;

            Android.Util.Log.Debug(Tag, "Play");
            this._videoView.Value.StopPlayback();
            this._videoView.Value.SetVideoURI(Android.Net.Uri.Parse(UrlHls), new Dictionary<string, string>()
                {
                    { "User-Agent", UserAgent }
                });
            this._videoView.Value.Start();
        }

        public override void Stop()
        {
            if (this.VideoView.Value == null || !this.IsPlaying.Value) return;

            this._videoView.Value.StopPlayback();
            this._updateIsPlaying.OnNext(Unit.Default);
        }
    }
}