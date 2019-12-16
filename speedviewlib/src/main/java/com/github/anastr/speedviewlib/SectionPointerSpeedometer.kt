package com.github.anastr.speedviewlib

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.github.anastr.speedviewlib.components.indicators.SpindleIndicator
/**
 * Created by Suyanwar on 2019-12-16.
 * Android Engineer
 **/
class SectionPointerSpeedometer @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : Speedometer(context, attrs, defStyleAttr) {

    private val markPath = Path()
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val speedometerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val markPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val speedometerRect = RectF()

    private val pointerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pointerBackPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var pointerColor = -0x1

    private var withPointer = true

    /**
     * change the color of the center circle (if exist),
     * **this option is not available for all Speedometers**.
     */
    private var centerCircleColor: Int
        get() = circlePaint.color
        set(centerCircleColor) {
            circlePaint.color = centerCircleColor
            if (!isAttachedToWindow)
                return
            invalidate()
        }

    init {
        init()
        initAttributeSet(context, attrs)
    }

    override fun defaultGaugeValues() {
        super.textColor = -0x1
        super.speedTextColor = -0x1
        super.unitTextColor = -0x1
        super.speedTextSize = dpTOpx(24f)
        super.unitTextSize = dpTOpx(11f)
        super.speedTextTypeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    override fun defaultSpeedometerValues() {
        super.setIndicator(SpindleIndicator(context)
                .setIndicatorWidth(dpTOpx(16f))
                .setIndicatorColor(-0x1))
        super.backgroundCircleColor = -0xb73317
        super.setSpeedometerWidth(dpTOpx(10f))
    }

    private fun init() {
        speedometerPaint.style = Paint.Style.STROKE
        speedometerPaint.strokeCap = Paint.Cap.ROUND
        markPaint.style = Paint.Style.STROKE
        markPaint.strokeCap = Paint.Cap.ROUND
        markPaint.strokeWidth = dpTOpx(2f)
        circlePaint.color = -0x1
    }

    private fun initAttributeSet(context: Context, attrs: AttributeSet?) {
        if (attrs == null) {
            initAttributeValue()
            return
        }
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.PointerSpeedometer, 0, 0)

        pointerColor = a.getColor(R.styleable.PointerSpeedometer_sv_pointerColor, pointerColor)
        circlePaint.color = a.getColor(R.styleable.PointerSpeedometer_sv_centerCircleColor, circlePaint.color)
        withPointer = a.getBoolean(R.styleable.PointerSpeedometer_sv_withPointer, withPointer)
        a.recycle()
        initAttributeValue()
    }

    private fun initAttributeValue() {
        pointerPaint.color = pointerColor
    }


    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)

        val risk = getSpeedometerWidth() * .5f + dpTOpx(8f) + padding.toFloat()
        speedometerRect.set(risk, risk, size - risk, size - risk)

        updateRadial()
        updateBackgroundBitmap()
    }

    private fun initDraw() {
        speedometerPaint.strokeWidth = getSpeedometerWidth()
        markPaint.color = markColor
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        initDraw()

        if (withPointer) {
            canvas.save()
            canvas.rotate(90 + degree, size * .5f, size * .5f)
            canvas.drawCircle(size * .5f, getSpeedometerWidth() * .5f + dpTOpx(8f) + padding.toFloat(), getSpeedometerWidth() * .5f + dpTOpx(8f), pointerBackPaint)
            canvas.drawCircle(size * .5f, getSpeedometerWidth() * .5f + dpTOpx(8f) + padding.toFloat(), getSpeedometerWidth() * .5f + dpTOpx(1f), pointerPaint)
            canvas.restore()
        }

        drawSpeedUnitText(canvas)
        drawIndicator(canvas)

        val c = centerCircleColor
        circlePaint.color = Color.argb((Color.alpha(c) * .5f).toInt(), Color.red(c), Color.green(c), Color.blue(c))
        canvas.drawCircle(size * .5f, size * .5f, widthPa / 14f, circlePaint)
        circlePaint.color = c
        canvas.drawCircle(size * .5f, size * .5f, widthPa / 22f, circlePaint)

        drawNotes(canvas)
    }

    override fun updateBackgroundBitmap() {
        val c = createBackgroundBitmapCanvas()
        initDraw()

        val markH = viewSizePa / 28f
        markPath.reset()
        markPath.moveTo(size * .5f, padding.toFloat())
        markPath.lineTo(size * .5f, markH + padding)
        markPaint.strokeWidth = markH / 3f

        val risk = getSpeedometerWidth() * .5f + padding
        speedometerRect.set(risk, risk, size - risk, size - risk)

        for (i in sections.size-1 downTo 0) {
            speedometerPaint.color = sections[i].color
            c.drawArc(speedometerRect, getStartDegree().toFloat(), (getEndDegree() - getStartDegree()) * sections[i].speedOffset, false, speedometerPaint)
        }

        c.save()
        c.rotate(90f + getStartDegree(), size * .5f, size * .5f)
        c.restore()

        if (tickNumber > 0)
            drawTicks(c)
        else
            drawDefMinMaxSpeedPosition(c)
    }

    private fun updateRadial() {
        val centerColor = Color.argb(160, Color.red(pointerColor), Color.green(pointerColor), Color.blue(pointerColor))
        val edgeColor = Color.argb(10, Color.red(pointerColor), Color.green(pointerColor), Color.blue(pointerColor))
        val pointerGradient = RadialGradient(size * .5f, getSpeedometerWidth() * .5f + dpTOpx(8f) + padding.toFloat(), getSpeedometerWidth() * .5f + dpTOpx(8f), intArrayOf(centerColor, edgeColor), floatArrayOf(.4f, 1f), Shader.TileMode.CLAMP)
        pointerBackPaint.shader = pointerGradient
    }

    fun setColorList(colorList : ArrayList<String>) {
        colorList.forEachIndexed { index, s ->
            try {
                sections[index].color = Color.parseColor(s)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}