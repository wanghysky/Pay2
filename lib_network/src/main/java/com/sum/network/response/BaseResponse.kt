package com.sum.network.response

/**
 * @author mingyan.su
 * @date   2023/2/24 13:10
 * @desc   通用数据类
 */
data class BaseResponse<out T>(
    val data: T?,
    val code: Int = 0,//服务器状态码 这里0表示请求成功
    val message: String = ""//错误信息
) {

    /**
     * 判定接口返回是否正常
     */
    fun isFailed(): Boolean {
        return code != 200
    }
}