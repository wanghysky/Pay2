package com.sum.framework.utils.mmkv

import android.text.TextUtils
import android.util.Base64
import com.fly.library.utils.mmkv.MD5
import com.sum.framework.base.BaseApp
import com.sum.framework.log.LogUtil
import com.tencent.mmkv.MMKV
import com.tencent.mmkv.MMKVHandler
import com.tencent.mmkv.MMKVLogLevel
import com.tencent.mmkv.MMKVRecoverStrategic
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author why
 * @date 2023/7/12 23:07
 */
object KVStorage  : IStorage {
    val TAG = "CTKVStorage"
    val EXPIRE_PREFIX = "__expire__"
    val EXPIRE_VALUE_STR = "_"

    var kvCaches: MutableMap<String, MutableMap<String, String>>? = null
    var mmkvMap: MutableMap<String, MMKV?>? = null
    var forbidClearAllKV = true

    init {
        if (mmkvMap == null) {
            MMKV.initialize(
               BaseApp.mContext,
                FileStorageUtil.getPath() + "/mmkv"
            )
            MMKV.registerHandler(object : MMKVHandler {
                override fun onMMKVCRCCheckFail(mmapID: String): MMKVRecoverStrategic {
                    val data: MutableMap<String, Any> = HashMap()
                    data["mmapID"] = mmapID
                    LogUtil.d("o_storage_mmkv_crc_fail")
                    return MMKVRecoverStrategic.OnErrorRecover
                }

                override fun onMMKVFileLengthError(mmapID: String): MMKVRecoverStrategic {
                    val data: MutableMap<String, Any> = HashMap()
                    data["mmapID"] = mmapID
                    LogUtil.d("o_storage_mmkv_length_fail")
                    return MMKVRecoverStrategic.OnErrorRecover
                }

                override fun wantLogRedirecting(): Boolean {
                    return false
                }

                override fun mmkvLog(
                    level: MMKVLogLevel,
                    file: String,
                    line: Int,
                    function: String,
                    message: String
                ) {
                    LogUtil.d(TAG + "_mmkvLog" +"$file-$line-$function-$message")
                    if (level == MMKVLogLevel.LevelError) {
                        val data: MutableMap<String, Any> = HashMap()
                        data["file"] = file
                        data["line"] = line
                        data["function"] = function
                        data["message"] = message
                        LogUtil.d("o_storage_mmkv_log")
                    }
                }
            })
        }
        mmkvMap = HashMap(4)
        kvCaches = HashMap(4)
    }


//    fun getInstance(): KVStorage? {
//        if (instance == null) {
//            synchronized(KVStorage::class.java) {
//                if (instance == null) {
//                    instance = KVStorage()
//                }
//            }
//        }
//        return instance
//    }

    override fun setString(
        domain: String,
        key: String,
        value: String,
        expire: Long,
        isSecret: Boolean,
        memOnly: Boolean
    ): Boolean {
        var domain = domain
        domain = makeDomainNonnull(domain)
        val result: Boolean = setKeyValue(domain, key, value, expire, isSecret, memOnly)
        if (!result) {
            val data: MutableMap<String, Any> = HashMap()
            data["k"] = key
            data["domain"] = domain
            data["v"] = wrapObjectValueForLog(value, String::class.java) ?: ""
            data["valueType"] = "String"
            LogUtil.d("o_kvs_set_fail")
            LogUtil.e( "setValueToMMKV String fail domain:$domain;key:$key")
        }
        return result
    }

    override fun setString(domain: String, key: String, value: String): Boolean {
        return setString(domain, key, value, -1, false, false)
    }

    fun setInt(domain: String?, key: String?, value: Int): Boolean {
        return setValueToMMKV(domain, key, value, Int::class.java)
    }

    fun setBoolean(domain: String?, key: String?, value: Boolean): Boolean {
        return setValueToMMKV(domain, key, value, Boolean::class.java)
    }

    fun setFloat(domain: String?, key: String?, value: Float): Boolean {
        return setValueToMMKV(domain, key, value, Float::class.java)
    }

