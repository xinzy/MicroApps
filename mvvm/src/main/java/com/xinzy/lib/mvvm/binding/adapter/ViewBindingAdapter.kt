package com.xinzy.mvvm.lib.kotlin.view.binding.adapter

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.xinzy.mvvm.lib.kotlin.view.binding.BindingAction
import com.xinzy.mvvm.lib.kotlin.view.binding.BindingFunction

@BindingAdapter("isVisible")
fun setVisible(view: View, isVisible: Boolean) {
    view.isVisible = isVisible
}

@BindingAdapter("isInvisible")
fun setInvisible(view: View, isInvisible: Boolean) {
    view.isInvisible = isInvisible
}

@BindingAdapter("isGone")
fun setGone(view: View, gone: Boolean) {
    view.isGone = gone
}

@BindingAdapter("onClickAction")
fun setOnClickListener(view: View, action: BindingAction) {
    view.setOnClickListener { action.call() }
}

@BindingAdapter("onLongClickAction")
fun setOnLongClickListener(view: View, function: BindingFunction<Boolean>) {
    view.setOnLongClickListener { function.call() }
}