package com.xinzy.microapp.relax.activity

import android.animation.ValueAnimator
import android.app.AlertDialog
import android.app.Service
import android.content.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.xinzy.microapp.lib.util.logV
import com.xinzy.microapp.relax.fragment.showTimePicker
import com.xinzy.microapp.relax.service.MediaService
import com.xinzy.microapp.relax.util.medias
import com.xinzy.microapp.relax.widget.MediaItemButton
import com.xinzy.microapp.relax.R
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val mReceiver = TimeReceiver()
    private var mBroadcastManager: LocalBroadcastManager? = null

    private var mServiceConnection: ServiceConnection = Connection()
    private var mMediaService: MediaService? = null
    private var isServiceConnected = false

    private var mLastSelectedItemButton: MediaItemButton? = null

    private var isHideUi = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initButtons()
        mBroadcastManager = LocalBroadcastManager.getInstance(this)
        startAndBindService()
    }

    override fun onResume() {
        super.onResume()

        val filter = IntentFilter(MediaService.RECEIVER_FILTER)
        mBroadcastManager?.registerReceiver(mReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        mBroadcastManager?.unregisterReceiver(mReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()

        try {
            if (isServiceConnected) {
                unbindService(mServiceConnection)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        if (v !is MediaItemButton) return
        selectItemMedia(v)
    }

    // 点击显示隐藏按钮
    fun onDisplayClicked(v: View) {
        if (isHideUi) {
            isHideUi = false
            displayView.setText(R.string.ic_hide)
            execAnimation(true)
        } else {
            isHideUi = true
            displayView.setText(R.string.ic_show)
            execAnimation(false)
        }
    }

    private fun execAnimation(show: Boolean) {
        val anim: ValueAnimator = if (show) ValueAnimator.ofFloat(0f, 1f) else ValueAnimator.ofFloat(1f, 0f)
        anim.interpolator = LinearInterpolator()
        anim.setDuration(500).addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            buttonLayout.alpha = value
            timeView.alpha = value
            exitBtn.alpha = value
        }
        anim.start()
    }

    // 点击计时按钮
    fun onTimeClicked(v: View) {
        showTimePicker(supportFragmentManager) {
            mMediaService?.totalTime = it
        }
    }

    // 退出按钮
    fun onExit(v: View) {
        AlertDialog.Builder(this).setTitle("确定要退出么?")
            .setNegativeButton("确定") { _, _ -> stopServiceAndExit() }
            .setPositiveButton("再听一会儿", null)
            .create().show()
    }

    // 停止服务并退出
    private fun stopServiceAndExit() {
        val service = Intent(this, MediaService::class.java)
        stopService(service)
        finish()
    }

    // 开启并绑定服务
    private fun startAndBindService() {
        val service = Intent(this, MediaService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(service)
        } else {
            startService(service)
        }
        bindService(service, mServiceConnection, Service.BIND_AUTO_CREATE)
    }

    // 点击切换场景
    private fun selectItemMedia(button: MediaItemButton) {
        mLastSelectedItemButton?.focused = false
        mLastSelectedItemButton = button
        button.focused = true

        button.media?.let {
            scrollableImageView.setImageResource(it.image)
            if (mMediaService == null) {
                startAndBindService()
            } else {
                mMediaService!!.play(it.mediaUrl)
            }
        }
    }

    // 初始化场景UI
    private fun initButtons() {
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val season = month / 3
        logV("init buttons, season = $season")

        medias.forEachIndexed { index, media ->
            val button = MediaItemButton(this)
            buttonLayout.addView(button)
            button.media = media

            button.setOnClickListener(this)

            if (index == season) {
                logV("select media + ${button.media}")
                selectItemMedia(button)
            }
        }
    }

    // 展示倒计时时间
    private fun showTime(total: Int, current: Int) {
        timerView.setTime(total, current)
    }

    private fun stopAndUnbindService() {
        val service = Intent(this, MediaService::class.java)
        stopService(service)
        unbindService(mServiceConnection)
        mMediaService = null
    }

    inner class Connection : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceConnected = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service !is MediaService.MediaBinder) return
            mMediaService = service.service
            isServiceConnected = true

            logV("service connection: $mMediaService")
            mMediaService?.let {
                showTime(it.totalTime, it.currentTime)

                if (!it.isPlaying) {
                    mLastSelectedItemButton?.media?.mediaUrl?.let { url -> it.play(url) }
                }
            }
        }
    }

    inner class TimeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return
            val total = intent.getIntExtra(MediaService.KEY_TOTAL, 0)
            val current = intent.getIntExtra(MediaService.KEY_CURRENT, 0)

            showTime(total, current)

            if (current == 0) {
                stopAndUnbindService()
            }
        }
    }
}
