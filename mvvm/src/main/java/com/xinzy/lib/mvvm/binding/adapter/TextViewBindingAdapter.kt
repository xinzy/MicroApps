package com.xinzy.mvvm.lib.kotlin.view.binding.adapter

import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

import androidx.databinding.BindingAdapter

import com.xinzy.mvvm.lib.kotlin.view.binding.BindingConsumer

@BindingAdapter("onTextChangedConsumer")
fun setTextChangeListener(editText: EditText, consumer: BindingConsumer<String>) {
    editText.addTextChangedListener(TextChange(consumer))
}

private class TextChange(private val consumer: BindingConsumer<String>) : TextWatcher {
    private val mHandler = Handler()

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        mHandler.removeCallbacksAndMessages(null)
        mHandler.postDelayed({ consumer.call(s.toString()) }, 300)
    }

    override fun afterTextChanged(s: Editable) {}
}
