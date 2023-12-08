package com.sum.login.login.widget

import android.content.Context
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.auramarker.lib_base.kotterknife.bindView
import com.sum.framework.ext.onClick
import com.sum.framework.ext.textChangeFlow
import com.sum.framework.toast.TipsToast
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
 * @date 2023/11/19 23:36
 */
class LoginPhoneLoginView@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(
    context, attrs, defStyle
) {

    private var phoneNum = "0"
    private var isShowPassword = true

    private val mViewModel = ViewModelProvider(context as FragmentActivity).get(LoginViewModel::class.java)

    private val phoneTitle by bindView<TextView>(R.id.tv_login_title)
    private val etPassword by bindView<ClearEditText>(R.id.et_password)
    private val etPhone by bindView<ClearEditText>(R.id.et_phone)
    private val ivPasswordToggle by bindView<ImageView>(R.id.iv_password_toggle)
    private val tvLogin by bindView<TextView>(R.id.tv_login)
    private val tvRegister by bindView<TextView>(R.id.tv_register)
    private val tvForgetPassword by bindView<TextView>(R.id.tv_forget_password)

    init {
        inflate(context, R.layout.layout_login_phone, this)
        initView()
        initObserver()
    }

    private fun initObserver() {
        mViewModel.phoneNum.observe(context as FragmentActivity) { phone ->
            update(phone)
        }
    }

    fun update(phone: String) {
        phoneNum = phone
        phoneTitle.text = getStringFromResource(com.sum.common.R.string.login_phone_title)
        etPhone.setText(phone)
    }

    private fun initView() {
        phoneTitle.text = getStringFromResource(com.sum.common.R.string.login_phone_title)
        etPassword.post {
            etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        ivPasswordToggle.onClick {
            setPasswordHide()
        }
        tvLogin.setOnClickListener {
            toLogin()
        }
        tvRegister.onClick {
            mViewModel.loginMode.value = LoginViewModel.LOGIN_PHONE
        }
        tvForgetPassword.onClick {
            mViewModel.loginMode.value = LoginViewModel.LOGIN_FOREGT
        }
        setEditTextChange(etPhone)
        setEditTextChange(etPassword)
    }

    private fun toLogin() {
        val userName = etPhone.text?.trim()?.toString()
        val password = etPassword.text?.trim()?.toString()

        if (userName.isNullOrEmpty() ) {
//            TipsToast.showTips(getStringFromResource(R.string.error_phone_number))
            return
        }

        if (userName.substring(0,2) != "08") {
            TipsToast.showTips(getStringFromResource(R.string.error_phone_number))
            return
        }
        //        mViewModel.checkPhoneNum(userName)
        if (password.isNullOrEmpty()) {
//            TipsToast.showTips(R.string.error_input_password)
            return
        }
        mViewModel.login(password?: "")
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
        val smsEnable = !phone.isNullOrEmpty() && phone.length >= 12
        val password = etPassword.text.toString()
        val passwordEnable = !password.isNullOrEmpty() && password.length >= 6

        tvLogin.isSelected = smsEnable && passwordEnable
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