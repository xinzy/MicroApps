package com.xinzy.microapp.relax.util

import android.content.Context
import com.xinzy.microapp.lib.util.md5
import java.io.File

fun mp3CacheFile(context: Context, url: String): File {

    val path = "audio/${url.md5()}"
    val dir = context.getExternalFilesDir(path) ?: File(context.filesDir, path)
    if (!dir.exists()) dir.mkdirs()

    return File(dir, url.md5())
}

fun mp3CacheExist(context: Context, url: String): Boolean {
    val mp3File = mp3CacheFile(context, url)

    if (!mp3File.exists()) return false
    val flagFile = File(mp3File.parentFile, ".downloaded")
    return flagFile.exists()
}