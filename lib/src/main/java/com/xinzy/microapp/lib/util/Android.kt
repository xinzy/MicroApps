package com.xinzy.microapp.lib.util

import android.content.Context
import android.view.View
import android.widget.Toast


fun View.setPadding(h: Int, v: Int) = setPadding(h, v, h, v)

fun Context.toast(msgId: Int) = toast(getString(msgId))
fun Context.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

fun Context.dp2px(dp: Int) = dp2px(dp.toFloat())
fun Context.dp2px(dp: Float) = (resources.displayMetrics.density * dp + .5f).toInt()

fun View.dp2px(dp: Int) = dp2px(dp.toFloat())
fun View.dp2px(dp: Float) = (resources.displayMetrics.density * dp + .5f).toInt()