    fun setLong(domain: String?, key: String?, value: Long): Boolean {
        return setValueToMMKV(domain, key, value, Long::class.java)
    }

    fun setStringSet(domain: String?, key: String?, value: Set<String?>?): Boolean {
        return setValueToMMKV(domain, key, value, MutableSet::class.java)
    }

    fun setBytes(domain: String?, key: String?, value: ByteArray?): Boolean {
        return setValueToMMKV(domain, key, value, Byte::class.java)
    }

    open fun setValueToMMKV(domain: String?, key: String?, value: Any?, type: Class<*>?): Boolean {
        val domain = domain ?: ""
        val key = key ?: ""
        val value = value ?: ""

        var keyResult = false
        val mmkv: MMKV? = getMMKVByDomain(domain)
        if (mmkv != null) {
            keyResult = if (type == String::class.java) {
                mmkv.encode(key, value as String)
            } else if (type == Int::class.java) {
                mmkv.encode(key, value as Int)
            } else if (type == Boolean::class.java) {
                mmkv.encode(key, value as Boolean)
            } else if (type == Float::class.java) {
                mmkv.encode(key, value as Float)
            } else if (type == Long::class.java) {
                mmkv.encode(key, value as Long)
            } else if (type == MutableSet::class.java) {
                mmkv.encode(key, value as Set<String?>)
            } else if (type == Byte::class.java) {
                mmkv.encode(key, value as ByteArray)
            } else {
                throw IllegalArgumentException("not support type:" + if (type == null) "" else type.simpleName)
            }
            if (!keyResult) {
                val data: MutableMap<String, Any> = HashMap()
                data["k"] = key
                data["domain"] = domain
                data["v"] = wrapObjectValueForLog(value, type) ?: ""
                data["valueType"] = type.simpleName
                LogUtil.e("o_kvs_set_fail")
                LogUtil.e( "setValueToMMKV fail domain:$domain;key:$key")
            }
        } else {
            LogUtil.e(
                 String.format(
                    "setValueToMMKV domain:%s;key:%s;type:%s error mmkv is null", domain, key,
                    if (type == null) "" else type.simpleName
                )
            )
        }
        return keyResult
    }


    override fun getString(
        domain: String,
        key: String,
        defaultValue: String,
        isSecret: Boolean
    ): String? {
        var domain = domain
        var value = defaultValue
        if (containsStringKey(domain, key, isSecret)) {
            domain = makeDomainNonnull(domain)
            value = doGet(domain, key, isSecret) ?: defaultValue
        }
        return value
    }

    override fun getString(domain: String, key: String, defaultValue: String): String? {
        return getString(domain, key, defaultValue, false)
    }


    fun getInt(domain: String, key: String, defaultValue: Int): Int {
        var result = defaultValue
        if (contains(domain, key)) {
            val mmkv: MMKV? = getMMKVByDomain(domain)
            if (mmkv != null) {
                result = mmkv.decodeInt(key, defaultValue)
            }
        }
        return result
    }

    fun getBoolean(domain: String, key: String, defaultValue: Boolean): Boolean {
        var result = defaultValue
        if (contains(domain, key)) {
            val mmkv: MMKV? = getMMKVByDomain(domain)
            if (mmkv != null) {
                result = mmkv.decodeBool(key, defaultValue)
            }
        }
        return result
    }

    fun getFloat(domain: String, key: String, defaultValue: Float): Float {
        var result = defaultValue
        if (contains(domain, key)) {
            val mmkv: MMKV? = getMMKVByDomain(domain)
            if (mmkv != null) {
                result = mmkv.decodeFloat(key, defaultValue)
            }
        }
        return result
    }

    fun getLong(domain: String, key: String, defaultValue: Long): Long {
        var result = defaultValue
        if (contains(domain, key)) {
            val mmkv: MMKV? = getMMKVByDomain(domain)
            if (mmkv != null) {
                result = mmkv.decodeLong(key, defaultValue)
            }
        }
        return result
    }

