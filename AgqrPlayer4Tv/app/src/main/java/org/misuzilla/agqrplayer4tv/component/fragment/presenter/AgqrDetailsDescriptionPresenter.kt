package org.misuzilla.agqrplayer4tv.component.fragment.presenter

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import androidx.leanback.widget.Presenter
import org.misuzilla.agqrplayer4tv.infrastracture.extension.addTo
import org.misuzilla.agqrplayer4tv.infrastracture.extension.observeOnUIThread
import org.misuzilla.agqrplayer4tv.component.fragment.PlaybackControlsRowViewModel
import rx.subscriptions.CompositeSubscription

class AgqrDetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {
    private val subscription = CompositeSubscription()

    override fun onBindDescription(viewHolder: ViewHolder, item: Any?) {
        val nowPlaying = (item as PlaybackControlsRowViewModel).nowPlaying

        viewHolder.apply {
            title.text = ""
            subtitle.text = ""
            body.text = ""
        }

        nowPlaying.apply {
            title.asObservable().subscribe({ viewHolder.title.text = it }).addTo(subscription)
            subtitle.asObservable().subscribe({ viewHolder.subtitle.text = it }).addTo(subscription)
            body.asObservable().subscribe({ viewHolder.body.text = it }).addTo(subscription)
        }
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder?) {
        subscription.unsubscribe()
        super.onUnbindViewHolder(viewHolder)
    }
}