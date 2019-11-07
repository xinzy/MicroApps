package com.xinzy.microapp.relax.manager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.xinzy.microapp.relax.service.MediaService

class AlarmAction private constructor(val context: Context) {

    private val mAlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun setJob() {
        val intent = Intent(MediaService.RECEIVER_TIMEOUT)
        val pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, 5 * 60 * 1000, pendingIntent)
    }

    fun setJob1() {
        val intent = Intent(MediaService.RECEIVER_TIMEOUT).apply { putExtra("type", 1) }
        val pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mAlarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, 3 * 60 * 1000, pendingIntent)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var sAlarmAction: AlarmAction? = null
        fun getInstance(context: Context): AlarmAction = sAlarmAction ?: synchronized(AlarmAction::class) {
            sAlarmAction ?: AlarmAction(context).also { sAlarmAction = it }
        }
    }
}