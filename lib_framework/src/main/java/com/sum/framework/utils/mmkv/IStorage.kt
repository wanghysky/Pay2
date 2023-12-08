package com.sum.framework.utils.mmkv

/**
 *
 * @author why
 * @date 2023/7/12 23:07
 */
open interface IStorage {
    /**
     * 保存一对kv数据
     *
     * @param domain   保存的文件
     * @param key      对应的键
     * @param value    对应的值
     * @param expire   过期时间,单位：秒 默认 不过期
     * @param isSecret 是否加密保存 默认不加密
     * @param memOnly  是否仅保存到内存 默认false，保存到文件
     */
    open fun setString(
        domain: String,
        key: String,
        value: String,
        expire: Long,
        isSecret: Boolean,
        memOnly: Boolean
    ): Boolean

    open fun setString(domain: String, key: String, value: String): Boolean

    /**
     * 获取一个数据
     *
     * @param domain       保存的文件
     * @param key          对应的键
     * @param defaultValue 默认值
     * @param isSecret     是否加密 默认false
     */
    open fun getString(
        domain: String,
        key: String,
        defaultValue: String,
        isSecret: Boolean
    ): String?

    open fun getString(domain: String, key: String, defaultValue: String): String?


    open fun remove(domain: String, key: String)

    /**
     * 移除一个键值对
     */
    open fun remove(domain: String, key: String, isSecret: Boolean)

    /**
     * 移除一个domain的所有数据
     */
    open fun removeAllKeysByDomain(domain: String)

    /**
     * 判断domain是否存在这个key
     * @param domain
     * @param key
     * @return
     */
    open fun contains(domain: String, key: String): Boolean

    /**
     * 清空所有domain的数据
     */
    open fun clearAll()
}