package com.app.customseekbar.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.app.customseekbar.R

class CustomSlider(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var mWidth = 0F
    private var mHeight = 0F

    private val mProgressWidth = 29F
    private var mProgressShaderGradient: IntArray

    private var mDashWidth = 17F
    private var mDashHeight = 15F
    private val mGapBetweenDash = 7F
    private val mDashThickness = 3F
    private val mDashColor = R.color.dashColor

    private var mTickCount = 1
    private val mTickWidth = 47F
    private val mTickHeight = 98F
    private val mTickStrokeWidth = 4F
    private val mTickSideHeight = 50F
    private val mTickRightOffset by lazy { mTickHeight - mTickSideHeight }
    private val mTickBorderColor = R.color.tickBorderColor
    private var mTickFillColor = R.color.tickFillColor

    private var mThumbIndex = -1
    private val mThumbWidth = 73F
    private val mThumbHeight = 159F
    private val mThumbSideHeight = 85F
    private val mThumbStrokeWidth = 2F
    private val mThumbStrokeColor = R.color.thumbStrokeColor
    private val mThumbRightOffset by lazy { mThumbHeight - mThumbSideHeight }

    private val mTrackRect = RectF()
    private val mProgressRect = RectF()
    private val mTickRect = RectF()
    private val mThumbRect = RectF()

    private val tickCenterY: FloatArray by lazy {
        FloatArray(mTickCount)
    }

    private var isValidTickCount = false

    private val mBoundsPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.GRAY
            style = Paint.Style.STROKE
            strokeWidth = 0.5F
        }
    }

    private val mBaseDashPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = context.getColor(mDashColor)
            style = Paint.Style.FILL
        }
    }

    private val mProgressTickPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = context.getColor(mTickFillColor)
            style = Paint.Style.FILL
        }
    }

    private val mBaseTickPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = context.getColor(mTickBorderColor)
            style = Paint.Style.STROKE
            strokeWidth = mTickStrokeWidth
        }
    }

    private val mThumbStrokePaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = context.getColor(mThumbStrokeColor)
            style = Paint.Style.STROKE
            strokeWidth = mThumbStrokeWidth
        }
    }

    init {
        val attributes =
            context.theme.obtainStyledAttributes(attrs, R.styleable.CustomSlider, 0, 0)
        mTickCount = attributes.getInteger(R.styleable.CustomSlider_tickCount, mTickCount)
        mTickFillColor =
            attributes.getResourceId(R.styleable.CustomSlider_tickFillColor, mTickFillColor)
        mProgressShaderGradient = attributes.resources.getIntArray(
            attributes.getResourceId(
                R.styleable.CustomSlider_shaderGradient,
                R.array.progressShaderGradient
            )
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Log.d("$tag", "onDraw")
        canvas?.let {
            if (!isValidTickCount) return

            //drawBounds(it)
            drawSlider(it)
        }
    }

    private fun drawSlider(canvas: Canvas) {

        if (mThumbIndex > mTickCount)
            return

        tickCenterY.forEachIndexed { index: Int, yValue: Float ->
            Log.d("$tag", "index:$index, yValue:$yValue")

            fillTick(
                yValue,
                canvas,
                if (index < mThumbIndex) Paint.Style.FILL else Paint.Style.STROKE
            )

            if (index == mThumbIndex - 1)
                drawThumb(tickCenterY[index], canvas)

            val tickTopLeft = PointF(mTickRect.left, yValue + mTickHeight / 2 - mTickSideHeight)
            val tickTopRight = PointF(mTickRect.right, yValue - mTickHeight / 2)

            drawDiscreteTrack(canvas, index, tickTopLeft, tickTopRight)

            drawProgressShader(canvas, index, tickTopLeft, tickTopRight)
        }
    }

    private fun drawThumb(centerY: Float, canvas: Canvas) {
        val tickPath = Path().apply {
            moveTo(mThumbRect.left, centerY + mThumbHeight / 2)
            lineTo(mThumbRect.right, centerY + mThumbHeight / 2 - mThumbRightOffset)
            lineTo(mThumbRect.right, centerY - mThumbHeight / 2)
            lineTo(mThumbRect.left, centerY + mThumbHeight / 2 - mThumbSideHeight)
            lineTo(mThumbRect.left, centerY + mThumbHeight / 2)
        }
        canvas.drawPath(tickPath, mThumbStrokePaint)
    }

    private fun collectTicksCenterY() {
        val segmentHeight = mTickRect.height() / mTickCount

        isValidTickCount = !(mTickCount == 0 || (mTickHeight > segmentHeight))

        if (!isValidTickCount) return

        var h = mTickRect.bottom

        repeat(mTickCount) {
            when (it) {
                0 -> {
                    tickCenterY[it] = (h + (h - mTickHeight)) / 2
                }

                mTickCount - 1 -> {
                    tickCenterY[it] = (mTickRect.top + (mTickRect.top + mTickHeight)) / 2
                }

                else -> {
                    tickCenterY[it] = (h + (h - segmentHeight)) / 2
                }
            }
            h -= segmentHeight
        }
    }

    private fun fillTick(centerY: Float, canvas: Canvas, type: Paint.Style) {
        val tickPath = Path().apply {
            moveTo(mTickRect.left, centerY + mTickHeight / 2)
            lineTo(mTickRect.right, centerY + mTickHeight / 2 - mTickRightOffset)
            lineTo(mTickRect.right, centerY - mTickHeight / 2)
            lineTo(mTickRect.left, centerY + mTickHeight / 2 - mTickSideHeight)
            lineTo(mTickRect.left, centerY + mTickHeight / 2)
        }
        canvas.drawPath(
            tickPath,
            if (type == Paint.Style.STROKE) mBaseTickPaint else mProgressTickPaint
        )
    }

    private fun drawDiscreteTrack(
        canvas: Canvas, tickIndex: Int, tickTopLeft: PointF, tickTopRight: PointF
    ) {
        if (tickIndex == mTickCount - 1) return

        val left = mTrackRect.left
        val right = mTrackRect.right

        val trackStartY = tickCenterY[tickIndex] - mTickSideHeight / 2 - mGapBetweenDash
        val trackEndY = tickCenterY[tickIndex + 1] + mTickSideHeight / 2 + mGapBetweenDash

        /* *
        * Using Line equation finding 'y' co-ordinates,
        * where tick parallel line is intersecting at track left & right.
        * (y-y1) = m(x-x1)
        * (y-y1) = ((y2-y1)/(x2-x1)) * (x-x1)
        * y = (((y2-y1)/(x2-x1)) * (x-x1)) + y1
        * */

        var dashPointY1 =
            (((tickTopRight.y - tickTopLeft.y) / (tickTopRight.x - tickTopLeft.x)) * (left - tickTopLeft.x)) + tickTopLeft.y
        var dashPointY2 =
            (((tickTopRight.y - tickTopLeft.y) / (tickTopRight.x - tickTopLeft.x)) * (right - tickTopLeft.x)) + tickTopLeft.y


        var h = trackStartY

        while (h > trackEndY) {
            val path = Path().apply {
                dashPointY1 -= mGapBetweenDash
                dashPointY2 -= mGapBetweenDash

                moveTo(left, dashPointY1 - mDashThickness)
                lineTo(left + mDashThickness, dashPointY1 - mDashThickness)
                lineTo(right, dashPointY2)
                lineTo(right - mDashThickness, dashPointY2)
                lineTo(left, dashPointY1 - mDashThickness)
            }
            canvas.drawPath(path, mBaseDashPaint)
            h -= mGapBetweenDash
        }
    }

    private fun drawProgressShader(
        canvas: Canvas,
        tickIndex: Int,
        tickTopLeft: PointF,
        tickTopRight: PointF
    ) {
        if ((tickIndex >= (mThumbIndex - 1)) || (tickIndex == mTickCount - 1)) return

        val left = mProgressRect.left
        val right = mProgressRect.right

        val trackStartY = tickCenterY[tickIndex] - mTickSideHeight / 2
        val trackEndY = tickCenterY[tickIndex + 1] + mTickSideHeight / 2

        val dashPointY1 =
            (((tickTopRight.y - tickTopLeft.y) / (tickTopRight.x - tickTopLeft.x)) * (left - tickTopLeft.x)) + tickTopLeft.y
        val dashPointY2 =
            (((tickTopRight.y - tickTopLeft.y) / (tickTopRight.x - tickTopLeft.x)) * (right - tickTopLeft.x)) + tickTopLeft.y

        val shaderHeight =
            (trackStartY - trackEndY).minus(if (tickIndex == mThumbIndex - 2) (mThumbSideHeight - mTickSideHeight) / 2 else 0F)

        val progressShaderPath = Path().apply {
            moveTo(mProgressRect.left, dashPointY1)
            lineTo(mProgressRect.right, dashPointY2)
            lineTo(mProgressRect.right, dashPointY2 - shaderHeight)
            lineTo(mProgressRect.left, dashPointY1 - shaderHeight)
        }

        canvas.drawPath(progressShaderPath, Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL

            if (tickIndex == mThumbIndex - 2) {
                shader = LinearGradient(
                    mProgressRect.left,
                    dashPointY1,
                    mProgressRect.right,
                    dashPointY2 - shaderHeight,
                    mProgressShaderGradient,
                    floatArrayOf(0F, 0.5F, 1F),
                    Shader.TileMode.MIRROR
                )
            } else color = context.getColor(R.color.shaderStartColor)
        })

    }

    private fun createBounds() {
        val cx = mWidth / 2

        mThumbRect.set(cx - mThumbWidth / 2, 0F, cx + mThumbWidth / 2, mHeight)
        mTickRect.set(
            cx - mTickWidth / 2,
            mThumbRect.top + (mThumbHeight - mTickHeight) / 2,
            cx + mTickWidth / 2,
            mThumbRect.bottom - (mThumbHeight - mTickHeight) / 2
        )
        mProgressRect.set(
            cx - mProgressWidth / 2,
            mTickRect.top,
            cx + mProgressWidth / 2,
            mTickRect.bottom
        )
        mTrackRect.set(
            ((mWidth / 2) - mDashWidth / 2),
            mTickRect.top,
            ((mWidth / 2) + mDashWidth / 2),
            mTickRect.bottom
        )
    }

    private fun drawBounds(canvas: Canvas) {
        canvas.drawRect(mTickRect, mBoundsPaint)
        canvas.drawRect(mTrackRect, mBoundsPaint)
        canvas.drawRect(mProgressRect, mBoundsPaint)
        canvas.drawRect(mThumbRect, mBoundsPaint)
    }

    private fun updateProgress(x: Float, y: Float) {
        if (x !in mThumbRect.left..mThumbRect.right)
            return

        tickCenterY.forEachIndexed { index, yCenter ->
            val tickTop = yCenter - mTickHeight / 2
            val tickBottom = yCenter + mTickHeight / 2
            if (y in tickTop..tickBottom) {
                mThumbIndex = index + 1
                invalidate()
                return@forEachIndexed
            }
        }
    }

    fun setTickCount(count: Int) {
        mTickCount = count
        invalidate()
    }

    fun setThumbIndex(index: Int) {
        mThumbIndex = index
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        Log.d("$tag", "onSizeChanged")

        mWidth = w.toFloat()
        mHeight = h.toFloat()

        createBounds()
        collectTicksCenterY()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d("$tag", "ACTION_DOWN, @ ${it.x} x ${it.y}")
                    return true
                }

                MotionEvent.ACTION_UP -> {
                    MotionEvent.ACTION_CANCEL
                    Log.d("$tag", "ACTION_UP, @ ${it.x} x ${it.y}")
                    updateProgress(it.x, it.y)
                    return true
                }

                else -> Log.d("$tag", "Action : ${it.action}")
            }
        }
        return false
    }

}