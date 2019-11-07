package com.xinzy.microapp.relax.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.xinzy.microapp.relax.activity.MainActivity
import com.xinzy.microapp.relax.manager.AlarmAction
import com.xinzy.microapp.lib.util.d
import com.xinzy.microapp.lib.util.fileLog
import com.xinzy.microapp.relax.work.download
import com.xinzy.microapp.relax.R
import com.xinzy.microapp.relax.util.*
import kotlin.math.min

class MediaService : Service(), Runnable, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

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
                stopSelf()
                return
            } else {
                field = value
                mHandler.postDelayed(this, 1000)
            }
            sendBroadcast()
        }

    // 广播发送者
    private lateinit var mBroadcastManager: LocalBroadcastManager
    private lateinit var mAlarmManager: AlarmManager
    private val mTimerReceiver = TimerReceiver()
    private val mHandler = Handler()


    private val mMediaPlayer = MediaPlayer().apply {
//        val attr = AudioAttributes.Builder()
//            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//            .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
//            .setLegacyStreamType(AudioManager.STREAM_MUSIC)
//            .setUsage(AudioAttributes.USAGE_MEDIA)
//            .build()
//        setAudioAttributes(attr)

        setAudioStreamType(AudioManager.STREAM_MUSIC)
        setOnPreparedListener(this@MediaService)
        setOnCompletionListener(this@MediaService)

        isLooping = true
    }
    var isPlaying = false
    var isPause = false

    private var playingUrl = ""


    private var lastBackgroundTimestamp = -1L
    private var lastBackgroundRemainTime = -1

    private val onAppEnterForeground = callable@ { _: Boolean ->
        if (lastBackgroundTimestamp < 0) return@callable
        if (lastBackgroundRemainTime < 0) return@callable


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
        AlarmAction.getInstance(applicationContext).setJob()
        AlarmAction.getInstance(applicationContext).setJob1()
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

        try {
            if (isPause || isPlaying) {
                mMediaPlayer.stop()
                mMediaPlayer.release()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            isPlaying = false
            isPause = false
        }
        fileLog(this, "service destroy")
    }

    override fun run() {
        currentTime -= 1
    }

    fun play() {
        if (isPause) {
            mMediaPlayer.start()
        }
    }

    /**
     * 开始播放url
     */
    fun play(url: String) {
        playingUrl = url

        fileLog(this, "准备播放音乐 $url", "media.txt")
        try {
            if (isPlaying) {
                mMediaPlayer.reset()
            }
            var playUrl = url
            if (mp3CacheExist(this, url)) {  // 缓存文件存在，则播放缓存文件
                playUrl = mp3CacheFile(this, url).absolutePath
            } else {
                download(this, url)
            }

            isPlaying = false
            mMediaPlayer.setDataSource(playUrl)
            mMediaPlayer.prepareAsync()
            fileLog(this, "播放音乐 $url", "media.txt")
        } catch (e: Exception) {
            e.printStackTrace()
            fileLog(this, "播放失败 ${e.message}", "media.txt")
        }
    }

    fun pause() {
        if (isPlaying && !isPause) {
            mMediaPlayer.pause()
            isPause = true
        }
    }

    fun stop() {
        if (isPlaying) {
            mMediaPlayer.stop()
        }
        isPause = false
        isPlaying = false
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
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Relax")
            .setSubText("正在放松中···")
            .setContentText("放下工作，放松心情，闭上眼睛，用心去聆听这大自然的声音")
            .setWhen(System.currentTimeMillis())
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

        fileLog(this, "设置闹钟， 当前剩余时间 $currentTime, 设置时间 $countdown")
        val intent = Intent(RECEIVER_TIMEOUT)
        val pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME, countdown, pendingIntent)
    }

    /////////////////////////////////////////////////////////////////////////////////////
    // callbacks
    /////////////////////////////////////////////////////////////////////////////////////
    override fun onPrepared(mp: MediaPlayer?) {
        d("prepared and start to play")
        mp?.start()
        isPlaying = true
    }

    override fun onCompletion(mp: MediaPlayer?) {
        d("prepared and start to play")
        fileLog(this, "播放完成，继续重播", "media.txt")
//        isPlaying = false
//        mMediaPlayer.reset()
//        play(playingUrl)
    }

    companion object {
        const val RECEIVER_FILTER = "TIME_COUNT_DOWN"
        const val RECEIVER_TIMEOUT = "TIMEOUT"

        const val KEY_TOTAL = "TOTAL"
        const val KEY_CURRENT = "CURRENT"

        private const val TAG = "MediaService"
        private const val DEFAULT_TIMEOUT = 30 * 60

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

            val duration = (SystemClock.elapsedRealtime() - lastBackgroundTimestamp).toInt() / 1000
            if (currentTime < duration) {
                currentTime = 0
                return
            }
            currentTime -= duration
            setAlarmForWakeupTimer()
        }
    }
}
