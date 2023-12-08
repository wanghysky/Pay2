package com.sum.network.interceptor

import android.text.TextUtils
import com.sum.common.provider.LoginServiceProvider
import com.sum.framework.BuildConfig
import com.sum.framework.log.LogUtil
import com.sum.framework.utils.AESUtil
import com.sum.network.constant.ARTICLE_WEBSITE
import com.sum.network.constant.COIN_WEBSITE
import com.sum.network.constant.COLLECTION_WEBSITE
import com.sum.network.constant.KEY_COOKIE
import com.sum.network.constant.NOT_COLLECTION_WEBSITE
import com.sum.network.manager.CookiesManager
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import org.json.JSONObject
import java.io.IOException


/**
 * @author mingyan.su
 * @date   2023/3/27 07:25
 * @desc   头信息拦截器
 * 添加头信息
 */
class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newBuilder = request.newBuilder()
        newBuilder.addHeader("Content-type", "application/json; charset=utf-8")
        newBuilder.addHeader("x-version", "1.0.0")
        newBuilder.addHeader("x-package-name", "com.fintek.id.market")
        if(LoginServiceProvider.isLogin()) {
            newBuilder.addHeader("x-auth-token", LoginServiceProvider.getLoginInfo() ?.token ?: "")
        }

        val host = request.url().host()
        val url = request.url().toString()

        //给有需要的接口添加Cookies
        if (!host.isNullOrEmpty()  && (url.contains(COLLECTION_WEBSITE)
                        || url.contains(NOT_COLLECTION_WEBSITE)
                        || url.contains(ARTICLE_WEBSITE)
                        || url.contains(COIN_WEBSITE))) {
            val cookies = CookiesManager.getCookies()
            LogUtil.e("HeaderInterceptor:cookies:$cookies", tag = "smy")
            if (!cookies.isNullOrEmpty()) {
                newBuilder.addHeader(KEY_COOKIE, cookies)
            }
        }
        return chain.proceed(newBuilder.build())
    }
}

val logInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        // 使用自己的日志工具接管
        LogUtil.d(message)
    }
}).setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.BASIC)


class DataEncryptInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        //请求
        var request = chain.request()
        val mediaType = MediaType.parse("text/plain; charset=utf-8")
        //随机生成AES秘钥
        try {
            //获取未加密数据
            val oldRequestBody: RequestBody? = request.body()
            val requestBuffer = Buffer()
            oldRequestBody?.writeTo(requestBuffer)
            val oldBodyStr: String = requestBuffer.readUtf8()
            requestBuffer.close()

            //未加密数据用AES秘钥加密
            val content = JSONObject()
            content.put("mobile", "81991419936")
            content.put("type", "text")

            val content2 = JSONObject()
            content2.put("BFBPY", content.toString())

            val newBodyStr = AESUtil.encrypt(content.toString(), AESUtil.base64ToDecode(AESUtil.paramKey))
//            val newBodyStr: String = EncryptionManager.getInstance().publicEncryptClient(oldBodyStr)
            //AES秘钥用服务端RSA公钥加密
//            val key: String = EncryptionManager.getInstance().publicEncrypt(aesKey)
            //构成新的request 并通过请求头发送加密后的AES秘钥

            val token = JSONObject()
            token.put("sourceChannel", "Orange")
            token.put("packageName", "com.tcssj.mbjmb")
            token.put("adid", "")
            token.put("version", "12.0.0")
            token.put("uuId", "")
            token.put("userId", "")
            val tokenAES = AESUtil.encrypt(token.toString() , AESUtil.base64ToDecode(AESUtil.tokenKey))
//            val tokenAES = "jbyBeTvd4CPH/cjXqR1KXX/GGVRPcV8ra3WZwy9L7kfhmpvL8J1xnz5TemikDJGRMFALQzkaKq3Dy88SRgff2Nyeu32HeKj8YJLOlZNkw7DaaLuKllls1VPExStuPY9/DIdvIe3eni9qF8Fc7WMn8A=="

            val headers = request.headers()
            val head = headers.newBuilder().add("packageName", "mbjmb")
            head.add("HCFQ", tokenAES.trim())

            val newBody = RequestBody.create(mediaType, newBodyStr)
            //构造新的request
            request = request.newBuilder()
                .headers(head.build())
//                .addHeader("Content-type", "text/plain; charset=utf-8")

//                .addHeader("packageName", "mbjmb")
//                .addHeader("HCFQ", tokenAES)
                .method(request.method(), newBody)
                .build()
        } catch (e: Exception) {
        }
        //响应
        var response = chain.proceed(request)
        if (response.code() == 200) {
            try {
                //获取加密的响应数据
                val oldResponseBody = response.body()
                val oldResponseBodyStr = oldResponseBody!!.string()
                //加密的响应数据用AES秘钥解密
                var newResponseBodyStr: String? = ""
                if (!TextUtils.isEmpty(oldResponseBodyStr)) {
                    newResponseBodyStr = AESUtil.decrypt(oldResponseBodyStr, AESUtil.base64ToDecode(AESUtil.paramKey))
                }
                oldResponseBody.close()
                //构造新的response
                val newResponseBody = ResponseBody.create(mediaType, newResponseBodyStr)
                response = response.newBuilder().body(newResponseBody).build()
            } catch (e: Exception) {
                LogUtil.d("RetrofitLog"+ "e" + e.message)
            } finally {
                response.close()
            }
        }
        //返回
        return response
    }
}
