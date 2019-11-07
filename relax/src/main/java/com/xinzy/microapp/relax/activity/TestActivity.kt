package com.xinzy.microapp.relax.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.xinzy.microapp.relax.fragment.showTimePicker
import com.xinzy.microapp.relax.R
import io.itimetraveler.widget.adapter.PickerAdapter
import io.itimetraveler.widget.model.StringItemView
import io.itimetraveler.widget.picker.PicketOptions
import io.itimetraveler.widget.picker.WheelPicker
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)


        val option = PicketOptions.Builder()
            .dividerColor(Color.RED)
            .backgroundColor(Color.WHITE)
            .dividedEqually(true)
            .build()
        picker.setOptions(option)
        picker.setAdapter(Adapter())

        picker.setSelection(0, 30)
        picker.setSelection(1, 5)

    }

    fun onShowPicker(v: View) {
        showTimePicker(supportFragmentManager)
    }

    inner class Adapter : PickerAdapter() {
        override fun numberOfComponentsInWheelPicker(wheelPicker: WheelPicker): Int {
            return 2
        }

        override fun numberOfRowsInComponent(component: Int): Int {
            return 60
        }

        override fun onBindView(parent: ViewGroup, convertView: View, row: Int, component: Int) {
            return StringItemView("${row + 1}").onBindView(parent, convertView, row)
        }

        override fun onCreateView(parent: ViewGroup, row: Int, component: Int): View {
            return StringItemView("${row + 1}").onCreateView(parent)
        }

        override fun labelOfComponent(component: Int): String {
            return if (component == 0) "分" else "秒"
        }
    }
}
