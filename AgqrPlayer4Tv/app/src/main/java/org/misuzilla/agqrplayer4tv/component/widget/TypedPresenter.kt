package org.misuzilla.agqrplayer4tv.component.widget

import android.support.v17.leanback.widget.Presenter

/**
 * 表示すべき値の型を指定したPresenterクラスです。
 */
abstract class TypedPresenter<TValue> : Presenter() {
    final override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        onBindViewHolderWithItem(viewHolder, item as TValue)
    }

    abstract fun onBindViewHolderWithItem(viewHolder: ViewHolder, item: TValue)
}