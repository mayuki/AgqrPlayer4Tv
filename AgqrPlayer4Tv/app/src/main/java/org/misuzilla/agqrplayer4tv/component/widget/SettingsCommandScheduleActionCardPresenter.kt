package org.misuzilla.agqrplayer4tv.component.widget

import android.support.v17.leanback.widget.ImageCardView
import android.view.ViewGroup
import android.widget.ImageView
import org.misuzilla.agqrplayer4tv.component.widget.SettingsCommandScheduleAction
import org.misuzilla.agqrplayer4tv.component.widget.TypedViewPresenter
import org.misuzilla.agqrplayer4tv.infrastracture.extension.addTo
import org.misuzilla.agqrplayer4tv.model.Reservation
import rx.Observable
import rx.subscriptions.CompositeSubscription

/**
 * 設定: 予約 のカードを表示するためのPresenterクラスです。
 */
class SettingsCommandScheduleActionCardPresenter : TypedViewPresenter<ImageCardView, SettingsCommandScheduleAction>() {
    val subscriptions = CompositeSubscription()
    override fun onBindViewHolderWithItem(viewHolder: ViewHolder, view: ImageCardView, item: SettingsCommandScheduleAction) {
        view.apply {
            setMainImageDimensions(320, 256)
            setMainImageScaleType(ImageView.ScaleType.CENTER)
            titleText = item.label1
            mainImage = item.icon
            item.label2?.let { contentText = it }
        }

        Observable.just(Reservation.instance)
                .mergeWith(Reservation.instance.onChangeAsObservable)
                .map { Reservation.instance.scheduledCount }
                .subscribe {
                    view.titleText = item.label1
                    view.contentText = String.format(item.label2.toString(), it)
                }
                .addTo(subscriptions)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder, view: ImageCardView) {
        subscriptions.clear()
    }

    override fun onCreateView(parent: ViewGroup): ImageCardView {
        return ImageCardView(parent.context)
    }
}
