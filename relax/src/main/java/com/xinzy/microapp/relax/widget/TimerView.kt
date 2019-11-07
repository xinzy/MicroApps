package com.xinzy.microapp.relax.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.xinzy.microapp.relax.R
import kotlin.math.min


class TimerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr) {

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val fontMetrics = Paint.FontMetrics()

    private var mTimerRadius = 0f   // 计时器的半径

    /* attrs */
    private var timerLineColor: Int  // 计时器颜色
    private var timerLineWidth: Float = 12f // 计时器线宽

    private var waveWidth: Float = 1f  // 波浪线宽
    private var waveColor: Int  // 波浪线颜色
    private var maxWaveCount: Int = 3  // 最大波浪数

    private var mCountDownColor: Int
    private var mTextColor: Int

    private var waveDistance = 0f
    private var waveStepDistance = 0f
    private var mWaveRadius: FloatArray

    private var mAnimValue: Float = 0f
    private var mValueAnimation: ValueAnimator? = null


    /// 当前时间
    private var currentSecond = 0
    /** 倒计时时长 */
    private var totalTimerSecond = 0

    private val timeString: String
        get() {
            val min = currentSecond / 60
            val second = currentSecond % 60
            return String.format("%02d:%02d", min, second)
        }

    init {
        mPaint.style = Paint.Style.STROKE
        val ta = context.obtainStyledAttributes(attrs, R.styleable.TimerView)

        timerLineColor = ta.getColor(R.styleable.TimerView_timerLineColor, Color.WHITE)
        timerLineWidth = ta.getDimension(R.styleable.TimerView_timerLineWidth, 6f)

        waveColor = ta.getColor(R.styleable.TimerView_waveColor, Color.WHITE)
        waveWidth = ta.getDimension(R.styleable.TimerView_waveWidth, 1f)
        maxWaveCount = ta.getInt(R.styleable.TimerView_maxWaveCount, 3)

        check(maxWaveCount > 0) { "max wave count must great than 0" }
        mWaveRadius = FloatArray(maxWaveCount) { 0f }

//        val duration = ta.getInt(R.styleable.TimerView_duration, 0)
//        this.timerCountDown = duration
        mCountDownColor = ta.getColor(R.styleable.TimerView_timerCountDownColor, Color.RED)
        mTextColor = ta.getColor(R.styleable.TimerView_android_textColor, Color.WHITE)

        ta.recycle()
    }

    fun setTime(total: Int, current: Int) {
        totalTimerSecond = total
        currentSecond = current
        postInvalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val size = min(measuredHeight, measuredWidth)
        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mTimerRadius = w / 4f
        waveDistance = w / 4f
        waveStepDistance = waveDistance / maxWaveCount

        mWaveRadius.forEachIndexed { index, _ -> mWaveRadius[index] = waveStepDistance * index }
    }

    override fun onDraw(canvas: Canvas) {

        mPaint.color = timerLineColor
        mPaint.strokeWidth = timerLineWidth
        canvas.drawCircle(width / 2f, height / 2f, mTimerRadius, mPaint)

        // 绘制倒计时
        mPaint.color = mCountDownColor

        if (totalTimerSecond > 0) {
            val angle = 360f * (currentSecond * 1f / totalTimerSecond)
            canvas.drawArc(width / 4f, width / 4f, width * 3f / 4, width * 3f / 4, -90f, angle, false, mPaint)
        }

        // 绘制时间文字
        mPaint.strokeWidth = 2f
        mPaint.color = mTextColor
        mPaint.textSize = width / 10f
        mPaint.textAlign = Paint.Align.CENTER
        mPaint.getFontMetrics(fontMetrics)
        val offset = (fontMetrics.descent + fontMetrics.ascent) / 2
        canvas.drawText(timeString, width / 2f, height / 2f - offset, mPaint)

        mPaint.strokeWidth = waveWidth
        mPaint.color = waveColor

        val distance = mAnimValue * waveStepDistance
        mWaveRadius.forEach { value ->
            val radius = distance + value

            if (radius > 0) {
                canvas.drawCircle(width / 2f, height / 2f, radius + mTimerRadius, mPaint)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        mValueAnimation = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                mAnimValue = it.animatedValue as Float
                postInvalidate()
            }
        }
        mValueAnimation!!.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        mValueAnimation?.cancel()
    }
}