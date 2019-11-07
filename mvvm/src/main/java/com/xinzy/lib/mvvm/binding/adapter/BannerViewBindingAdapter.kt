package com.xinzy.mvvm.lib.kotlin.view.binding.adapter

import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import com.xinzy.mvvm.lib.kotlin.BR
import com.xinzy.mvvm.lib.kotlin.view.widget.BannerView

@BindingAdapter("autoStart")
fun setAutoFlip(view: BannerView, auto: Boolean) {
    view.setAutoStart(auto)
}

@BindingAdapter("onItemClickListener")
fun setOnItemClickListener(banner: BannerView, listener: BannerView.OnItemClickListener) {
    banner.setOnItemClickListener(listener)
}

@BindingAdapter("onItemSelectedListener")
fun setOnItemSelectedListener(banner: BannerView, l: BannerView.OnItemSelectedListener) {
    banner.setOnItemSelectedListener(l)
}

@BindingAdapter("adapter")
fun setAdapter(banner: BannerView, adapter: BannerView.Adapter) {
    banner.setAdapter(adapter)
}

@BindingAdapter(value = ["data", "layoutId"])
fun setAdater(banner: BannerView, data: List<Any>, layoutId: Int) {
    banner.setAdapter(object : BannerView.Adapter {
        override fun getLayout() = layoutId

        override fun getCount() = data.size

        override fun getItem(position: Int): Any {
            return data[position]
        }

        override fun convert(dataBinding: ViewDataBinding, position: Int) {
            val item = data[position]
            dataBinding.setVariable(BR.item, item)
            dataBinding.setVariable(BR.position, position)
        }
    })
}