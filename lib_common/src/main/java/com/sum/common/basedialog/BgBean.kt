package com.sum.common.basedialog

import android.graphics.Color
import android.graphics.drawable.GradientDrawable

/**
 *
 * @author why
 * @date 2023/11/17 17:37
 */
class BgBean {
    var color //背景颜色
            = 0
    var left_top_radius //
            = 0f
    var right_top_radius //
            = 0f
    var right_bottom_radius //
            = 0f
    var left_bottom_radius //
            = 0f

    //渐变方向
    var gradientsOrientation = GradientDrawable.Orientation.TOP_BOTTOM
    var gradientsColors: IntArray? = null//渐变颜色

    fun getRoundRectDrawable(): GradientDrawable {
        //1. 圆角
        val radiuss = floatArrayOf( //左上、右上、右下、左下的圆角半径
            left_top_radius, left_top_radius,
            right_top_radius, right_top_radius,
            right_bottom_radius, right_bottom_radius,
            left_bottom_radius, left_bottom_radius
        )
        //2.渐变
        val drawable: GradientDrawable
        if (gradientsColors == null) {
            drawable = GradientDrawable()
            drawable.setColor(if (color != 0) color else Color.TRANSPARENT)
        } else {
            drawable = GradientDrawable(gradientsOrientation, gradientsColors)
        }
        drawable.cornerRadii = radiuss
        return drawable
    }
}