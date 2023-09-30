package com.app.customseekbar.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View

class GMSlider(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var mWidth = 0F
    private var mHeight = 0F

    private var mTrackThickness = 30F
    private val mDashGap = 16F
    private val mDashThickness = 7F

    private val mTrackRect = Rect()

    private val mBoundsPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.GRAY
            style = Paint.Style.STROKE
            strokeWidth = 1F
        }
    }

    private val mSlantedTrackPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.GRAY
            style = Paint.Style.FILL_AND_STROKE
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Log.d("$tag", "onDraw")
        canvas?.let {
            //drawBounds(it)
            drawSlantedTrack(it)
        }
    }

    private fun drawSlantedTrack(it: Canvas) {
        val left = mTrackRect.left.toFloat()
        val right = mTrackRect.right.toFloat()

        var h = mTrackRect.bottom.toFloat() - mDashGap

        while (h > 0) {
            val path = Path().apply {
                moveTo(left, h + mDashGap)
                lineTo(left + mDashThickness, h + mDashGap)
                lineTo(right, h)
                lineTo(right - mDashThickness, h)
                lineTo(left, h + mDashGap)
            }
            it.drawPath(path, mSlantedTrackPaint)
            h -= mDashGap
        }
    }

    private fun drawBounds(it: Canvas) {
        it.drawRect(mTrackRect, mBoundsPaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        Log.d("$tag", "onSizeChanged")

        mWidth = w.toFloat()
        mHeight = h.toFloat()

        mTrackRect.set(
            ((mWidth / 2) - mTrackThickness / 2).toInt(),
            0,
            ((mWidth / 2) + mTrackThickness / 2).toInt(),
            mHeight.toInt()
        )
    }

}