    fun getStringSet(domain: String, key: String, defaultValue: Set<String?>?): Set<String?>? {
        var result = defaultValue
        if (contains(domain, key)) {
            val mmkv: MMKV? = getMMKVByDomain(domain)
            if (mmkv != null) {
                result = mmkv.decodeStringSet(key, defaultValue)
            }
        }
        return result
    }

    fun getBytes(domain: String, key: String, defaultValue: ByteArray?): ByteArray? {
        var result = defaultValue
        val mmkv: MMKV? = getMMKVByDomain(domain)
        if (mmkv != null) {
            result = mmkv.decodeBytes(key, defaultValue)
        }
        return result
    }

    override fun remove(domain: String, key: String) {
        remove(domain, key, false)
    }

    /**
     * 首先删除MMKV的kv，然后删除SP中的。
     * @param domain
     * @param key
     * @param isSecret
     */
    override fun remove(domain: String, key: String, isSecret: Boolean) {
        if (key.isEmpty()) return
        onlyRemoveKeyFromMMKV(domain, key, isSecret)
    }

    override fun removeAllKeysByDomain(domain: String) {
        var domain  =domain
        domain = makeDomainNonnull(domain)
        //删除mmkv
        //删除mmkv
        val targetMMKV = getMMKVByDomain(domain)
        targetMMKV?.clearAll()

        //删除mapCache

        //删除mapCache
        val keyValueCache = getKVCacheMapByDomain(domain) as? MutableMap
        keyValueCache?.clear()
    }

    /**
     * 仅仅删除MMKV中的kv
     * 不仅删除key，同时删除存储的过期时间
     * @param domain
     * @param key
     * @param isSecret
     */
    fun onlyRemoveKeyFromMMKV(domain: String?, key: String?, isSecret: Boolean) {
        var domain = domain
        var key = key
        if (key.isNullOrEmpty()) return

        //需要移除key自己和可能存储的过期的key
        domain = makeDomainNonnull(domain)
        key = encryptKey(key, isSecret)
        val expireKey: String = getExpirePrefixKey(key)
        doRemove(domain, key, expireKey)
    }

    fun onlyRemoveAllKeyByDomainFromMMKV(domain: String): Int {
        var domain = domain
        var removeLines = -1
        domain = makeDomainNonnull(domain)
        //删除mmkv
        val targetMMKV: MMKV? = getMMKVByDomain(domain)
        if (targetMMKV != null) {
            removeLines = 0
            val allKeys = targetMMKV.allKeys()
            if (allKeys != null && allKeys.size > 0) {
                for (i in allKeys.indices) {
                    if (allKeys[i] != null && !allKeys[i]!!.startsWith(EXPIRE_PREFIX)) {
                        removeLines = removeLines + 1
                    }
                }
            }
            targetMMKV.clearAll()
        }

        //删除mapCache
        val keyValueCache: MutableMap<String, String>? = getKVCacheMapByDomain(domain) as? MutableMap<String, String>
        if (keyValueCache != null) {
            keyValueCache.clear()
        }
        return removeLines
    }


    /**
     * 非必要禁止使用
     * 强制清空所有MMKV存储。
     */
    fun forceClearAllKV() {
        forbidClearAllKV = false
    }

    override fun clearAll() {
        //非必要，禁止清空所有MMKV数据
        if (forbidClearAllKV) {
            return
        }
        //清空mmkv
        val mmkvs: Collection<MMKV?> = mmkvMap!!.values
        for (mmkv in mmkvs) {
            mmkv?.clearAll()
        }

        //清空cache
        val kvs: Collection<MutableMap<String, String>> = kvCaches!!.values
        for (kv in kvs) {
            kv.clear()
        }
        forbidClearAllKV = true
    }

    /**
     * 可以在内存不足的时候主动清除mmkv的内存缓存
     */
    fun clearMMKVMemoryCache() {
        val values: Collection<MMKV?> = mmkvMap!!.values
        for (mmkv in values) {
            mmkv?.clearMemoryCache()
        }
    }

