package com.xinzy.microapp.relax.work

import android.content.Context
import androidx.work.*
import com.xinzy.microapp.lib.util.fileLog
import java.util.concurrent.TimeUnit

fun startTimeCountDown(context: Context) {
    val constraints = Constraints.Builder()
//        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val request = PeriodicWorkRequest.Builder(TimerWork::class.java, 5, TimeUnit.MINUTES)
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context).enqueue(request)
}

class TimerWork(val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        fileLog(context, "任务执行")

        return Result.success()
    }
}