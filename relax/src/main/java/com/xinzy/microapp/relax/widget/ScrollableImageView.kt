package com.xinzy.microapp.relax.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.HorizontalScrollView
import android.widget.ImageView
import com.xinzy.microapp.relax.R


class ScrollableImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : HorizontalScrollView(context, attrs, defStyleAttr), Runnable {

    private val STEP_DISTANCE = 1 //每次滚动的像素

    private val mImageView: ImageView = ImageView(context)
    private var isAutoStart = false

    private var isStarted = false
    private var isEnableScroll = false

    private var direction = true  // 滚动方向，true 向右，false 向左

    init {
        overScrollMode = OVER_SCROLL_NEVER
        mImageView.scaleType = ImageView.ScaleType.FIT_XY
        mImageView.adjustViewBounds = true
        addView(mImageView)

        val ta = context.obtainStyledAttributes(attrs, R.styleable.ScrollableImageView)

        val id = ta.getResourceId(R.styleable.ScrollableImageView_android_src, 0)
        if (id != 0) setImageResource(id)

        isAutoStart = ta.getBoolean(R.styleable.ScrollableImageView_autoStart, false)
        isEnableScroll = ta.getBoolean(R.styleable.ScrollableImageView_scrollable, false)

        ta.recycle()
    }

    fun setImageResource(id: Int) {
        setImageBitmap(BitmapFactory.decodeResource(resources, id))
    }

    fun setImageBitmap(bitmap: Bitmap) {
        stop()
        val blurBitmap = createBlurBitmap(bitmap)

        mImageView.setImageBitmap(blurBitmap)
        scrollTo(0, 0)
        direction = true
        start()
    }

    fun start() {
        isStarted = true
        postDelayed(this, 30)
    }

    fun stop() {
        isStarted = false
        removeCallbacks(this)
    }

    override fun run() {
        if (direction) { // 向右滚动
            if (canScrollHorizontally(1)) { // 能向右滚动
                scrollBy(STEP_DISTANCE, 0)
            } else {
                direction = false
                scrollBy(- STEP_DISTANCE, 0)
            }
        } else {
            if (canScrollHorizontally(-1)) {
                scrollBy(- STEP_DISTANCE, 0)
            } else {
                direction = true
                scrollBy(STEP_DISTANCE, 0)
            }
        }
        postDelayed(this, 30)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return if (isEnableScroll) super.dispatchTouchEvent(ev) else true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (isAutoStart) {
            post { start() }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
    }


    private fun createBlurBitmap(bitmap: Bitmap, radius: Float = 5f): Bitmap {

        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val renderScript = RenderScript.create(context)
        val allocationIn = Allocation.createFromBitmap(renderScript, bitmap)
        val allocationOut = Allocation.createFromBitmap(renderScript, output)

        val blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        blurScript.setRadius(radius)

        blurScript.setInput(allocationIn)
        blurScript.forEach(allocationOut)

        allocationOut.copyTo(output)

        bitmap.recycle()
        renderScript.destroy()

        return output
    }

}