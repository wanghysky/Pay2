package com.sum.framework.utils.mmkv

import com.sum.framework.base.BaseApp
import java.io.File
import java.io.IOException

/**
 *
 * @author why
 * @date 2023/7/12 23:10
 */
object FileStorageUtil {

    fun getAppTempPath(): String {
        return getBuPath("temp")
    }

    fun getPath(): String {
        return try {
            BaseApp.mContext.filesDir.canonicalPath
        } catch (e: IOException) {
            BaseApp.mContext.filesDir.absolutePath
        }
    }

    fun getBuPath(bizType: String?): String {
        if (bizType.isNullOrEmpty()) {
            return getPath()
        }
        val path = StringBuilder().append(getPath())
            .append(File.separator)
            .append(bizType)
            .toString()

        val file = File(path)

        if (!file.exists()) {
            file.mkdirs()
        }

        return path
    }
}