package com.sum.network.interceptor

import com.sum.framework.utils.mmkv.KVStorage
import com.sum.network.constant.BASE_URL
import com.sum.network.manager.HttpManager
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response


/**
 *
 * @author why
 * @date 2023/11/19 14:38
 */
class BaseUrlInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // 修改 base URL
        val newurl = HttpManager.getBaseUrl()
        if(!newurl.isNullOrEmpty() && newurl != BASE_URL) {
            // 修改 base URL
            val newUrl: HttpUrl = originalRequest.url().newBuilder()
                .host(newurl)
                .build()

            // 创建一个新的 Request，并使用新的 URL
            val newRequest = originalRequest.newBuilder()
                .url(newUrl)
                .build()

            // 继续处理请求

            return chain.proceed(newRequest)
        }


        return chain.proceed(originalRequest)
    }
}