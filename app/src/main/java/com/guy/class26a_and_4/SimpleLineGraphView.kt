package com.guy.class26a_and_4

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.max
import kotlin.math.min

/**
 * A simple line graph view that displays smooth curved data points
 * with customizable colors and an interactive thumb indicator.
 */
class SimpleLineGraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Data
    private var dataPoints: FloatArray = floatArrayOf()
    private var minValue: Float? = null
    private var maxValue: Float? = null
    private var calculatedMin: Float = 0f
    private var calculatedMax: Float = 1f

    // Colors
    var trackColor: Int = Color.parseColor("#2DB3A0")
        set(value) {
            field = value
            linePaint.color = value
            updateGradient()
            invalidate()
        }

    var thumbColor: Int = Color.parseColor("#2DB3A0")
        set(value) {
            field = value
            thumbPaint.color = value
            invalidate()
        }

    var thumbBorderColor: Int = Color.WHITE
        set(value) {
            field = value
            thumbBorderPaint.color = value
            invalidate()
        }

    var gradientColor: Int? = null
        set(value) {
            field = value
            updateGradient()
            invalidate()
        }

    // Dimensions
    var lineWidth: Float = 8f
        set(value) {
            field = value
            linePaint.strokeWidth = value
            invalidate()
        }

    var thumbRadius: Float = 24f
        set(value) {
            field = value
            invalidate()
        }

    var thumbBorderWidth: Float = 6f
        set(value) {
            field = value
            thumbBorderPaint.strokeWidth = value
            invalidate()
        }

    // Thumb position (index in data array, supports fractional values for smooth positioning)
    var thumbPosition: Float = -1f
        set(value) {
            field = value.coerceIn(-1f, (dataPoints.size - 1).toFloat())
            invalidate()
        }

    // Enable touch to move thumb
    var touchEnabled: Boolean = true

    // Padding for the graph area
    var graphPaddingHorizontal: Float = 40f
    var graphPaddingTop: Float = 40f
    var graphPaddingBottom: Float = 20f

    // Smoothing factor for curves (0 = sharp corners, 0.5 = very smooth)
    var smoothingFactor: Float = 0.2f
        set(value) {
            field = value.coerceIn(0f, 0.5f)
            invalidate()
        }

    // Paints
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = lineWidth
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        color = trackColor
    }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = thumbColor
    }

    private val thumbBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = thumbBorderWidth
        color = thumbBorderColor
    }

    private val thumbShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.argb(40, 0, 0, 0)
    }

    // Paths
    private val linePath = Path()
    private val fillPath = Path()

    // Calculated points
    private val points = mutableListOf<PointF>()

    init {
        // Parse custom attributes if available
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.SimpleLineGraphView)
            try {
                trackColor = typedArray.getColor(R.styleable.SimpleLineGraphView_trackColor, trackColor)
                thumbColor = typedArray.getColor(R.styleable.SimpleLineGraphView_thumbColor, thumbColor)
                thumbBorderColor = typedArray.getColor(R.styleable.SimpleLineGraphView_thumbBorderColor, thumbBorderColor)
                lineWidth = typedArray.getDimension(R.styleable.SimpleLineGraphView_lineWidth, lineWidth)
                thumbRadius = typedArray.getDimension(R.styleable.SimpleLineGraphView_thumbRadius, thumbRadius)
                thumbBorderWidth = typedArray.getDimension(R.styleable.SimpleLineGraphView_thumbBorderWidth, thumbBorderWidth)
                smoothingFactor = typedArray.getFloat(R.styleable.SimpleLineGraphView_smoothingFactor, smoothingFactor)
                touchEnabled = typedArray.getBoolean(R.styleable.SimpleLineGraphView_touchEnabled, touchEnabled)

                if (typedArray.hasValue(R.styleable.SimpleLineGraphView_gradientColor)) {
                    gradientColor = typedArray.getColor(R.styleable.SimpleLineGraphView_gradientColor, trackColor)
                }
            } finally {
                typedArray.recycle()
            }
        }

        // Enable software layer for shadow
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    /**
     * Set the data points to display
     * @param data Array of float values representing the graph data
     * @param minVal Optional minimum value (auto-calculated if null)
     * @param maxVal Optional maximum value (auto-calculated if null)
     */
    fun setData(data: FloatArray, minVal: Float? = null, maxVal: Float? = null) {
        dataPoints = data
        minValue = minVal
        maxValue = maxVal
        calculateMinMax()

        // Set thumb to middle position if not set
        if (thumbPosition < 0 && data.isNotEmpty()) {
            thumbPosition = (data.size - 1) / 2f
        }

        requestLayout()
        invalidate()
    }

    /**
     * Set min and max values for the Y axis
     */
    fun setRange(minVal: Float?, maxVal: Float?) {
        minValue = minVal
        maxValue = maxVal
        calculateMinMax()
        invalidate()
    }

    private fun calculateMinMax() {
        if (dataPoints.isEmpty()) {
            calculatedMin = 0f
            calculatedMax = 1f
            return
        }

        calculatedMin = minValue ?: dataPoints.minOrNull() ?: 0f
        calculatedMax = maxValue ?: dataPoints.maxOrNull() ?: 1f

        // Ensure we have some range
        if (calculatedMax <= calculatedMin) {
            calculatedMax = calculatedMin + 1f
        }

        // Add some padding to auto-calculated values
        if (minValue == null && maxValue == null) {
            val range = calculatedMax - calculatedMin
            val padding = range * 0.1f
            calculatedMin -= padding
            calculatedMax += padding
        }
    }

    private fun updateGradient() {
        if (height <= 0) return

        val baseColor = gradientColor ?: trackColor
        val gradientShader = LinearGradient(
            0f, graphPaddingTop,
            0f, height.toFloat() - graphPaddingBottom,
            intArrayOf(
                Color.argb(80, Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor)),
                Color.argb(0, Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor))
            ),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
        fillPaint.shader = gradientShader
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateGradient()
        calculatePoints()
    }

    private fun calculatePoints() {
        points.clear()
        if (dataPoints.isEmpty() || width <= 0 || height <= 0) return

        val graphWidth = width - 2 * graphPaddingHorizontal
        val graphHeight = height - graphPaddingTop - graphPaddingBottom

        val range = calculatedMax - calculatedMin

        dataPoints.forEachIndexed { index, value ->
            val x = if (dataPoints.size > 1) {
                graphPaddingHorizontal + (index.toFloat() / (dataPoints.size - 1)) * graphWidth
            } else {
                width / 2f
            }

            val normalizedValue = (value - calculatedMin) / range
            val y = graphPaddingTop + graphHeight * (1 - normalizedValue)

            points.add(PointF(x, y))
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (points.isEmpty()) {
            calculatePoints()
        }

        if (points.size < 2) return

        // Build the smooth path
        buildSmoothPath()

        // Draw gradient fill
        canvas.drawPath(fillPath, fillPaint)

        // Draw the line
        canvas.drawPath(linePath, linePaint)

        // Draw thumb
        if (thumbPosition >= 0 && thumbPosition < dataPoints.size) {
            val thumbPoint = getPointAtPosition(thumbPosition)

            // Draw shadow
            canvas.drawCircle(thumbPoint.x + 2f, thumbPoint.y + 4f, thumbRadius, thumbShadowPaint)

            // Draw border
            canvas.drawCircle(thumbPoint.x, thumbPoint.y, thumbRadius, thumbBorderPaint)

            // Draw thumb
            canvas.drawCircle(thumbPoint.x, thumbPoint.y, thumbRadius - thumbBorderWidth / 2, thumbPaint)
        }
    }

    private fun buildSmoothPath() {
        linePath.reset()
        fillPath.reset()

        if (points.size < 2) return

        linePath.moveTo(points[0].x, points[0].y)

        // Use cubic bezier curves for smooth lines
        for (i in 0 until points.size - 1) {
            val p0 = if (i > 0) points[i - 1] else points[i]
            val p1 = points[i]
            val p2 = points[i + 1]
            val p3 = if (i + 2 < points.size) points[i + 2] else points[i + 1]

            // Calculate control points
            val cp1x = p1.x + (p2.x - p0.x) * smoothingFactor
            val cp1y = p1.y + (p2.y - p0.y) * smoothingFactor
            val cp2x = p2.x - (p3.x - p1.x) * smoothingFactor
            val cp2y = p2.y - (p3.y - p1.y) * smoothingFactor

            linePath.cubicTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y)
        }

        // Create fill path
        fillPath.addPath(linePath)
        fillPath.lineTo(points.last().x, height.toFloat() - graphPaddingBottom)
        fillPath.lineTo(points.first().x, height.toFloat() - graphPaddingBottom)
        fillPath.close()
    }

    /**
     * Get interpolated point at a fractional position
     */
    private fun getPointAtPosition(position: Float): PointF {
        if (points.isEmpty()) return PointF(0f, 0f)
        if (position <= 0) return points.first()
        if (position >= points.size - 1) return points.last()

        val index = position.toInt()
        val fraction = position - index

        val p1 = points[index]
        val p2 = points[min(index + 1, points.size - 1)]

        // For smooth interpolation along the curve, we need to account for the bezier
        // For simplicity, we use linear interpolation between calculated points
        // and then adjust Y based on the curve
        val x = p1.x + (p2.x - p1.x) * fraction

        // Get Y from the curve at this X position
        val y = getYAtX(x, index)

        return PointF(x, y)
    }

    /**
     * Approximate Y value at given X using the bezier curve
     */
    private fun getYAtX(targetX: Float, segmentIndex: Int): Float {
        if (points.size < 2) return points.firstOrNull()?.y ?: 0f

        val i = segmentIndex.coerceIn(0, points.size - 2)

        val p0 = if (i > 0) points[i - 1] else points[i]
        val p1 = points[i]
        val p2 = points[i + 1]
        val p3 = if (i + 2 < points.size) points[i + 2] else points[i + 1]

        val cp1x = p1.x + (p2.x - p0.x) * smoothingFactor
        val cp1y = p1.y + (p2.y - p0.y) * smoothingFactor
        val cp2x = p2.x - (p3.x - p1.x) * smoothingFactor
        val cp2y = p2.y - (p3.y - p1.y) * smoothingFactor

        // Find t for given X using binary search
        var tLow = 0f
        var tHigh = 1f
        var t = 0.5f

        repeat(10) {
            val x = cubicBezier(t, p1.x, cp1x, cp2x, p2.x)
            when {
                x < targetX -> tLow = t
                x > targetX -> tHigh = t
                else -> return cubicBezier(t, p1.y, cp1y, cp2y, p2.y)
            }
            t = (tLow + tHigh) / 2f
        }

        return cubicBezier(t, p1.y, cp1y, cp2y, p2.y)
    }

    private fun cubicBezier(t: Float, p0: Float, p1: Float, p2: Float, p3: Float): Float {
        val oneMinusT = 1 - t
        return oneMinusT * oneMinusT * oneMinusT * p0 +
                3 * oneMinusT * oneMinusT * t * p1 +
                3 * oneMinusT * t * t * p2 +
                t * t * t * p3
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!touchEnabled || dataPoints.isEmpty()) return super.onTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val x = event.x
                val graphWidth = width - 2 * graphPaddingHorizontal
                val relativeX = (x - graphPaddingHorizontal) / graphWidth
                thumbPosition = (relativeX * (dataPoints.size - 1)).coerceIn(0f, (dataPoints.size - 1).toFloat())

                // Notify listener
                onThumbPositionChanged?.invoke(thumbPosition, getValueAtPosition(thumbPosition))
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * Get the interpolated data value at a position
     */
    fun getValueAtPosition(position: Float): Float {
        if (dataPoints.isEmpty()) return 0f
        if (position <= 0) return dataPoints.first()
        if (position >= dataPoints.size - 1) return dataPoints.last()

        val index = position.toInt()
        val fraction = position - index
        val v1 = dataPoints[index]
        val v2 = dataPoints[min(index + 1, dataPoints.size - 1)]

        return v1 + (v2 - v1) * fraction
    }

    /**
     * Listener for thumb position changes
     */
    var onThumbPositionChanged: ((position: Float, value: Float) -> Unit)? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 400
        val desiredHeight = 200

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> min(desiredWidth, widthSize)
            else -> desiredWidth
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> min(desiredHeight, heightSize)
            else -> desiredHeight
        }

        setMeasuredDimension(width, height)
    }
}