package com.sum.login.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sum.common.dialog.DialogUtil
import com.sum.common.model.LoginRequest
import com.sum.common.model.Register
import com.sum.common.model.ResetPassword
import com.sum.common.model.SmsCount
import com.sum.common.model.SmsReq
import com.sum.common.model.User
import com.sum.common.model.UserInfo
import com.sum.framework.log.LogUtil
import com.sum.framework.toast.TipsToast
import com.sum.framework.utils.RSAUtils
import com.sum.framework.utils.getStringFromResource
import com.sum.login.R
import com.sum.network.viewmodel.BaseViewModel
import java.security.PublicKey


/**
 * @author mingyan.su
 * @date   2023/3/25 16:38
 * @desc   登录viewModel
 */
class LoginViewModel : BaseViewModel() {
    val loginLiveData = MutableLiveData<UserInfo?>()
    val registerLiveData = MutableLiveData<User?>()
    val loginRepository by lazy { LoginRepository() }


    val loginMode = MutableLiveData<Int>()
    val phoneNum = MutableLiveData<String>()
    val smsStatus = MutableLiveData<Int>()
    val showDialog = MutableLiveData<Boolean>()

    /**
     * 登录
     * @param username  用户名
     * @param password  密码
     * @return
     */
//    fun login(username: String, password: String): LiveData<User?> {
//        launchUI(errorBlock = { code, error ->
//            TipsToast.showTips(error)
//            loginLiveData.value = null
//        }) {
//            val data = loginRepository.login(username, password)
//            loginLiveData.value = data
//        }
//        return loginLiveData
//    }

    /**
     * 注册
     * @param username  用户名
     * @param password  密码
     * @param repassword  确认密码
     * @return
     */
    fun register(username: String, password: String, repassword: String): LiveData<User?> {
        launchUI(errorBlock = { code, error ->
            TipsToast.showTips(error)
            registerLiveData.value = null
        }) {
            val data = loginRepository.register(username, password, repassword)
            registerLiveData.value = data
        }
        return registerLiveData
    }

    /**
     * 登录
     * @param username  用户名
     * @param password  密码
     * @return
     */
    fun checkPhoneNum(phone: String) {
        launchUI(errorBlock = { code, error ->
//            TipsToast.showTips(error)
//            loginMode.value = 2
        }) {
            val data = loginRepository.checkPhoneExit(phone)
            phoneNum.value = phone
            if (data?.exist == true) {
                loginMode.value = LOGIN_LOGIN
            } else {
                loginMode.value = LOGIN_REGISTER
            }
        }
    }

    fun checkSmsCount(phone: String, voiceCode: Boolean, isForget: Boolean) {
        launchUI(errorBlock = { code, error ->
//            TipsToast.showTips(error)
//            loginMode.value = 2
        }) {
            val data = loginRepository.checkSmsTimes(SmsCount().apply {
                this.merchantNo = "String"
                this.phone = phone
                this.type = if(!isForget)"register" else "reset_pwd"
                this.voiceCode = voiceCode
            })
            if (data != null) {
                if (data >= 3) {
                    showDialog.value = true
                } else {
                    sendSms(phone, voiceCode,isForget)
                }
            }
        }
    }

    fun sendSms(phone: String, voiceCode: Boolean, isForget: Boolean) {
        launchUI(errorBlock = { code, error ->
//            TipsToast.showTips(error)
//            loginMode.value = 2
            if (voiceCode) {
                TipsToast.showTips(getStringFromResource(R.string.login_voice_fail))
            }
        }) {
            val data = loginRepository.sendSms(SmsReq().apply {
                this.merchantNo = ""
                this.phone = phone
                this.type = if(!isForget)"register" else "reset_pwd"
                this.voiceCode = voiceCode
                this.imageCode = "string"
                this.uniquCode = "string"
            })
            if(voiceCode) {
                TipsToast.showTips("Sedang mengirim kode verifikasi telepon , mohon untuk terima panggilan")
                smsStatus.value = 0
            } else {
                smsStatus.value = 1
            }
        }
    }

    fun getRESKey(sms: String, password: String) {
        launchUI(errorBlock = { code, error ->
        }) {
            val key = loginRepository.getRSAKey()

            //获取公钥
            //获取公钥
            val publicKey: PublicKey = RSAUtils.keyStrToPublicKey(key?.key ?: "")

            val rsaKey =
                RSAUtils.encryptDataByPublicKey(password.toByteArray(), publicKey)
            LogUtil.d("加密密码  ： " + rsaKey)
            register(sms, rsaKey)
//            test.value = data.toString()
        }
    }

    fun register(sms: String, password: String) {
        launchUI(errorBlock = { code, error ->
        }) {
            val key = loginRepository.register(Register().apply {
                this.loginName = phoneNum.value
                this.password = password
                this.registerClient = "android"
                this.vcode = sms
            })
            loginLiveData.value = key
            LogUtil.d("加密密码  ： $key")
        }
    }

    fun login(password: String) {
        launchUI(errorBlock = { code, error ->
        }) {
            val key = loginRepository.getRSAKey()

            //获取公钥
            //获取公钥
            val publicKey: PublicKey = RSAUtils.keyStrToPublicKey(key?.key ?: "")

            val rsaKey =
                RSAUtils.encryptDataByPublicKey(password.toByteArray(), publicKey)
            LogUtil.d("加密密码  ： $rsaKey")
            launchUI(errorBlock = { code, error ->
            }) {
                val key = loginRepository.login(LoginRequest().apply {
                    this.loginName = phoneNum.value
                    this.loginType = "PWD"
                    this.pwd = rsaKey
                })
                loginLiveData.value = key
                LogUtil.d("加密密码  ： $key")
            }
        }
    }

    fun resetPassword(phone: String, sms: String, password: String) {
        launchUI(errorBlock = { code, error ->
        }) {
            val key = loginRepository.getRSAKey()
            //获取公钥
            val publicKey: PublicKey = RSAUtils.keyStrToPublicKey(key?.key ?: "")

            val rsaKey =
                RSAUtils.encryptDataByPublicKey(password.toByteArray(), publicKey)
            LogUtil.d("加密密码  ： $rsaKey")
            launchUI(errorBlock = { code, error ->
            }) {
                val key = loginRepository.resetPwd(ResetPassword().apply {
                    this.accessToken = sms
                    this.loginName = phone
                    this.password = rsaKey
                })
                LogUtil.d("加密密码  ： $key")
            }
        }


    }

    companion object {
        val LOGIN_PHONE = 0
        val LOGIN_REGISTER = 1
        val LOGIN_LOGIN = 2
        val LOGIN_FOREGT = 3
    }
}