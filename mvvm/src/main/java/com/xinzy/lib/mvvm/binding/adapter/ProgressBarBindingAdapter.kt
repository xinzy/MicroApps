package com.xinzy.mvvm.lib.kotlin.view.binding.adapter

import android.widget.ProgressBar

import androidx.databinding.BindingAdapter

@BindingAdapter("android:progress")
fun setProgress(bar: ProgressBar, progress: Int) {
    bar.progress = progress
}
