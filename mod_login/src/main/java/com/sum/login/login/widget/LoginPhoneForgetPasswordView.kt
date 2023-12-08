package com.sum.login.login.widget

import android.content.Context
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.auramarker.lib_base.kotterknife.bindView
import com.sum.framework.ext.onClick
import com.sum.framework.ext.textChangeFlow
import com.sum.framework.toast.TipsToast
import com.sum.framework.utils.CountDownUtils
import com.sum.framework.utils.getStringFromResource
import com.sum.framework.weights.ClearEditText
import com.sum.login.R
import com.sum.login.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 *
 * @author why
 * @date 2023/11/20 12:26
 */
class LoginPhoneForgetPasswordView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(
    context, attrs, defStyle
) {

    private var phoneNum = "0"
    private var isShowPassword = true

    private val mViewModel = ViewModelProvider(context as FragmentActivity).get(LoginViewModel::class.java)

    private val etPhone by bindView<ClearEditText>(R.id.et_phone)
    private val etPassword by bindView<ClearEditText>(R.id.et_password)
    private val llPhonesms by bindView<LinearLayout>(R.id.ll_phone_sms)
    private val etSms by bindView<ClearEditText>(R.id.et_sms)
    private val ivPasswordToggle by bindView<ImageView>(R.id.iv_password_toggle)
    private val tvLogin by bindView<TextView>(R.id.tv_login)
    private val tvSend by bindView<TextView>(R.id.tv_send)

    init {
        inflate(context, R.layout.layout_forget_password, this)
        initView()
        initObserver()
    }

    private fun initObserver() {
        mViewModel.phoneNum.observe(context as FragmentActivity) { phone ->
            update(phone)
        }
        mViewModel.smsStatus.observe(context as FragmentActivity) { status ->
            when(status) {
                0 -> {
                    tvSend.text = getStringFromResource(com.sum.common.R.string.send)
                    tvSend.isEnabled = true
                }
                1 -> {
                    tvSend.isEnabled = false
                    CountDownUtils.starCountDownByTime(1)
                }
                2 -> {
                    //loading 状态
                }
            }
        }
        mViewModel.smsStatus.observe(context as FragmentActivity) { status ->
            when(status) {
                0 -> {
                    tvSend.text = getStringFromResource(com.sum.common.R.string.send)
                    tvSend.isEnabled = true
                }
                1 -> {
                    tvSend.isEnabled = false
                    CountDownUtils.starCountDownByTime(1)
                }
                2 -> {
                    //loading 状态
                }
            }
        }
    }

    fun update(phone: String) {
        phoneNum = phone
    }

    private fun initView() {
        etPassword.post {
            etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        ivPasswordToggle.onClick {
            setPasswordHide()
        }
        tvSend.onClick {
            mViewModel.checkSmsCount(etPhone.text.toString(), false, true)
        }
        tvLogin.onClick {
            toLogin()
        }
        llPhonesms.onClick {
            mViewModel.sendSms(etPhone.text.toString(), true, false)
        }
        setEditTextChange(etPhone)
        setEditTextChange(etSms)
        setEditTextChange(etPassword)
        CountDownUtils.addTextView(tvSend, object : CountDownUtils.CallBack{
            override fun finish() {
                mViewModel.smsStatus.value = 0
            }
        })
    }

    private fun toLogin() {
        val phone = etPhone.text?.trim()?.toString()
        val sms = etSms.text?.trim()?.toString()
        val password = etPassword.text?.trim()?.toString()

        if (phone.isNullOrEmpty() ) {
//            TipsToast.showTips(getStringFromResource(R.string.error_phone_number))
            return
        }

        if (phone.substring(0,2) != "08") {
            TipsToast.showTips(getStringFromResource(R.string.error_phone_number))
            return
        }

        if (sms.isNullOrEmpty()) {
//            TipsToast.showTips(getStringFromResource(R.string.error_phone_number))
            return
        }

//        mViewModel.checkPhoneNum(userName)
        if (password.isNullOrEmpty() || password?.length?:0 < 6) {
//            TipsToast.showTips(R.string.error_input_password)
            return
        }
        mViewModel.resetPassword(phone, sms, password ?: "")
//        if (!mBinding.cbAgreement.isChecked) {
//            TipsToast.showTips(R.string.tips_read_user_agreement)
//            return
//        }
//        showLoading()
//        mViewModel.login(userName, password)
    }

    /**
     * 监听EditText文本变化
     */
    private fun setEditTextChange(editText: EditText) {
        editText.textChangeFlow()
//                .filter { it.isNotEmpty() }
            .debounce(300)
            //.flatMapLatest { searchFlow(it.toString()) }
            .flowOn(Dispatchers.IO)
            .onEach {
                updateLoginState()
            }
            .launchIn((context as FragmentActivity).lifecycleScope)
    }

    /**
     * 更新登录按钮状态
     */
    private fun updateLoginState() {
        val phone = etPhone.text.toString()
        val phoneEnable = !phone.isNullOrEmpty()
        val sms = etSms.text.toString()
        val smsEnable = !sms.isNullOrEmpty()
        val password = etPassword.text.toString()
        val passwordEnable = !password.isNullOrEmpty()

        tvLogin.isSelected = smsEnable && passwordEnable && phoneEnable
    }

    /**
     * 密码是否可见
     */
    private fun setPasswordHide() {
        isShowPassword = !isShowPassword
        if (isShowPassword) {
            ivPasswordToggle.setImageResource(R.mipmap.ic_psw_invisible)
            etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        } else {
            etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            ivPasswordToggle.setImageResource(R.mipmap.ic_psw_visible)
        }
        etPassword.setSelection(etPassword.length())
    }
}