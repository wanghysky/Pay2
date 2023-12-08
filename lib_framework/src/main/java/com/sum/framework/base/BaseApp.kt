package com.sum.framework.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.sum.framework.utils.DeviceInfoUtils

/**
 *
 * @author why
 * @date 2023/11/17 17:42
 */
open class BaseApp: Application() {

    override fun onCreate() {
        super.onCreate()
        mContext = this
        DeviceInfoUtils.init(mContext)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context
    }

}