package com.xinzy.microapp.lib.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.xinzy.microapp.lib.R
import com.xinzy.microapp.lib.util.dp2px

class LoadingView : View, ValueAnimator.AnimatorUpdateListener {

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mColor = 0
    private var mSize = 0

    private var mAnimateValue = 0
    private var mAnimator: ValueAnimator? = null

    companion object {
        private const val LINE_COUNT = 12
        private const val DEGREE_PER_LINE = 360 / LINE_COUNT
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.LoadingView, defStyleAttr, 0)

        mColor = ta.getColor(R.styleable.LoadingView_android_color, 0)
        mSize = ta.getDimensionPixelSize(R.styleable.LoadingView_size, dp2px(32))

        ta.recycle()
        mPaint.strokeCap = Paint.Cap.ROUND
    }

    constructor(context: Context, size: Int, color: Int = Color.WHITE) : this(context) {
        mColor = color
        mSize = size
    }

    fun setColor(color: Int) {
        mColor = color
        postInvalidate()
    }

    fun setSize(size: Int) {
        mSize = size
        requestLayout()
    }

    fun start() {
        if (mAnimator == null) {
            mAnimator = ValueAnimator.ofInt(0, LINE_COUNT - 1)
            mAnimator!!.addUpdateListener(this)
            mAnimator!!.duration = 600
            mAnimator!!.repeatMode = ValueAnimator.RESTART
            mAnimator!!.repeatCount = ValueAnimator.INFINITE
            mAnimator!!.interpolator = LinearInterpolator()
            mAnimator!!.start()
        } else if (!mAnimator!!.isStarted) {
            mAnimator!!.start()
        }
    }

    fun stop() {
        mAnimator?.let {
            it.removeAllUpdateListeners()
            it.cancel()
            mAnimator = null
        }
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        mAnimateValue = if (animation == null) 0 else animation.animatedValue as Int
        invalidate()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(mSize, mSize)
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) return
        mPaint.color = mColor
        val saveCount = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null, Canvas.ALL_SAVE_FLAG)
        drawLoading(canvas, mAnimateValue * DEGREE_PER_LINE)
        canvas.restoreToCount(saveCount)
    }

    private fun drawLoading(canvas: Canvas, rotateDegrees: Int) {
        val width = mSize / 12
        val height = mSize / 6
        mPaint.strokeWidth = width.toFloat()

        canvas.rotate(rotateDegrees.toFloat(), (mSize / 2).toFloat(), (mSize / 2).toFloat())
        canvas.translate((mSize / 2).toFloat(), (mSize / 2).toFloat())

        for (i in 0 until LINE_COUNT) {
            canvas.rotate(DEGREE_PER_LINE.toFloat())
            mPaint.alpha = (255f * (i + 1) / LINE_COUNT).toInt()
            canvas.translate(0f, (-mSize / 2 + width / 2).toFloat())
            canvas.drawLine(0f, 0f, 0f, height.toFloat(), mPaint)
            canvas.translate(0f, (mSize / 2 - width / 2).toFloat())
        }
    }
}