package com.xinzy.microapp.relax.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.xinzy.microapp.lib.util.fileLog
import com.xinzy.microapp.relax.R
import com.xinzy.microapp.relax.activity.MainActivity
import com.xinzy.microapp.relax.media.InternalMediaPlayer
import com.xinzy.microapp.relax.util.*
import com.xinzy.microapp.relax.work.download
import kotlin.math.min

class MediaService : Service(), Runnable {

    // 倒计时总时长
    var totalTime = DEFAULT_TIMEOUT
        set(value) {
            if (value > 0) {
                field = value
                currentTime = field

                mHandler.removeCallbacks(this)
                mHandler.postDelayed(this, 1000)
            }
        }
    // 倒计时剩余时长
    var currentTime = totalTime
        set(value) {
            if (value < 0) {
                field = 0
                fileLog(this, "计时结束")

                if (mMediaPlayer.isPlaying) {
                    mMediaPlayer.stop()
                }
                stopSelf()
                return
            } else {
                field = value
                fileLog(this, "设置时间 $field")
                mHandler.removeCallbacks(this)
                mHandler.postDelayed(this, 1000)
            }
            sendBroadcast()
        }
    val isPlaying: Boolean
        get() = mMediaPlayer.playing

    // 广播发送者
    private lateinit var mBroadcastManager: LocalBroadcastManager
    private lateinit var mAlarmManager: AlarmManager
    private val mTimerReceiver = TimerReceiver()
    private val mHandler = Handler()
    private var mAlarmOperation: PendingIntent? = null

    private val mMediaPlayer: InternalMediaPlayer by lazy { InternalMediaPlayer() }
    private var playingUrl = ""

    private var lastBackgroundTimestamp = -1L
    private var lastBackgroundRemainTime = -1

    private val onAppEnterForeground = callable@ { _: Boolean ->
        if (lastBackgroundTimestamp < 0) return@callable
        if (lastBackgroundRemainTime < 0) return@callable

        clearAlarm()
        val timestamp = SystemClock.elapsedRealtime()
        val delta = (timestamp - lastBackgroundTimestamp).toInt() / 1000

        fileLog(
            this,
            "App 回到前台，显示剩余时间 $currentTime, 实际过了 $delta, 还剩余 ${lastBackgroundRemainTime - delta}"
        )

        if (lastBackgroundRemainTime < delta) { //进入后台时间超过了倒计时剩余时间
            currentTime = 0
            stopSelf()
        } else {    //进入后台时间小于倒计时时间
            currentTime = lastBackgroundRemainTime - delta
        }

        lastBackgroundRemainTime = -1
        lastBackgroundTimestamp = -1
    }

    private val onAppEnterBackground = callable@ { _: Boolean ->
        if (currentTime < 0) return@callable
        lastBackgroundRemainTime = currentTime
        lastBackgroundTimestamp = SystemClock.elapsedRealtime()

        fileLog(this, "App进入后台, 剩余时间 $currentTime")
        setAlarmForWakeupTimer()
    }

    override fun onBind(intent: Intent): IBinder = MediaBinder()

