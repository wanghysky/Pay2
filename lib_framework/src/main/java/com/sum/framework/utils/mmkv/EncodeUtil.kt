package com.sum.framework.utils.mmkv

import android.content.Context

/**
 *
 * @author why
 * @date 2023/7/12 23:09
 */
object EncodeUtil {

    var classVerify = false //只能为false不能变为True

    private var isTest = false

    /**
     * 加密
     *
     * @param bytes
     * @return
     */
    fun Encode(bytes: ByteArray): ByteArray {
        return bytes
    }

    /**
     * 解密
     *
     * @param bytes
     * @return
     */
    fun Decode(bytes: ByteArray): ByteArray {
        return bytes
    }


    fun setInfo(isTest: Boolean, context: Context?) {
        EncodeUtil.isTest = isTest
    }


}