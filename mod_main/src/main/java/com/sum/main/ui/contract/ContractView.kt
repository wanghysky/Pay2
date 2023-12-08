package com.sum.main.ui.contract

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View


/**
 *
 * @author why
 * @date 2023/11/28 23:08
 */
class ContractView@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var paint: Paint? = null
    private var paint2: Paint? = null
    private var path: Path? = null

    init {
        paint = Paint()
        paint!!.color = Color.BLACK
        paint!!.style = Paint.Style.FILL

        paint2 = Paint()
        paint2!!.color = Color.WHITE
        paint2!!.style = Paint.Style.FILL

        path = Path()
    }

//    override fun onDraw(canvas: Canvas?)  {
//        super.onDraw(canvas)
//        val width = width
//        val height = height
//
//        // 绘制半弧形
//
//        // 绘制半弧形
//        path!!.moveTo(0f, (height / 2).toFloat())
//        path!!.arcTo(RectF(0f, 0f, width.toFloat(), height.toFloat()), -90f, 180f)
//        path!!.lineTo(width.toFloat(), (height / 2).toFloat())
//        path!!.close()
//
//        // 绘制长方形
//
//        // 绘制长方形
//        canvas!!.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint!!)
//
//
//        // 计算半圆的半径
//        val radius = Math.min(width, height) / 2
//
//        // 绘制半圆
//
//        // 绘制半圆
//        canvas.drawArc(RectF(0f, height.toFloat()/2, width.toFloat(), height.toFloat() ), 180f, 180f, true, paint2!!)
////        canvas!!.drawPath(path!!, paint2!!)
//    }

}