    override fun onCreate() {
        super.onCreate()

        mBroadcastManager = LocalBroadcastManager.getInstance(this)
        mAlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        createNotification()

        mHandler.postDelayed(this, 1000)
        addOnForegroundCallback(onAppEnterForeground)
        addOnBackgroundCallback(onAppEnterBackground)

        val filter = IntentFilter(RECEIVER_TIMEOUT)
        registerReceiver(mTimerReceiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        stopForeground(true)
        unregisterReceiver(mTimerReceiver)
        removeOnBackgroundCallback(onAppEnterBackground)
        removeOnForegroundCallback(onAppEnterForeground)
        mHandler.removeCallbacks(this)

        try {
            mMediaPlayer.close()
        } catch (e: Throwable) {
            fileLog(this, "onDestroy $e")
            e.printStackTrace()
        } finally {
        }
        fileLog(this, "service destroy")
    }

    override fun run() {
        currentTime -= 1
    }

    fun play() {
        mMediaPlayer.play()
    }

    /**
     * 开始播放url
     */
    fun play(url: String) {
        playingUrl = url

        fileLog(this, "准备播放音乐 $url", "media.txt")
        try {
            if (mMediaPlayer.playing || mMediaPlayer.pausing) {
                mMediaPlayer.reset()
            }
            var playUrl = url
            if (mp3CacheExist(this, url)) {  // 缓存文件存在，则播放缓存文件
                playUrl = mp3CacheFile(this, url).absolutePath
            } else {
                download(this, url)
            }

            mMediaPlayer.setDataSource(playUrl)
            mMediaPlayer.prepareAsync()
            fileLog(this, "播放音乐 $url", "media.txt")
        } catch (e: Exception) {
            e.printStackTrace()
            fileLog(this, "播放失败 ${e.message}", "media.txt")
        }
    }

    fun pause() {
        if (mMediaPlayer.playing) mMediaPlayer.pause()
    }

    fun stop() {
        mMediaPlayer.stop()
    }

    /// 创建前台通知
    private fun createNotification() {

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(channel)
            }
        }

        val intent = Intent(this, MainActivity::class.java)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .setSmallIcon(R.drawable.ic_notification_small)
            .setContentTitle("Relax")
            .setSubText("正在放松中···")
            .setContentText("放下工作，放松心情，闭上眼睛，用心去聆听这大自然的声音")
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(true)
            .setChannelId(CHANNEL_ID)
            .build()

        startForeground(100, notification)
    }

    /// 发送倒计时广播
    private fun sendBroadcast() {
        val intent = Intent(RECEIVER_FILTER).apply {
            putExtra(KEY_TOTAL, totalTime)
            putExtra(KEY_CURRENT, currentTime)
        }
        mBroadcastManager.sendBroadcast(intent)
    }

    // 设置闹钟唤醒计时器
    private fun setAlarmForWakeupTimer() {
        if (currentTime <= 0) return
        val countdown: Long = min(currentTime * 1000, 30 * 1000).toLong()

        fileLog(this, "设置闹钟， 当前剩余时间 $currentTime, 设置时间 ${countdown / 1000}")
        val intent = Intent(RECEIVER_TIMEOUT)
        mAlarmOperation = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC, System.currentTimeMillis() + countdown, mAlarmOperation)
        lastBackgroundTimestamp = SystemClock.elapsedRealtime()
        lastBackgroundRemainTime = currentTime
    }

    private fun clearAlarm() {
        if (mAlarmOperation != null) {
            mAlarmManager.cancel(mAlarmOperation)
            mAlarmOperation = null
        }
    }

    companion object {
        const val RECEIVER_FILTER = "TIME_COUNT_DOWN"
        const val RECEIVER_TIMEOUT = "TIMEOUT"

        const val KEY_TOTAL = "TOTAL"
        const val KEY_CURRENT = "CURRENT"

        private const val TAG = "MediaService"
        private const val DEFAULT_TIMEOUT = 10 * 60

        private const val CHANNEL_ID = "Media"
        private const val CHANNEL_NAME = "RelaxPlayer"
    }

    inner class MediaBinder : Binder() {
        val service: MediaService
            get() = this@MediaService
    }

    inner class TimerReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent == null) return
            if (intent.action != RECEIVER_TIMEOUT) return
            if (lastBackgroundTimestamp < 0) return

            val duration = (SystemClock.elapsedRealtime() - lastBackgroundTimestamp).toInt() / 1000

            fileLog(context, "收到闹钟消息，duration = $duration, lastBackgroundRemainTime=$lastBackgroundRemainTime currentTime = $currentTime")

            currentTime = if (lastBackgroundRemainTime < duration) 0 else lastBackgroundRemainTime - duration
            if (currentTime > 0) setAlarmForWakeupTimer()
        }
    }
}
