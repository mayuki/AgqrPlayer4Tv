package org.misuzilla.agqrplayer4tv.component.widget

import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ObjectAdapter
import androidx.leanback.widget.PresenterSelector

/**
 * 型を指定したArrayObjectAdapterクラスです。
 */
class TypedArrayObjectAdapter<T> : ObjectAdapter {
    private val _arrayObjectAdapter = ArrayObjectAdapter()

    init {
        _arrayObjectAdapter.registerObserver(object : DataObserver() {
            override fun onChanged() {
                this@TypedArrayObjectAdapter.notifyChanged()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                this@TypedArrayObjectAdapter.notifyItemRangeChanged(positionStart, itemCount)
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                this@TypedArrayObjectAdapter.notifyItemRangeInserted(positionStart, itemCount)
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                this@TypedArrayObjectAdapter.notifyItemRangeInserted(positionStart, itemCount)
            }
        })
    }

    constructor() : super() {}
    constructor(presenter: TypedPresenter<T>) : super(presenter) {}
    constructor(presenterSelector: PresenterSelector) : super(presenterSelector) {}

    override fun size(): Int { return _arrayObjectAdapter.size() }
    override fun get(position: Int): Any { return _arrayObjectAdapter.get(position) }

    fun clear(): Unit { _arrayObjectAdapter.clear() }
    fun add(item: T): Unit { _arrayObjectAdapter.add(item) }
    fun add(id: Int, item: T): Unit { _arrayObjectAdapter.add(id, item) }
    fun add(index: Int, item: Collection<T>) { _arrayObjectAdapter.addAll(index, item) }
    fun indexOf(item: T): Int { return _arrayObjectAdapter.indexOf(item) }
    fun notifyArrayItemRangeChanged(positionStart: Int, itemCount: Int): Unit { _arrayObjectAdapter.notifyArrayItemRangeChanged(positionStart, itemCount) }
    fun remove(item: T): Boolean { return _arrayObjectAdapter.remove(item) }
    fun removeItems(position: Int, count: Int): Int { return _arrayObjectAdapter.removeItems(position, count) }
    fun replace(position: Int, item: T): Unit { _arrayObjectAdapter.replace(position, item) }
    fun unmodifiableList(): List<T> { return _arrayObjectAdapter.unmodifiableList<T>() }
}