    open fun setKeyValue(
        domain: String,
        key: String,
        value: String,
        expire: Long,
        isSecret: Boolean,
        memOnly: Boolean
    ): Boolean {
        if (key.isEmpty()) {
            logError(key, value, "key is empty")
            return false
        }

        //获取对应的缓存map
        val keyValueCache: MutableMap<String, String> ?= getKVCacheMapByDomain(domain, true) as? MutableMap<String, String>?
        val encryptKey: String = encryptKey(key, isSecret)

        //处理超时过期判断
        val expiredKey: String = getExpirePrefixKey(encryptKey)
        var expiredValue: String? = null
        if (expire > -1) {
            expiredValue = expire.toString() + EXPIRE_VALUE_STR + System.currentTimeMillis()
        } else {
            //如果这个key不超时了，那么要移除可能之前设置的超时内容
            doRemove(domain, expiredKey)
        }
        return if (memOnly) { //如果只保存内容
            keyValueCache?.set(encryptKey, value)
            if (expiredValue != null) {
                keyValueCache?.set(expiredKey, expiredValue)
            }
            true
        } else { //不主动进行内存缓存
            //移除可能存在的内存缓存
            keyValueCache?.remove(encryptKey)
            keyValueCache?.remove(expiredKey)
            val mmkv: MMKV?= getMMKVByDomain(domain)
            val wrapValue: String? = encryptValue(value, isSecret)
            val keyResult = mmkv?.encode(encryptKey, wrapValue) ?: false
            var expireResult = true
            if (expiredValue != null) {
                expireResult = mmkv?.encode(expiredKey, expiredValue) ?: false
            }
            keyResult && expireResult
        }
    }

    open fun doGet(domain: String, key: String?, isSecret: Boolean): String? {
        if (key.isNullOrEmpty()) {
            return ""
        }
        val keyValueCache: Map<String, String>? = getKVCacheMapByDomain(domain)
        var value: String?
        var cacheExpireValue: String? = ""
        var cacheValue: String? = null
        val encryptKey: String = encryptKey(key, isSecret)
        if (keyValueCache != null) {
            cacheValue = keyValueCache[encryptKey]
        }
        val expireKey: String = getExpirePrefixKey(encryptKey)
        if (cacheValue?.isNotEmpty() == true) { //从缓存获取
            value = cacheValue
            if (keyValueCache != null) {
                cacheExpireValue = keyValueCache[expireKey]
            }
        } else { //从mmkv获取
            val mmkv: MMKV = getMMKVByDomain(domain) ?: return null
            value = mmkv.decodeString(encryptKey)
            if (value == null) {
                return null
            }
            if (isSecret) {
                value = translateValue(value)
            }
            cacheExpireValue = mmkv.decodeString(expireKey)
            //mmkv方案不主动缓存获取到的数据
//            if (keyValueCache != null && !(StringUtil.isEmpty(value))) {
//                keyValueCache.put(encryptKey, value);
//                keyValueCache.put(expireKey, cacheExpireValue);
//            }
        }
        if (isDataOutOfDate(cacheExpireValue ?: "")) {
            doRemove(domain, encryptKey, expireKey)
            val params: MutableMap<String, Any> = HashMap()
            params["expire"] = "1"
            params["type"] = "mmkv"
            LogUtil.d("o_storage_get_fail ${params.toString()}")
            return null
        }
        return value
    }

    /**
     * 进行移除操作
     * 会移除cache和mmkv
     *
     * @param realKeys 真实保存的key
     */
    open fun doRemove(domain: String, vararg realKeys: String) {

        //cache remove
        val keyValueCache: MutableMap<String, String>? = getKVCacheMapByDomain(domain) as? MutableMap<String, String>?
        if (keyValueCache != null) {
            for (key in realKeys) {
                keyValueCache.remove(key)
            }
        }

        //mmkv remove
        val targetDomain: MMKV? = getMMKVByDomain(domain)
        if (targetDomain != null) {
            targetDomain.removeValuesForKeys(realKeys)
        }
    }


