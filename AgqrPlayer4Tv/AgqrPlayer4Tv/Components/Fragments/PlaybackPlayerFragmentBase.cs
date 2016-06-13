using System;
using System.Reactive;
using System.Reactive.Disposables;
using System.Reactive.Linq;
using System.Reactive.Subjects;
using AgqrPlayer4Tv.Activities;
using AgqrPlayer4Tv.Model;
using Android.App;
using Android.Content;
using Android.Util;
using Android.Widget;
using Reactive.Bindings;
using Reactive.Bindings.Extensions;

namespace AgqrPlayer4Tv.Components.Fragments
{
    public abstract class PlaybackPlayerFragmentBase : Fragment
    {
        protected const string UrlHls = "http://ic-www.uniqueradio.jp/hls/m3u8.php";
        protected const string UrlRtmp = "rtmp://fms-base1.mitene.ad.jp:1935/agqr/aandg22 live=1";
        protected const string UserAgent = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.23 Mobile Safari/537.36";

        private ISubject<Unit> _onError = new Subject<Unit>();

        protected CompositeDisposable LifetimeDisposable { get; } = new CompositeDisposable();

        public IReadOnlyReactiveProperty<bool> IsPlaying { get; protected set; }
        public IReadOnlyReactiveProperty<int> Progress { get; protected set; }
        public IReadOnlyReactiveProperty<int> ElapsedSeconds { get; protected set; }

        public PlaybackPlayerFragmentBase()
        {
            var refreshInterval = Observable.Interval(TimeSpan.FromSeconds(1))
                .Select(_ => ApplicationMain.ServiceLocator.GetInstance<NowPlaying>().Program.Value)
                .Publish()
                .RefCount();

            this.ElapsedSeconds = refreshInterval
                .Select(x =>
                {
                    if (x == null)
                    {
                        return 0;
                    }
                    else
                    {
                        return (int)(LogicalDateTime.Now.Time - x.Start).TotalSeconds;
                    }
                })
                .ToReadOnlyReactiveProperty(0)
                .AddTo(this.LifetimeDisposable);

            this.Progress = refreshInterval
                .Select(x =>
                {
                    if (x == null)
                    {
                        return 0;
                    }
                    else
                    {
                        var duration = x.End - x.Start;
                        var elapsed = LogicalDateTime.Now.Time - x.Start;

                        var percent = (int)(elapsed.TotalSeconds / duration.TotalSeconds * 100);
                        return Math.Max(0, Math.Min(100, percent));
                    }
                })
                .ToReadOnlyReactiveProperty(0)
                .AddTo(this.LifetimeDisposable);

            this.ObserveError();
       }

        private void ObserveError()
        {
            // 1秒を置いてリトライ、リトライ回数は30秒間に最大5回。
            this._onError
                .Delay(TimeSpan.FromSeconds(1))
                .Do(x => Log.Debug(Tag, "ERROR!"))
                .TakeUntil(this._onError.Buffer(TimeSpan.FromSeconds(30), 5).Where(x => x.Count >= 5).Do(x => Log.Debug(Tag, "Buffer!")))
                .ObserveOnUIDispatcher()
                .Subscribe(x => this.Play(), ex => Log.Error(Tag, ex.ToString()), () => this.OnRetryThresholdReached())
                .AddTo(this.LifetimeDisposable);
        }

        private void OnRetryThresholdReached()
        {
            Log.Debug(Tag, "OnRetryThresholdReached");
            this.StartActivity(new Intent(this.Activity.ApplicationContext, typeof(ErrorActivity)));
        }

        public override void OnPause()
        {
            base.OnPause();

            this.Stop();
        }

        public override void OnResume()
        {
            base.OnResume();

            this.Play();
        }

        public override void OnDetach()
        {
            this.Stop();

            this.LifetimeDisposable.Dispose();
            base.OnDetach();
        }

        protected void ReportError()
        {
            this._onError.OnNext(Unit.Default);
        }

        public abstract void Play();
        public abstract void Stop();
    }
}