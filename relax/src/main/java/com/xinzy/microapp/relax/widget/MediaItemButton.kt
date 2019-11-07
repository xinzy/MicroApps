package com.xinzy.microapp.relax.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.xinzy.microapp.relax.R
import com.xinzy.microapp.relax.entity.Media
import kotlinx.android.synthetic.main.view_media_item_button.view.*

class MediaItemButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : LinearLayout(context, attrs, defStyleAttr) {

    var media: Media? = null
        set(value) {
            field = value

            field?.let {
                val icon = if (focused) it.selectedIcon else it.icon
                iconFont.setText(icon)
                nameText.text = it.name
                setBackgroundResource(it.backgroundColor)
            }
        }

    var focused: Boolean = false
        set(value) {
            field = value

            media?.let {
                val icon = if (value) it.selectedIcon else it.icon
                iconFont.setText(icon)
            }
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_media_item_button, this)
    }

}