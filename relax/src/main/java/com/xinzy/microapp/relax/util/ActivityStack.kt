package com.xinzy.microapp.relax.util

import android.app.Activity
import java.lang.ref.WeakReference


typealias OnApplicationStatusChangedCallback = (Boolean) -> Unit

private val onBackgroundCallbacks = mutableListOf<WeakReference<OnApplicationStatusChangedCallback>>()
private val onForegroundCallbacks = mutableListOf<WeakReference<OnApplicationStatusChangedCallback>>()

private val activities = mutableListOf<Activity>()

fun addOnForegroundCallback(callback: OnApplicationStatusChangedCallback) {
    onForegroundCallbacks.add(WeakReference(callback))
}

fun addOnBackgroundCallback(callback: OnApplicationStatusChangedCallback) {
    onBackgroundCallbacks.add(WeakReference(callback))
}

fun removeOnForegroundCallback(callback: OnApplicationStatusChangedCallback) {
    val iterator = onForegroundCallbacks.iterator()
    while (iterator.hasNext()) {
        val ele = iterator.next()
        if (ele.get() == null || ele.get() == callback) {
            iterator.remove()
        }
    }
}

fun removeOnBackgroundCallback(callback: OnApplicationStatusChangedCallback) {
    val iterator = onBackgroundCallbacks.iterator()
    while (iterator.hasNext()) {
        val ele = iterator.next()
        if (ele.get() == null || ele.get() == callback) {
            iterator.remove()
        }
    }
}

fun pushActivity(activity: Activity) {
    val foreground = isForeground()
    activities.add(activity)

    if (!foreground) {
        val iterator = onForegroundCallbacks.iterator()
        while (iterator.hasNext()) {
            val ele = iterator.next()
            if (ele.get() == null) {
                iterator.remove()
            } else {
                ele.get()?.let { it(true) }
            }
        }
    }
}

fun popActivity(activity: Activity) {
    activities.remove(activity)

    if (isBackground()) {
        val iterator = onBackgroundCallbacks.iterator()
        while (iterator.hasNext()) {
            val ele = iterator.next()
            if (ele.get() == null) {
                iterator.remove()
            } else {
                ele.get()?.let { it(false) }
            }
        }
    }
}

fun isBackground() = activities.isEmpty()

fun isForeground() = activities.isNotEmpty()