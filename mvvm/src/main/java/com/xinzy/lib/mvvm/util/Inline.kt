package com.xinzy.lib.mvvm.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


fun RecyclerView.edge(
    endAction: (view: RecyclerView) -> Unit = {},
    startAction: (view: RecyclerView) -> Unit = {}
) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState != RecyclerView.SCROLL_STATE_IDLE) return

            val layoutManager = recyclerView.layoutManager
            if (layoutManager !is LinearLayoutManager) return

            if (layoutManager.orientation == RecyclerView.VERTICAL) {
                if (!recyclerView.canScrollVertically(1)) {
                    endAction(recyclerView)
                } else if (!recyclerView.canScrollVertically(-1)) {
                    startAction(recyclerView)
                }
            } else {
                if (!recyclerView.canScrollHorizontally(1)) {
                    endAction(recyclerView)
                } else if (!recyclerView.canScrollHorizontally(-1)) {
                    startAction(recyclerView)
                }
            }
        }
    })
}