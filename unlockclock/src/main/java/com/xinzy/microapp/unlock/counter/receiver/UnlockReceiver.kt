package com.xinzy.microapp.unlock.counter.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.xinzy.microapp.lib.util.logV

class UnlockReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) return

        val action = intent.action ?: ""
        when (action) {
            Intent.ACTION_SCREEN_OFF -> {
                logV("ACTION_SCREEN_OFF")
            }
            Intent.ACTION_SCREEN_ON -> {
                logV("ACTION_SCREEN_ON")
            }
            Intent.ACTION_USER_PRESENT -> {
                logV("ACTION_USER_PRESENT")
            }
        }
    }
}