package com.xinzy.microapp.lib.util


import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import java.io.File

private var isDebug = true

fun setDebugable(b: Boolean) {
    isDebug = b
}

fun i(content: String) {
    if (!isDebug) return
    Log.i(generateTag(), content)
}

fun v(content: String) {
    if (!isDebug) return
    Log.v(generateTag(), content)
}

fun d(content: String) {
    if (!isDebug) return
    Log.d(generateTag(), content)
}

fun w(content: String, tr: Throwable? = null) {
    if (!isDebug) return
    val tag = generateTag()
    if (tr == null) {
        Log.w(tag, content)
    } else {
        Log.w(tag, content, tr)
    }
}

fun e(content: String, tr: Throwable? = null) {
    if (!isDebug) return
    val tag = generateTag()
    if (tr == null) {
        Log.e(tag, content)
    } else {
        Log.e(tag, content, tr)
    }
}

private fun generateTag(): String {
    val caller = Thread.currentThread().stackTrace[4]
    var callerClazzName = caller.className
    callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1)
    return "$callerClazzName.${caller.methodName}(L:${caller.lineNumber})"
}

fun fileLog(context: Context, content: String, filename: String = "log.txt") {
    val dir = context.getExternalFilesDir("log") ?: return
    if (!dir.exists()) dir.mkdirs()
    val file = File(dir, filename)

    val time = DateFormat.format("dd HH:mm:ss", System.currentTimeMillis())
    file.appendText("$time: $content\n")
}