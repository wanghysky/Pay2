package com.sum.common.basedialog

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.FrameLayout

/**
 *
 * @author why
 * @date 2023/11/17 17:38
 */
class DialogRootView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var width //
            = 0
    private var height //
            = 0
    private var maxWidth //最大宽度
            = 0
    private var maxHeight //最大高度
            = 0
    private var left_top_radius //
            = 0f
    private var right_top_radius //
            = 0f
    private var right_bottom_radius //
            = 0f
    private var left_bottom_radius //
            = 0f
    private val path = Path() //

    init {
        init()
    }

    private fun init() {}
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (width == w && height == h) {
            return
        }
        width = w
        height = h
        setBgRadius()
    }

    /**
     * 设置背景圆角
     * @param bgRadius
     */
    fun setBgRadius(bgRadius: Float): DialogRootView {
        return setBgRadius(bgRadius, bgRadius, bgRadius, bgRadius)
    }

    fun setBgRadius(
        left_top_radius: Float,
        right_top_radius: Float,
        right_bottom_radius: Float,
        left_bottom_radius: Float
    ): DialogRootView {
        this.left_top_radius = left_top_radius
        this.right_top_radius = right_top_radius
        this.right_bottom_radius = right_bottom_radius
        this.left_bottom_radius = left_bottom_radius
        setBgRadius()
        return this
    }

    private fun setBgRadius(): DialogRootView {
        path.reset()
        path.addRoundRect(
            RectF(0f, 0f, width.toFloat(), height.toFloat()), floatArrayOf(
                left_top_radius, left_top_radius,
                right_top_radius, right_top_radius,
                right_bottom_radius, right_bottom_radius,
                left_bottom_radius, left_bottom_radius
            ),
            Path.Direction.CW
        )
        invalidate()
        return this
    }

    fun setMaxWidth(maxWidth: Int) {
        this.maxWidth = maxWidth
        invalidate()
    }

    fun setMaxHeight(maxHeight: Int) {
        this.maxHeight = maxHeight
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            getWidthMeasureSpec(widthMeasureSpec),
            getHeightMeasureSpec(heightMeasureSpec)
        )
    }

    //最大宽高处理
    fun getWidthMeasureSpec(widthMeasureSpec: Int): Int {
        var widthMeasureSpec = widthMeasureSpec
        if (maxWidth > 0) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST)
        }
        return widthMeasureSpec
    }

    fun getHeightMeasureSpec(heightMeasureSpec: Int): Int {
        var heightMeasureSpec = heightMeasureSpec
        if (maxHeight > 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST)
        }
        return heightMeasureSpec
    }

    //    // 绘制圆角
    //    @Override
    //    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
    //        canvas.clipPath(path);
    //        return super.drawChild(canvas, child, drawingTime);
    //    }
    override fun draw(canvas: Canvas) {
        canvas.clipPath(path)
        super.draw(canvas)
    }

    /** dp转px  */
    private fun dp2px(dpVal: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpVal,
            resources.displayMetrics
        ).toInt()
    }
}
