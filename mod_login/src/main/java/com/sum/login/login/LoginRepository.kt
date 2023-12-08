package com.sum.login.login

import com.sum.common.model.LoginRequest
import com.sum.common.model.PasswordKey
import com.sum.common.model.PhoneExit
import com.sum.common.model.Register
import com.sum.common.model.ResetPassword
import com.sum.common.model.SmsCount
import com.sum.common.model.SmsReq
import com.sum.common.model.User
import com.sum.common.model.UserInfo
import com.sum.framework.log.LogUtil
import com.sum.framework.utils.AESUtil
import com.sum.network.manager.ApiManager
import com.sum.network.repository.BaseRepository
import org.json.JSONObject

/**
 * @author mingyan.su
 * @date   2023/3/24 18:36
 * @desc   登录仓库
 */
class LoginRepository : BaseRepository() {

    /**
     * 登录
     * @param username  用户名
     * @param password  密码
     */
    suspend fun login(username: String, password: String): User? {
        return requestResponse {
            ApiManager.api.login(username, password)
        }
    }

    /**
     * 注册
     * @param username  用户名
     * @param password  密码
     * @param repassword  确认密码
     */
    suspend fun register(username: String, password: String, repassword: String): User? {
        return requestResponse {
            ApiManager.api.register(username, password, repassword)
        }
    }

    suspend fun checkPhoneExit(phone:String) : PhoneExit? {
        return requestResponse {
            ApiManager.apis.checkPhoneExist(phone)
        }
    }

    suspend fun checkSmsTimes(smsCount: SmsCount) : Int? {
        return requestResponse {
            ApiManager.apis.checkSmsTimes(smsCount)
        }
    }

    suspend fun sendSms(smsCount: SmsReq) : Any? {
        return requestResponse {
            ApiManager.apis.sendSms(smsCount)
        }
    }

    suspend fun getRSAKey(): PasswordKey? {
        return requestResponse {
            ApiManager.apis.getRsaKey()
        }
    }

    suspend fun register(register: Register): UserInfo? {
        return requestResponse {
            ApiManager.apis.register(register)
        }
    }

    suspend fun login(loginRequest: LoginRequest): UserInfo? {
        return requestResponse {
            ApiManager.apis.login(loginRequest)
        }
    }

    suspend fun resetPwd(restPwd: ResetPassword): Any? {
        return requestResponse {
            ApiManager.apis.resetPwd(restPwd)
        }
    }

}