    /**
     * 根据domain获取对应的mmkv 可能为null
     */
    open fun getMMKVByDomain(domain: String): MMKV? {
        val mmapId: String = generateMmapID(domain)
        var mmkv = mmkvMap!![domain]
        if (mmkv == null) {
            synchronized(KVStorage::class.java) {
                mmkv = MMKV.mmkvWithID(mmapId, MMKV.MULTI_PROCESS_MODE)
                mmkvMap!!.put(domain, mmkv)
            }
        }
        return mmkv
    }

    open fun generateMmapID(domain: String): String {
        return "ctrip_$domain"
    }

    //For debug
    fun getAllMMKVDomain(): List<String>? {
        return if (mmkvMap == null || mmkvMap!!.isEmpty()) null else ArrayList(mmkvMap!!.keys)
    }

    //For debug
//    fun getMMKVKvDetailByDomain(domain: String): List<MMKVFlipperDetail>? {
//        if (mmkvMap == null || mmkvMap!!.isEmpty() || TextUtils.isEmpty(domain)) return null
//        val mmkvByDomain = getMMKVByDomain(domain) ?: return null
//        val keys = mmkvByDomain.allKeys() ?: return null
//        val result: MutableList<MMKVFlipperDetail> = ArrayList<MMKVFlipperDetail>()
//        for (key in keys) {
//            if (key.startsWith(EXPIRE_PREFIX)) {
//                continue
//            }
//            val keyExpireTime: Long = getKeyExpireTime(mmkvByDomain, key)
//            if (keyExpireTime == -1L) {
//                continue
//            }
//            val mmkvFlipperDetail = MMKVFlipperDetail(
//                key,
//                getObjectValue(mmkvByDomain, key),
//                keyExpireTime
//            )
//            result.add(mmkvFlipperDetail)
//        }
//        return result
//    }

