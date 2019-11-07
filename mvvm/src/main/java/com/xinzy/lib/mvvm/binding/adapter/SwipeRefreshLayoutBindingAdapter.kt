package com.xinzy.mvvm.lib.kotlin.view.binding.adapter

import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.xinzy.mvvm.lib.kotlin.view.binding.BindingAction

@BindingAdapter("isRefreshing")
fun showRefresh(layout: SwipeRefreshLayout, refresh: Boolean) {
    layout.isRefreshing = refresh
}

@BindingAdapter("onRefreshAction")
fun setOnRefreshListener(layout: SwipeRefreshLayout, action: BindingAction) {
    layout.setOnRefreshListener { action.call() }
}
