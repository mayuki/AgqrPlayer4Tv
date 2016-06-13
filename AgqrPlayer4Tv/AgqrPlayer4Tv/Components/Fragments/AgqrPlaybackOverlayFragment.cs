using System;
using System.Collections.Generic;
using System.Linq;
using System.Reactive.Disposables;
using System.Reactive.Linq;
using AgqrPlayer4Tv.Activities;
using AgqrPlayer4Tv.Components.Presenters;
using AgqrPlayer4Tv.Infrastracture.Extensions;
using AgqrPlayer4Tv.Model;
using Android.Content;
using Android.OS;
using Android.Support.V17.Leanback.App;
using Android.Support.V17.Leanback.Widget;
using Reactive.Bindings.Extensions;

using LeanbackWidgetAction = Android.Support.V17.Leanback.Widget.Action;

namespace AgqrPlayer4Tv.Components.Fragments
{
    public class AgqrPlaybackOverlayFragment : PlaybackOverlayFragment
    {
        private ArrayObjectAdapter _rowsAdapter;
        protected CompositeDisposable LifetimeDisposables { get; } = new CompositeDisposable();

        public override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            this.BuildDetails();
        }

        private Dictionary<LeanbackWidgetAction, Action<LeanbackWidgetAction>> GetPrimaryActionDefinition()
        {
            var playbackPlayer = ((PlaybackPlayerFragmentBase)this.ParentFragment);
            return new Dictionary<LeanbackWidgetAction, Action<LeanbackWidgetAction>>
            {
                {
                    new PlaybackControlsRow.PlayPauseAction(this.Activity),
                    x =>
                    {
                        if (playbackPlayer.IsPlaying.Value)
                        {
                            playbackPlayer.Stop();
                        }
                        else
                        {
                            playbackPlayer.Play();
                        }
                    }
                },
            };
        }
        private Dictionary<LeanbackWidgetAction, Action<LeanbackWidgetAction>> GetSecondaryActionDefinition()
        {
            var playbackPlayer = ((PlaybackPlayerFragmentBase)this.ParentFragment);
            return new Dictionary<LeanbackWidgetAction, Action<LeanbackWidgetAction>>
            {
                {
                    new LeanbackWidgetAction(1, "Settings"),
                    x =>
                    {
                        playbackPlayer.Stop();
                        this.StartActivity(new Intent(this.Activity.ApplicationContext, typeof(SettingsActivity)));
                    }
                },
            };
        }

        private void BuildDetails()
        {
            var selector = new ClassPresenterSelector();
            var rowPresenter = new AgqrPlaybackControlsRowPresenter(new AgqrDetailsDescriptionPresenter());
            rowPresenter.SetSecondaryActionsHidden(true);
            rowPresenter.BackgroundColor = Resources.GetColor(Resource.Color.AccentDark);
            rowPresenter.ProgressColor = Resources.GetColor(Resource.Color.Accent);
            selector.AddClassPresenter<PlaybackControlsRow>(rowPresenter);
            selector.AddClassPresenter<ListRow>(new ListRowPresenter());
            this._rowsAdapter = new ArrayObjectAdapter(selector);

            var nowPlaying = ApplicationMain.ServiceLocator.GetInstance<NowPlaying>();
            var playbackPlayer = ((PlaybackPlayerFragmentBase)this.ParentFragment);

            var playbackControlsRow = new PlaybackControlsRow(new PlaybackControlsRowViewModel(playbackPlayer, nowPlaying));
            var controlPresenterSelector = new ControlButtonPresenterSelector();
            var primaryActions = new ArrayObjectAdapter(controlPresenterSelector);
            var primaryActionsDefinition = this.GetPrimaryActionDefinition();
            var secondaryActions = new ArrayObjectAdapter(controlPresenterSelector);
            var secondaryActionsDefinition = this.GetSecondaryActionDefinition();
            playbackControlsRow.PrimaryActionsAdapter = primaryActions;
            playbackControlsRow.SecondaryActionsAdapter = secondaryActions;
            primaryActions.AddAll(0, primaryActionsDefinition.Keys);
            secondaryActions.AddAll(0, secondaryActionsDefinition.Keys);
            this._rowsAdapter.Add(playbackControlsRow);

            Observable.FromEventPattern<ActionClickedEventArgs>(h => rowPresenter.ActionClicked += h, h => rowPresenter.ActionClicked -= h)
                .Select(x => x.EventArgs.Action)
                .Select(x => new { Action = primaryActionsDefinition.ContainsKey(x) ? primaryActionsDefinition[x] : secondaryActionsDefinition[x], Target = x })
                .Subscribe(x => x.Action(x.Target))
                .AddTo(this.LifetimeDisposables);

            playbackPlayer.IsPlaying
                .ObserveOnUIDispatcher()
                .Subscribe(x =>
                {
                    var playPauseAction = primaryActionsDefinition.Select(y => y.Key).OfType<PlaybackControlsRow.PlayPauseAction>().Single();
                    playPauseAction.Index = x
                        ? PlaybackControlsRow.PlayPauseAction.Pause
                        : PlaybackControlsRow.PlayPauseAction.Play;
                    this._rowsAdapter.NotifyArrayItemRangeChanged(0, 1);
                })
                .AddTo(this.LifetimeDisposables);

            playbackPlayer.ElapsedSeconds
                .ObserveOnUIDispatcher()
                .Subscribe(x =>
                {
                    playbackControlsRow.CurrentTime = x*1000;
                    //this._rowsAdapter.NotifyArrayItemRangeChanged(0, 1); // ‚±‚Á‚¿‚Í’Ê’m‚µ‚Ä‚Íƒ_ƒ(ƒKƒRƒKƒR‚·‚é
                })
                .AddTo(this.LifetimeDisposables);

            nowPlaying.Program
                .Where(x => x != null)
                .ObserveOnUIDispatcher()
                .Subscribe(x =>
                {
                    playbackControlsRow.TotalTime = (int) (x.End - x.Start).TotalMilliseconds;
                    this._rowsAdapter.NotifyArrayItemRangeChanged(0, 1);
                })
                .AddTo(this.LifetimeDisposables);

            this.Adapter = this._rowsAdapter;
        }

        public override void OnDestroy()
        {
            this.LifetimeDisposables.Dispose();
            base.OnDestroy();
        }
    }

    public class PlaybackControlsRowViewModel : Java.Lang.Object
    {
        public PlaybackPlayerFragmentBase PlaybackPlayer { get; private set; }
        public NowPlaying NowPlaying { get; private set; }
        public PlaybackControlsRowViewModel(PlaybackPlayerFragmentBase playbackPlayer, NowPlaying nowPlaying)
        {
            this.PlaybackPlayer = playbackPlayer;
            this.NowPlaying = nowPlaying;
        }
    }
}