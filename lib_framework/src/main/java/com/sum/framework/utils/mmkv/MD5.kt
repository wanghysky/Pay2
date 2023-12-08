package com.fly.library.utils.mmkv

import java.io.UnsupportedEncodingException
import java.lang.IllegalStateException
import java.security.MessageDigest

/**
 *
 * @author why
 * @date 2023/7/12 23:10
 */
object MD5 {
    private var MD5_DIGEST: MessageDigest? = null
    val HEX_CHARS = "0123456789abcdef".toCharArray()

    @Synchronized
    fun bytes(bytes: ByteArray?): ByteArray {
        return MD5_DIGEST!!.digest(bytes)
    }

    fun bytes(string: String): ByteArray? {
        return bytes(toBytes(string))
    }

    fun hex(bytes: ByteArray?): String {
        return encodeHex(bytes(bytes))
    }

    fun hex(string: String): String {
        return hex(toBytes(string))
    }


    fun encodeHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v = bytes[j].toInt() and 255
            hexChars[j * 2] = HEX_CHARS[v ushr 4]
            hexChars[j * 2 + 1] = HEX_CHARS[v and 15]
        }
        return String(hexChars)
    }

    fun toBytes(string: String): ByteArray? {
        return try {
            string.toByteArray(charset("UTF-8"))
        } catch (var2: UnsupportedEncodingException) {
            throw IllegalStateException("UTF-8 encoding not supported by platform", var2)
        }
    }
}