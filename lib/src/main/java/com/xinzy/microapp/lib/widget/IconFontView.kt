package com.xinzy.microapp.lib.widget

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView
import com.xinzy.microapp.lib.R

class IconFontView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : TextView(context, attrs, defStyleAttr)  {

    init {
        gravity = Gravity.CENTER
        val ta = context.obtainStyledAttributes(attrs, R.styleable.IconFontView, defStyleAttr, 0)
        val path = ta.getString(R.styleable.IconFontView_fontPath) ?: ""
        ta.recycle()

        if (path.isNotEmpty()) {
            typeface = Typeface.createFromAsset(context.assets, path)
        }
    }
}