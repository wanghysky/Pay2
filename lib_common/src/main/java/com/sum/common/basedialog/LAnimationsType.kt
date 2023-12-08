package com.sum.common.basedialog

import androidx.annotation.StringDef

/**
 *
 * @author why
 * @date 2023/11/17 17:38
 */
public interface LAnimationsType {
    @StringDef(DEFAULT, LEFT, RIGHT, TOP, BOTTOM, SCALE)
    @Retention(AnnotationRetention.SOURCE)
    @Target(AnnotationTarget.VALUE_PARAMETER)
    annotation class LAnimationsType
    companion object {
        const val DEFAULT = "default"
        const val LEFT = "left"
        const val RIGHT = "right"
        const val TOP = "top"
        const val BOTTOM = "bottom"
        const val SCALE = "scale"
    }
}