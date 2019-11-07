package com.xinzy.microapp.relax.work

import android.content.Context
import androidx.work.*
import com.xinzy.microapp.lib.util.logD
import com.xinzy.microapp.relax.util.mp3CacheExist
import com.xinzy.microapp.relax.util.mp3CacheFile
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import java.io.File
import java.util.concurrent.TimeUnit


const val KEY_URL = "URL"

val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

fun download(context: Context, url: String) {
    val constraints = Constraints.Builder()
//        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val data = Data.Builder().putString(KEY_URL, url).build()
    val request = OneTimeWorkRequest.Builder(DownloadWork::class.java)
        .setConstraints(constraints)
        .setInputData(data)
        .build()

    WorkManager.getInstance(context).enqueue(request)
}

class DownloadWork(private val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        logD("download thread: ${Thread.currentThread().name}")

        val url = inputData.getString(KEY_URL) ?: return Result.failure()
        if (mp3CacheExist(context, url)) return Result.success()

        val request = Request.Builder().get().url(url).build()
        val response = okHttpClient.newCall(request).execute()
        return if (response.isSuccessful) {

            val source = response.body?.source()
            if (source == null) {
                Result.failure()
            } else {

                try {
                    val file = mp3CacheFile(context, url)
                    val sink = file.sink().buffer()
                    sink.writeAll(source)

                    File(file.parentFile, ".downloaded").createNewFile()

                    Result.success()
                } catch (e: Exception) {
                    e.printStackTrace()

                    Result.failure()
                }
            }
        } else {
            Result.failure()
        }
    }
}