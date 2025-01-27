package com.github.anastr.speedviewlib

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet

/**
 * this Library build By Anas Altair
 * see it on [GitHub](https://github.com/anastr/SpeedView)
 */
class TubeSpeedometer @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : Speedometer(context, attrs, defStyleAttr) {

    private val tubePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val tubeBacPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val speedometerRect = RectF()


    var speedometerBackColor: Int
        get() = tubeBacPaint.color
        set(speedometerBackColor) {
            tubeBacPaint.color = speedometerBackColor
            updateBackgroundBitmap()
            invalidate()
        }

    init {
        init()
        initAttributeSet(context, attrs)
    }

    override fun defaultGaugeValues() {
        sections[0].color = -0xff432c
        sections[1].color = -0x3ef9
        sections[2].color = -0xbbcca
    }

    override fun defaultSpeedometerValues() {
        super.backgroundCircleColor = 0
        super.setSpeedometerWidth(dpTOpx(40f))
    }

    private fun init() {
        tubePaint.style = Paint.Style.STROKE
        tubeBacPaint.style = Paint.Style.STROKE
        tubeBacPaint.color = -0x8a8a8b

        if (Build.VERSION.SDK_INT >= 11)
            setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    private fun initAttributeSet(context: Context, attrs: AttributeSet?) {
        if (attrs == null)
            return
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.TubeSpeedometer, 0, 0)

        tubeBacPaint.color = a.getColor(R.styleable.TubeSpeedometer_sv_speedometerBackColor, tubeBacPaint.color)
        a.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)

        updateBackgroundBitmap()
    }

    private fun initDraw() {
        tubePaint.strokeWidth = getSpeedometerWidth()
        if (currentSection != null)
            tubePaint.color = currentSection!!.color
        else
            tubePaint.color = 0 // transparent color
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        initDraw()

        val sweepAngle = (getEndDegree() - getStartDegree()) * getOffsetSpeed()
        canvas.drawArc(speedometerRect, getStartDegree().toFloat(), sweepAngle, false, tubePaint)

        drawSpeedUnitText(canvas)
        drawIndicator(canvas)
        drawNotes(canvas)
    }

    override fun updateBackgroundBitmap() {
        val c = createBackgroundBitmapCanvas()
        tubeBacPaint.strokeWidth = getSpeedometerWidth()

        val risk = getSpeedometerWidth() * .5f + padding
        speedometerRect.set(risk, risk, size - risk, size - risk)

        c.drawArc(speedometerRect, getStartDegree().toFloat(), (getEndDegree() - getStartDegree()).toFloat(), false, tubeBacPaint)

        if (tickNumber > 0)
            drawTicks(c)
        else
            drawDefMinMaxSpeedPosition(c)
    }
}
