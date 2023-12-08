package com.sum.login.login.widget

import android.content.Context
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.launcher.ARouter
import com.auramarker.lib_base.kotterknife.bindView
import com.google.android.gms.auth.api.credentials.Credential
import com.sum.common.constant.MAIN_ACTIVITY_HOME
import com.sum.common.constant.VIDEO_ACTIVITY_PLAYER
import com.sum.common.provider.LoginServiceProvider
import com.sum.common.provider.MainServiceProvider
import com.sum.common.provider.UserServiceProvider
import com.sum.framework.ext.onClick
import com.sum.framework.ext.textChangeFlow
import com.sum.framework.log.LogUtil
import com.sum.framework.toast.TipsToast
import com.sum.framework.utils.DeviceInfoUtils
import com.sum.framework.utils.getColorFromResource
import com.sum.framework.utils.getStringFromResource
import com.sum.framework.weights.ClearEditText
import com.sum.login.R
import com.sum.login.login.LoginViewModel
import com.sum.login.login.LoginViewModel.Companion.LOGIN_LOGIN
import com.sum.login.login.LoginViewModel.Companion.LOGIN_PHONE
import com.sum.login.login.LoginViewModel.Companion.LOGIN_REGISTER
import com.sum.login.policy.PrivacyPolicyActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 *
 * @author why
 * @date 2023/11/18 22:44
 */
class LoginPhoneInputView@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(
    context, attrs, defStyle
) {

    private val etPhone by bindView<ClearEditText>(R.id.et_phone)
    private val tvLogin by bindView<TextView>(R.id.tv_login)
    private val cbAgreement by bindView<AppCompatCheckBox>(R.id.cb_agreement)

   private val mViewModel = ViewModelProvider(context as FragmentActivity).get(LoginViewModel::class.java)

    private var hasCheckPermission = false

    init {
        inflate(context, R.layout.layout_edit_phone, this)
        initView()
    }

    private fun initView() {
        initListener()
        initAgreement()
        etPhone.setText(UserServiceProvider.getUserPhone())
        etPhone.setSelection(etPhone.length())
    }

    private fun initListener() {
        tvLogin.onClick {
            toLogin()
//            ARouter.getInstance().build(MAIN_ACTIVITY_HOME).navigation()
        }
        setEditTextChange(etPhone)
    }

    private fun toLogin() {
        val userName = etPhone.text?.trim()?.toString()
//        val password = etPassword.text?.trim()?.toString()

        if (userName.isNullOrEmpty() ) {
//            TipsToast.showTips(getStringFromResource(R.string.error_phone_number))
            return
        }

        if (userName.substring(0,2) != "08") {
            TipsToast.showTips(getStringFromResource(R.string.error_phone_number))
            return
        }
        mViewModel.checkPhoneNum(userName)
    }

    /**
     * 更新登录按钮状态
     */
    private fun updateLoginState() {
        val phone = etPhone.text.toString()
        val phoneEnable = !phone.isNullOrEmpty() && phone.length >= 12

        tvLogin.isSelected = phoneEnable
    }

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
     * 初始化协议点击
     */
    private fun initAgreement() {
        val agreement = getStringFromResource(R.string.login_agreement)
        try {
            cbAgreement.movementMethod = LinkMovementMethod.getInstance()
            val spaBuilder = SpannableStringBuilder(agreement)
            val privacySpan = getStringFromResource(R.string.login_privacy_agreement)
            val serviceSpan = getStringFromResource(R.string.login_user_agreement)
            spaBuilder.setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        (widget as TextView).highlightColor = getColorFromResource(com.sum.common.R.color.transparent)
                        PrivacyPolicyActivity.start(context)
                    }

                    @RequiresApi(Build.VERSION_CODES.M)
                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.color = getColorFromResource(com.sum.common.R.color._A0A067)
                        ds.isUnderlineText = false
                        ds.clearShadowLayer()
                    }
                },
                spaBuilder.indexOf(privacySpan),
                spaBuilder.indexOf(privacySpan) + privacySpan.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spaBuilder.setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        (widget as TextView).highlightColor = getColorFromResource(com.sum.common.R.color.transparent)
                        PrivacyPolicyActivity.start(context)
                    }

                    @RequiresApi(Build.VERSION_CODES.M)
                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.color = getColorFromResource(com.sum.common.R.color._A0A067)
                        ds.isUnderlineText = false
                        ds.clearShadowLayer()
                    }
                },
                spaBuilder.indexOf(serviceSpan),
                spaBuilder.indexOf(serviceSpan) + serviceSpan.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            cbAgreement.setText(spaBuilder, TextView.BufferType.SPANNABLE)
        } catch (e: Exception) {
            LogUtil.e(e)
            cbAgreement.text = agreement
        }
    }

    fun updatePhone(credential: Credential?) {
        val phoneNum = credential?.id
        phoneNum?.let {
            etPhone.setText(it)
        }
    }

    fun hasPermission() {
        if(!hasCheckPermission) {
            DeviceInfoUtils.getPhoneNum(context as FragmentActivity)
        }
        hasCheckPermission = true
    }

}