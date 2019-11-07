@file:JvmName("LayoutManagers")

package com.xinzy.mvvm.lib.kotlin.view.binding.adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.xinzy.mvvm.lib.kotlin.util.edge
import com.xinzy.mvvm.lib.kotlin.view.binding.BindingAction
import com.xinzy.mvvm.lib.kotlin.view.widget.MultiAdapter
import com.xinzy.mvvm.lib.kotlin.view.widget.OnItemClickListener

@BindingAdapter(value = ["layoutManager", "adapter", "onItemClickListener"], requireAll = false)
fun setAdapter(view: RecyclerView, factory: LayoutManagerFactory?, adapter: MultiAdapter?,
               onItemClickListener: OnItemClickListener?) {

    factory?.let { view.layoutManager = it.create(view) }
    view.adapter = adapter
    adapter?.setOnItemClickListener(onItemClickListener)
}

@BindingAdapter("data")
fun setData(view: RecyclerView, data: List<Any>?) {
    data?.let {
        view.post {
            (view.adapter as? MultiAdapter)?.replace(data)
        }
    }
}

@BindingAdapter(value = ["onScrollToStartAction", "onScrollToEndAction"], requireAll = false)
fun setOnScrollToEdge(view: RecyclerView, startAction: BindingAction?, endAction: BindingAction?) {
    view.edge(endAction = { endAction?.call() }, startAction = { startAction?.call() })
}

@JvmOverloads fun linear(@RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL, reverse: Boolean = false): LayoutManagerFactory {
    return object : LayoutManagerFactory {
        override fun create(view: RecyclerView): RecyclerView.LayoutManager = LinearLayoutManager(view.context, orientation, reverse)
    }
}

@JvmOverloads fun grid(spanCount: Int, @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL, reverse: Boolean = false): LayoutManagerFactory {
    return object : LayoutManagerFactory {
        override fun create(view: RecyclerView): RecyclerView.LayoutManager = GridLayoutManager(view.context, spanCount, orientation, reverse)
    }
}

@JvmOverloads fun flexbox(@FlexDirection direction: Int = FlexDirection.ROW, @FlexWrap wrap: Int = FlexWrap.WRAP, @AlignItems alignItems: Int = AlignItems.CENTER): LayoutManagerFactory {
    return object : LayoutManagerFactory {
        override fun create(view: RecyclerView) = FlexboxLayoutManager(view.context, direction, wrap).apply { this.alignItems = alignItems }
    }
}

interface LayoutManagerFactory {
    fun create(view: RecyclerView): RecyclerView.LayoutManager
}