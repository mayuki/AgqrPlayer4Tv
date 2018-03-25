package org.misuzilla.agqrplayer4tv.component.widget

import android.support.v17.leanback.widget.ImageCardView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

/**
 * CommandActionをカード状に表示するPresenterクラスです。
 */
class CommandActionCardPresenter : TypedViewPresenter<ImageCardView, CommandAction>() {
    override fun onBindViewHolderWithItem(viewHolder: ViewHolder, view: ImageCardView, item: CommandAction) {
        view.apply {
            setMainImageDimensions(320, 256)
            setMainImageScaleType(ImageView.ScaleType.CENTER)
            titleText = item.label1
            mainImage = item.icon
            item.label2?.let { contentText = it }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder, view: ImageCardView) {
    }

    override fun onCreateView(parent: ViewGroup): ImageCardView {
        return ImageCardView(parent.context)
    }
}