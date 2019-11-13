package com.xinzy.lib.mvvm.binding.adapter

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter

//@BindingAdapter(value = ["imageUrl", "placeholder", "error"], requireAll = false)
//fun loadImage(view: ImageView, url: String, placeholder: Drawable?, error: Drawable?) {
//    val builder = Glide.with(view).load(url)
//    placeholder?.let { builder.placeholder(it) }
//    error?.let { builder.error(error) }
//    builder.into(view)
//}