    open fun getKeyExpireTime(mmkvByDomain: MMKV?, key: String): Long {
        if (TextUtils.isEmpty(key) || mmkvByDomain == null) return 0
        val expireKey: String = getExpirePrefixKey(key)
        val expireValue = mmkvByDomain.decodeString(expireKey)
        if (TextUtils.isEmpty(expireValue)) {
            return 0
        }
        try {
            val expires =
                expireValue!!.split(EXPIRE_VALUE_STR.toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            if (expires != null && expires.size == 2) {
                val expireTime = expires[0].toLong()
                val startTime = expires[1].toLong()
                val duration = expireTime * 1000 + startTime - System.currentTimeMillis()
                return if (duration > 0) {
                    duration
                } else {
                    -1
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    fun getObject(domain: String, key: String): Any? {
        if (mmkvMap == null || mmkvMap!!.isEmpty()
            || TextUtils.isEmpty(domain) || TextUtils.isEmpty(key)
        ) return null
        val mmkvByDomain = getMMKVByDomain(domain) ?: return null
        return getObjectValue(mmkvByDomain, key)
    }

    //For debug
    open fun getObjectValue(mmkv: MMKV, key: String): Any? {
        // 因为其他基础类型value会读成空字符串,所以不是空字符串即为string or string-set类型
        val value = mmkv.decodeString(key)
        if (!TextUtils.isEmpty(value)) {
            // 判断 string or string-set
            return if (value!![0].code == 0x01) {
                mmkv.decodeStringSet(key)
            } else {
                value
            }
        }
        // float double类型可通过string-set配合判断
        // 通过数据分析可以看到类型为float或double时string类型为空字符串且string-set类型读出空数组
        // 最后判断float为0或NAN的时候可以直接读成double类型,否则读float类型
        // 该判断方法对于非常小的double类型数据 (0d < value <= 1.0569021313E-314) 不生效
        val set = mmkv.decodeStringSet(key)
        if (set != null && set.size == 0) {
            val valueFloat = mmkv.decodeFloat(key)
            val valueDouble = mmkv.decodeDouble(key)
            return if (java.lang.Float.compare(valueFloat, 0f) == 0 || java.lang.Float.compare(
                    valueFloat,
                    Float.NaN
                ) == 0
            ) {
                valueDouble
            } else {
                valueFloat
            }
        }
        // int long bool 类型的处理放在一起, int类型1和0等价于bool类型true和false
        // 判断long或int类型时, 如果数据长度超出int的最大长度, 则long与int读出的数据不等, 可确定为long类型
        val valueInt = mmkv.decodeInt(key)
        val valueLong = mmkv.decodeLong(key)
        return if (valueInt.toLong() != valueLong) {
            valueLong
        } else {
            valueInt
        }
    }

    /**
     * 根据domain获取对应的缓存map
     *
     * @return 可能为空
     */
    open fun getKVCacheMapByDomain(domain: String): Map<String, String>? {
        return getKVCacheMapByDomain(domain, false)
    }

    /**
     * 根据domain获取对应的缓存map
     *
     * @param notNull 是否为空 set的时候不为空，没有就去创建 其他情况一般设置false，避免没必要的资源创建
     */
    open fun getKVCacheMapByDomain(domain: String, notNull: Boolean): Map<String, String>? {
        var map = kvCaches!![domain]
        if (map == null && notNull) {
            synchronized(KVStorage::class.java) {
                map = ConcurrentHashMap()
                kvCaches!!.put(domain, map as ConcurrentHashMap<String, String>)
            }
        }
        return map
    }

    open fun encryptKey(originalString: String, isSecret: Boolean): String {
        return if (isSecret) {
            MD5.hex(originalString)
        } else originalString
    }

    open fun encryptValue(originalString: String, isSecret: Boolean): String? {
        if (TextUtils.isEmpty(originalString)) {
            return ""
        }
        return if (isSecret) {
            val encodeBytes: ByteArray = EncodeUtil.Encode(originalString.toByteArray())
            if (encodeBytes == null) {
                LogUtil.e( "error when encode encode")
                return ""
            }
            Base64.encodeToString(encodeBytes, Base64.NO_WRAP)
        } else {
            originalString
        }
    }

    open fun translateValue(wrapedString: String): String? {
        if (TextUtils.isEmpty(wrapedString)) {
            return ""
        }
        val unBase64Byte = Base64.decode(wrapedString, Base64.NO_WRAP)
        val decodeBytes: ByteArray = EncodeUtil.Decode(unBase64Byte)
        if (decodeBytes == null) {
            LogUtil.e( "error when decode value")
            return ""
        }
        return String(decodeBytes)
    }

    /**
     * 获取过期时间的key
     */
    open fun getExpirePrefixKey(key: String): String {
        return EXPIRE_PREFIX + key
    }

    open fun isDataOutOfDate(expireValue: String): Boolean {
        if (expireValue.isNotEmpty()) {
            try {
                val expires =
                    expireValue.split(EXPIRE_VALUE_STR.toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                if (expires != null && expires.size == 2) {
                    val expireTime = expires[0].toLong()
                    val startTime = expires[1].toLong()
                    return expireTime * 1000 + startTime <= System.currentTimeMillis()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    open fun logError(key: String, value: String, errorMsg: String) {
        val userInfo: MutableMap<String, String> = HashMap()
        userInfo["key"] = key
        userInfo["value"] = wrapObjectValueForLog(value, String::class.java) ?: ""
        userInfo["errorMsg"] = errorMsg
        userInfo["type"] = "mmkv"
        LogUtil.e("o_storage_result_exception  ${userInfo}")
    }

    open fun wrapValueForLog(value: String?): String? {
        return if (value != null && value.length > 512) "value length is too long:" + value.length + ";__subString 511 is:" + value.substring(
            0,
            511
        ) else value
    }

    open fun wrapObjectValueForLog(value: Any?, valueType: Class<*>): String? {
        if (value == null) return ""
        var result: String? = ""
        try {
            result = if (value is String || valueType == String::class.java) {
                wrapValueForLog(value as String?)
            } else if (value is Set<*> || valueType == MutableSet::class.java) {
                val setArr = value as Set<String>?
                val stringBuilder = StringBuilder()
                if (setArr != null && setArr.size > 0) {
                    for (key in setArr) {
                        stringBuilder.append(key)
                        stringBuilder.append(",")
                    }
                }
                wrapValueForLog(stringBuilder.toString())
            } else if (valueType == Byte::class.java) {
                Base64.encodeToString(value as ByteArray?, Base64.NO_WRAP)
            } else {
                value.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    open fun makeDomainNonnull(domain: String?): String {
        return domain ?: ""
    }

    override fun contains(domain: String, key: String): Boolean {
        val mmkvByDomain = getMMKVByDomain(domain)
        return mmkvByDomain?.contains(key) ?: false
    }

    /**
     * 仅setString支持isSecret和memOnly设置，所以getString的contains判断需要特殊处理
     * 首先判断 非secret和非memOnly的情况，
     * 然后再分别判断isSecret和memOnly的情况
     * @param domain
     * @param key
     * @param isSecret
     * @return
     */
    fun containsStringKey(domain: String, key: String?, isSecret: Boolean): Boolean {
        var contains = false
        if (key == null) return contains
        contains = contains(domain, key)
        if (contains) return contains

        //memOnly 判断
        val keyValueCache = getKVCacheMapByDomain(domain, true)
        val encryptKey = encryptKey(key, isSecret)
        if (keyValueCache != null) {
            contains = keyValueCache.containsKey(encryptKey)
        }
        if (contains) return contains

        //isSecret 判断
        if (isSecret) {
            val mmkvByDomain = getMMKVByDomain(domain)
            if (mmkvByDomain != null) {
                contains = mmkvByDomain.contains(encryptKey)
            }
        }
        return contains
    }

    /**
     * 获取MMKV中的string value，不存在则返回default value
     * @param domain
     * @param key
     * @param defaultValue
     * @param isSecret
     * @return
     */
    fun getStringValueFromMMKV(
        domain: String,
        key: String?,
        defaultValue: String?,
        isSecret: Boolean
    ): String? {
        var domain = domain
        var value = defaultValue
        if (containsStringKey(domain, key, isSecret)) {
            domain = makeDomainNonnull(domain)
            value = doGet(domain, key, isSecret)
            value = if (value == null) defaultValue else value
        }
        return value
    }

    /**
     * 仅获取MMKV中所有的Key
     * @param domain
     * @return
     */
    fun getMMKVAllKeys(domain: String): Array<String?>? {
        var allKeys: Array<String?>? = null
        val mmkvByDomain = getMMKVByDomain(domain)
        if (mmkvByDomain != null) {
            allKeys = mmkvByDomain.allKeys()
        }
        return allKeys
    }

    /**
     * MMKV中固定用String的方式去读，必须保证MMKV存进去的都是String
     * MMKV中不存在，则读SP
     * @param domain
     * @return
     */
    @Deprecated("")
    fun getAllStringValue(domain: String): Map<String, *>? {
        val mmkvByDomain = getMMKVByDomain(domain)
        var result: Map<String, *>? = null
        if (mmkvByDomain != null) {
            val mmkvAll: MutableMap<String, Any?> = HashMap()
            val allKeys = mmkvByDomain.allKeys()
            if (allKeys != null && allKeys.size > 0) {
                for (i in allKeys.indices) {
                    mmkvAll[allKeys[i]] = mmkvByDomain.decodeString(allKeys[i], "")
                }
            }
            if (!mmkvAll.isEmpty()) {
                result = HashMap(mmkvAll)
            }
        }
        return result
    }

    fun isMMKVContainsDomain(domain: String): Boolean {
        return getMMKVByDomain(domain) != null
    }

    fun getAllStringValueFromMMKV(domain: String): Map<String?, String?>? {
        val mmkvByDomain = getMMKVByDomain(domain)
        var result: MutableMap<String?, String?>? = null
        if (mmkvByDomain != null) {
            result = HashMap()
            val allKeys = mmkvByDomain.allKeys()
            if (allKeys != null && allKeys.size > 0) {
                for (i in allKeys.indices) {
                    if (allKeys[i] != null && !allKeys[i]!!.startsWith(EXPIRE_PREFIX)) {
                        result[allKeys[i]] = mmkvByDomain.decodeString(allKeys[i], "")
                    }
                }
            }
        }
        return result
    }

}