package com.xinzy.microapp.unlock.counter.service

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.service.wallpaper.WallpaperService
import android.util.DisplayMetrics
import android.view.SurfaceHolder
import android.view.WindowManager

class UnlockCounterService : WallpaperService() {
    override fun onCreateEngine(): Engine {
        return UnlockCounterEngine(this)
    }


    inner class UnlockCounterEngine(private val context: Context) : Engine() {
        private lateinit var mSurfaceHolder: SurfaceHolder

        private var mWidth = 0
        private var mHeight = 0

        private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

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

            val canvas = holder.lockCanvas()
            mPaint.color = Color.BLUE
            canvas.drawColor(Color.BLUE)
            holder.unlockCanvasAndPost(canvas)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
        }

        override fun onDestroy() {
            super.onDestroy()
        }
    }
}
