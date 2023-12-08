package com.sum.login.login

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.sum.common.constant.LOGIN_ACTIVITY_LOGIN
import com.sum.common.dialog.XDialog
import com.sum.common.dialog.base.BindViewHolder
import com.sum.common.dialog.listener.OnBindViewListener
import com.sum.common.dialog.listener.OnViewClickListener
import com.sum.common.provider.UserServiceProvider
import com.sum.framework.base.BaseMvvmActivity
import com.sum.framework.ext.onClick
import com.sum.framework.ext.textChangeFlow
import com.sum.framework.log.LogUtil
import com.sum.framework.toast.TipsToast
import com.sum.framework.utils.PermissionUtil
import com.sum.framework.utils.getColorFromResource
import com.sum.framework.utils.getStringFromResource
import com.sum.login.R
import com.sum.login.databinding.ActivityLoginBinding
import com.sum.login.policy.PrivacyPolicyActivity
import com.sum.login.register.RegisterActivity
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * @author mingyan.su
 * @date   2023/3/25 8:11
 * @desc   登录
 */
//@Route(path = LOGIN_ACTIVITY_LOGIN)
class LoginActivity : BaseMvvmActivity<ActivityLoginBinding, LoginViewModel>() {
    private var isShowPassword = true
    private var isShowDialog = false

    override fun initView(savedInstanceState: Bundle?) {
        initAgreement()
        initListener()
        mBinding.etPhone.setText(UserServiceProvider.getUserPhone())
        mBinding.etPhone.setSelection(mBinding.etPhone.length())
        mBinding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
    }

    override fun initData() {
//        mViewModel.loginLiveData.observe(this) { user ->
//            //登录成功
//            dismissLoading()
//            user?.let {
//                UserServiceProvider.saveUserInfo(user)
//                UserServiceProvider.saveUserPhone(user.username)
//                TipsToast.showTips(R.string.success_login)
////                MainServiceProvider.toMain(context = this)
//                finish()
//            } ?: kotlin.run {
//
//            }
//        }
    }

    override fun onResume() {
        super.onResume()
        if(!isShowDialog) {
            requestPermission()
        }
    }

    private fun requestPermission() {
        RxPermissions(this).requestEachCombined(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_CALL_LOG
        ).subscribe { permission ->
            if (permission.granted) {
                // 授权成功
            } else if (permission.shouldShowRequestPermissionRationale) {
                // 用户拒绝授权，但未勾选“不再询问”
                showPermissionDialog(this as FragmentActivity)
            } else {
                // 用户拒绝授权，且勾选了“不再询问”
                showPermissionDialog(this as FragmentActivity)
            }
        }
    }

