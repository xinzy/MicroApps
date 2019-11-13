package com.xinzy.microapp.relax

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.work.Configuration
import com.tencent.bugly.crashreport.CrashReport
import com.xinzy.microapp.relax.util.popActivity
import com.xinzy.microapp.relax.util.pushActivity

class RelaxApplication : Application(), Configuration.Provider,
    Application.ActivityLifecycleCallbacks {



    override fun onCreate() {
        super.onCreate()

        registerActivityLifecycleCallbacks(this)
        CrashReport.initCrashReport(this, "a54147c058", false)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
//            .setExecutor(Executors.newSingleThreadExecutor())
//            .setTaskExecutor(Executors.newSingleThreadExecutor())
            .setMinimumLoggingLevel(android.util.Log.WARN)
            .build()
    }



    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        pushActivity(activity)
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity) {
        popActivity(activity)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }
}