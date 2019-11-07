package com.xinzy.microapp.lib.widget


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import androidx.core.view.setPadding
import com.xinzy.microapp.lib.R
import com.xinzy.microapp.lib.util.dp2px

class ProgressDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(initView())

        this.window?.setBackgroundDrawable(ColorDrawable(0))
        setCanceledOnTouchOutside(false)
    }

    private fun initView(): View {
        val container = LinearLayout(context).apply {
            gravity = Gravity.CENTER
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(dp2px(72f), dp2px(72f))
            setPadding(dp2px(20))

            background = GradientDrawable().apply {
                setColor(context.getColor(R.color.translucent))
                cornerRadius = context.dp2px(4f).toFloat()
            }
        }

        val loadingView = LoadingView(context).apply {
            setColor(Color.WHITE)
        }
        container.addView(loadingView, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))

        return container
    }

    override fun show() {
        try {
            super.show()
        } catch (e: Exception) { }
    }

    override fun dismiss() {
        try {
            super.dismiss()
        } catch (e: Exception) { }
    }
}