package com.xinzy.microapp.unlock.counter.service

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Paint
import android.service.wallpaper.WallpaperService
import android.util.DisplayMetrics
import android.view.SurfaceHolder
import android.view.WindowManager
import com.xinzy.microapp.lib.util.logD
import com.xinzy.microapp.lib.util.logV
import com.xinzy.microapp.unlock.counter.receiver.UnlockReceiver
import kotlin.math.log

class UnlockCounterService : WallpaperService() {

    private val mReceiver = UnlockReceiver()

    override fun onCreate() {
        super.onCreate()

        logD("service onCreate")
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_USER_PRESENT)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
        }
        registerReceiver(mReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(mReceiver)
    }

    override fun onCreateEngine(): Engine {
        return UnlockCounterEngine(this)
    }

    inner class UnlockCounterEngine(private val context: Context) : Engine() {
        private lateinit var mSurfaceHolder: SurfaceHolder

        private var mWidth = 0
        private var mHeight = 0

        private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val fontMetrics = Paint.FontMetrics()

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            mSurfaceHolder = surfaceHolder

            val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val displayMetrics = DisplayMetrics()
            manager.defaultDisplay.getMetrics(displayMetrics)
            mWidth = displayMetrics.widthPixels
            mHeight = displayMetrics.heightPixels
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)

            logV("surface create")

            val canvas = holder.lockCanvas()
            mPaint.color = Color.BLUE
            canvas.drawColor(Color.BLUE)

            mPaint.color = Color.WHITE
            mPaint.textSize = 36f

            mPaint.strokeWidth = 2f
            mPaint.color = Color.RED
            mPaint.textSize = mWidth / 2.5f
            mPaint.textAlign = Paint.Align.CENTER
            mPaint.getFontMetrics(fontMetrics)
            val offset = (fontMetrics.descent + fontMetrics.ascent) / 2
            canvas.drawText("888", mWidth / 2f, mHeight / 2f - offset, mPaint)

            holder.unlockCanvasAndPost(canvas)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)

            logV("surface destroy")
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            logV("visible change $visible")
        }

        override fun onDestroy() {
            super.onDestroy()
            logV("engine destroy")
        }
    }
}
