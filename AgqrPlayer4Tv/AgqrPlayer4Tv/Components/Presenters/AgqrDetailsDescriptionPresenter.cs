using System;
using System.Reactive.Disposables;
using AgqrPlayer4Tv.Components.Fragments;
using Android.Support.V17.Leanback.Widget;
using Reactive.Bindings.Extensions;

namespace AgqrPlayer4Tv.Components.Presenters
{
    class AgqrDetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter
    {
        private CompositeDisposable _disposables = new CompositeDisposable();

        protected override void OnBindDescription(ViewHolder viewHolder, Java.Lang.Object objectValue)
        {
            var nowPlaying = ((PlaybackControlsRowViewModel)objectValue).NowPlaying;

            viewHolder.Title.Text = " ";
            viewHolder.Subtitle.Text = " ";
            viewHolder.Body.Text = " ";

            nowPlaying.Title.ObserveOnUIDispatcher().Subscribe(x => viewHolder.Title.Text = x).AddTo(this._disposables);
            nowPlaying.Subtitle.ObserveOnUIDispatcher().Subscribe(x => viewHolder.Subtitle.Text = x).AddTo(this._disposables);
            nowPlaying.Body.ObserveOnUIDispatcher().Subscribe(x => viewHolder.Body.Text = x).AddTo(this._disposables);
        }

        public override void OnUnbindViewHolder(Presenter.ViewHolder viewHolder)
        {
            this._disposables.Dispose();
            base.OnUnbindViewHolder(viewHolder);
        }
    }
}