package org.misuzilla.agqrplayer4tv.component.widget

import android.view.View
import android.view.ViewGroup

/**
 * 表示する値とViewの型を指定したPresenterクラスです。
 */
abstract class TypedViewPresenter<TView, TValue> : TypedPresenter<TValue>() where TView: View {
    final override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        onUnbindViewHolder(viewHolder, viewHolder.view as TView)
    }
    final override fun onBindViewHolderWithItem(viewHolder: ViewHolder, item: TValue) {
        return onBindViewHolderWithItem(viewHolder, viewHolder.view as TView, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(onCreateView(parent))
    }

    abstract fun onBindViewHolderWithItem(viewHolder: ViewHolder, view: TView, item: TValue)
    abstract fun onUnbindViewHolder(viewHolder: ViewHolder, view: TView)
    abstract fun onCreateView(parent: ViewGroup): TView
}