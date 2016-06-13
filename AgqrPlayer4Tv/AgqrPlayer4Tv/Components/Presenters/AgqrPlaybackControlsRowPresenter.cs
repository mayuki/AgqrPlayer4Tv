using Android.Support.V17.Leanback.Widget;
using Java.Lang;

namespace AgqrPlayer4Tv.Components.Presenters
{
    class AgqrPlaybackControlsRowPresenter : PlaybackControlsRowPresenter
    {
        public AgqrPlaybackControlsRowPresenter(Presenter descriptionPresenter) : base(descriptionPresenter)
        {
        }

        protected override void OnBindRowViewHolder(RowPresenter.ViewHolder vh, Object item)
        {
            base.OnBindRowViewHolder(vh, item);
        }

        protected override void OnUnbindRowViewHolder(RowPresenter.ViewHolder vh)
        {
            base.OnUnbindRowViewHolder(vh);
        }
    }
}