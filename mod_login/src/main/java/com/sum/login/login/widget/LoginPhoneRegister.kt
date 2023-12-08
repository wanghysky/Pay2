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
import com.sum.common.dialog.DialogUtil
import com.sum.framework.ext.onClick
import com.sum.framework.ext.textChangeFlow
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
 * @date 2023/11/19 16:05
 */
class LoginPhoneRegister@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(
    context, attrs, defStyle
) {

    private var phoneNum = "0"
    private var isShowPassword = true

    private val mViewModel = ViewModelProvider(context as FragmentActivity).get(LoginViewModel::class.java)

    private val phoneTitle by bindView<TextView>(R.id.tv_login_phone_edit)
    private val etPassword by bindView<ClearEditText>(R.id.et_password)
    private val etPhone by bindView<ClearEditText>(R.id.et_phone)
    private val ivPasswordToggle by bindView<ImageView>(R.id.iv_password_toggle)
    private val tvLogin by bindView<TextView>(R.id.tv_login)
    private val tvSend by bindView<TextView>(R.id.tv_send)
    private val llPhoneSms by bindView<LinearLayout>(R.id.ll_phone_sms)

    init {
        inflate(context, R.layout.layout_register_phone, this)
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
        mViewModel.showDialog.observe(context as FragmentActivity) { user ->
            DialogUtil.showMessageDialog(context as FragmentActivity, {
                mViewModel.checkSmsCount(mViewModel.phoneNum.value ?: "", false, false)
            }, {
                mViewModel.sendSms(mViewModel.phoneNum.value ?: "", true, false)
            })
        }
    }

    fun update(phone: String) {
        phoneNum = phone
        phoneTitle.text = String.format(getStringFromResource(com.sum.common.R.string.login_sms_title), phoneNum)
    }

    private fun initView() {
        phoneTitle.text = String.format(getStringFromResource(com.sum.common.R.string.login_sms_title), phoneNum)
        etPassword.post {
            etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        ivPasswordToggle.onClick {
            setPasswordHide()
        }
        tvSend.onClick {
            mViewModel.checkSmsCount(mViewModel.phoneNum.value ?: "", false, false)
        }
        llPhoneSms.onClick {
            mViewModel.sendSms(mViewModel.phoneNum.value ?: "", true, false)
        }
        setEditTextChange(etPhone)
        setEditTextChange(etPassword)
        CountDownUtils.addTextView(tvSend, object : CountDownUtils.CallBack{
            override fun finish() {
                mViewModel.smsStatus.value = 0
            }
        })
        tvLogin.onClick {
            toLogin()
        }

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
        val smsEnable = !phone.isNullOrEmpty() && phone.length >= 4
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

    private fun toLogin() {
        val sms = etPhone.text?.trim()?.toString()
        val password = etPassword.text?.trim()?.toString()

        if (sms.isNullOrEmpty()) {
//            TipsToast.showTips(getStringFromResource(R.string.error_phone_number))
            return
        }

        if (password.isNullOrEmpty() ) {
//            TipsToast.showTips(R.string.error_input_password)
            return
        }
        mViewModel.getRESKey(sms, password?: "")

    }
}