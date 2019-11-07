package com.xinzy.microapp.relax.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.xinzy.microapp.relax.R
import io.itimetraveler.widget.adapter.PickerAdapter
import io.itimetraveler.widget.model.StringItemView
import io.itimetraveler.widget.picker.PicketOptions
import io.itimetraveler.widget.picker.WheelPicker
import kotlinx.android.synthetic.main.fragment_time_picker.*

private val minutes = Array(60) { it + 1 }
private val seconds = Array(60) { it }

private const val TAG = "TimePickerDialog"

typealias OnSelectedTimeCallback = (Int) -> Unit

fun showTimePicker(manager: FragmentManager, callback: OnSelectedTimeCallback? = null) {
    TimePickerDialog().apply { this.callback = callback }.show(manager)
}

class TimePickerDialog : DialogFragment(), View.OnClickListener {

    var callback: OnSelectedTimeCallback? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.let {
                it.decorView.setPadding(0, 0, 0, 0)
                it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val lp = it.attributes
                lp.width = WindowManager.LayoutParams.MATCH_PARENT
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                lp.gravity = Gravity.BOTTOM

                it.attributes = lp
                it.setWindowAnimations(R.style.Animation_Picker)
            }
        }

        return inflater.inflate(R.layout.fragment_time_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cancelBtn.setOnClickListener(this)
        okBtn.setOnClickListener(this)

        val option = PicketOptions.Builder()
            .dividerColor(0xFFF0F0F0.toInt())
            .dividedEqually(true)
            .build()
        wheelPicker.setOptions(option)
        wheelPicker.setAdapter(TimePickerAdapter())

        wheelPicker.setSelection(0, 29)
    }

    override fun onClick(v: View) {
        when (v) {
            okBtn -> {
                val positions = wheelPicker.selectedPositions
                val second = minutes[positions[0]] * 60 + seconds[positions[1]]
                callback?.let { it(second) }
                dismiss()
            }
            cancelBtn -> dismiss()
        }
    }

    fun show(manager: FragmentManager) {
        try {
            super.show(manager, TAG)
        } catch (e: Exception) {  }
    }

    override fun dismiss() {
        try {
            super.dismiss()
        } catch (e: Exception) { }
    }
}

class TimePickerAdapter : PickerAdapter() {
    override fun numberOfComponentsInWheelPicker(wheelPicker: WheelPicker) = 2

    override fun numberOfRowsInComponent(component: Int) = if (component == 0) minutes.size else seconds.size

    override fun onBindView(parent: ViewGroup?, convertView: View?, row: Int, component: Int) {
        if (component == 0) StringItemView(minutes[row].toString()).onBindView(parent, convertView, row)
        else StringItemView(seconds[row].toString()).onBindView(parent, convertView, row)
    }

    override fun onCreateView(parent: ViewGroup?, row: Int, component: Int): View =
        if (component == 0) StringItemView(minutes[row].toString()).onCreateView(parent)
        else StringItemView(seconds[row].toString()).onCreateView(parent)

    override fun labelOfComponent(component: Int): String = if (component == 0) "分" else "秒"
}