    private fun showPermissionDialog(activity: FragmentActivity) {
        isShowDialog = true
        XDialog.Builder(activity.supportFragmentManager)
            .setLayoutRes(com.sum.common.R.layout.check_permission_dialog)
            .setScreenWidthAspect(activity, 0.9f)
            .setTag("PermissionDialog")
            .setDimAmount(0.6f)
            .setCancelableOutside(false)
            .setGravity(Gravity.CENTER)
            .addOnClickListener(com.sum.common.R.id.btn_right, com.sum.common.R.id.btn_left, com.sum.common.R.id.iv_dialog_close)
            .setOnKeyListener { p0, p1, p2 ->
                if(p1 == KeyEvent.KEYCODE_BACK) {
//                    this.finish()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
            .setOnViewClickListener(object : OnViewClickListener {
                override fun onViewClick(
                    viewHolder: BindViewHolder?,
                    view: View?,
                    xDialog: XDialog?
                ) {
                    when (view?.id) {
                        com.sum.common.R.id.btn_right -> {
                            PermissionUtil.openAppSettings(activity)
                            xDialog?.dismiss()
                        }

                        com.sum.common.R.id.btn_left -> {
                            activity.finish()
                        }

                        com.sum.common.R.id.iv_dialog_close -> {
//                            exitProcess(0);
                            activity.finish()
                        }
                    }
                }
            })
            .setOnDismissListener {
                isShowDialog = false
            }
            .create()
            .show()
    }

    private fun initListener() {
        mBinding.ivPasswordToggle.onClick {
            setPasswordHide()
        }
        mBinding.tvForgetPassword.onClick {
//            TipsToast.showTips(R.string.login_forget_password)
//            mViewModel.sendVerifiyCode()

        }
        mBinding.tvLogin.onClick {
            toLogin()
        }
        mBinding.tvRegister.onClick {
            RegisterActivity.start(this)
        }

        setEditTextChange(mBinding.etPhone)
        setEditTextChange(mBinding.etPassword)
        mBinding.cbAgreement.setOnCheckedChangeListener { _, _ ->
            updateLoginState()
        }
    }

    /**
     * 更新登录按钮状态
     */
    private fun updateLoginState() {
        val phone = mBinding.etPhone.text.toString()
        val phoneEnable = !phone.isNullOrEmpty() && phone.length == 11
        val password = mBinding.etPassword.text.toString()
        val passwordEnable = !password.isNullOrEmpty()
        val agreementEnable = mBinding.cbAgreement.isChecked

        mBinding.tvLogin.isSelected = phoneEnable && passwordEnable && agreementEnable
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
                .launchIn(lifecycleScope)
    }

    /**
     * 去登录
     */
    private fun toLogin() {
        val userName = mBinding.etPhone.text?.trim()?.toString()
        val password = mBinding.etPassword.text?.trim()?.toString()

        if (userName.isNullOrEmpty() || userName.length < 11) {
            TipsToast.showTips(getStringFromResource(R.string.error_phone_number))
            return
        }
        if (password.isNullOrEmpty()) {
            TipsToast.showTips(R.string.error_input_password)
            return
        }
        if (!mBinding.cbAgreement.isChecked) {
            TipsToast.showTips(R.string.tips_read_user_agreement)
            return
        }
        showLoading()
//        mViewModel.login(userName, password)
    }

    /**
     * 密码是否可见
     */
    private fun setPasswordHide() {
        isShowPassword = !isShowPassword
        if (isShowPassword) {
            mBinding.ivPasswordToggle.setImageResource(R.mipmap.ic_psw_invisible)
            mBinding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            mBinding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            mBinding.ivPasswordToggle.setImageResource(R.mipmap.ic_psw_visible)
        }
        mBinding.etPassword.setSelection(mBinding.etPassword.length())
    }

    /**
     * 初始化协议点击
     */
    private fun initAgreement() {
        val agreement = getStringFromResource(R.string.login_agreement)
        try {
            mBinding.cbAgreement.movementMethod = LinkMovementMethod.getInstance()
            val spaBuilder = SpannableStringBuilder(agreement)
            val privacySpan = getStringFromResource(R.string.login_privacy_agreement)
            val serviceSpan = getStringFromResource(R.string.login_user_agreement)
            spaBuilder.setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        (widget as TextView).highlightColor = getColorFromResource(com.sum.common.R.color.transparent)
                        PrivacyPolicyActivity.start(this@LoginActivity)
                    }

                    @RequiresApi(Build.VERSION_CODES.M)
                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.color = getColorFromResource(com.sum.common.R.color.color_0165b8)
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
                        PrivacyPolicyActivity.start(this@LoginActivity)
                    }

                    @RequiresApi(Build.VERSION_CODES.M)
                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.color = getColorFromResource(com.sum.common.R.color.color_0165b8)
                        ds.isUnderlineText = false
                        ds.clearShadowLayer()
                    }
                },
                spaBuilder.indexOf(serviceSpan),
                spaBuilder.indexOf(serviceSpan) + serviceSpan.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            mBinding.cbAgreement.setText(spaBuilder, TextView.BufferType.SPANNABLE)
        } catch (e: Exception) {
            LogUtil.e(e)
            mBinding.cbAgreement.text = agreement
        }
    }
}