package com.sum.login.login

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.sum.common.constant.LOGIN_ACTIVITY_LOGIN
import com.sum.common.dialog.DialogUtil
import com.sum.common.dialog.XDialog
import com.sum.common.dialog.base.BindViewHolder
import com.sum.common.dialog.listener.OnViewClickListener
import com.sum.common.provider.LoginServiceProvider
import com.sum.common.provider.MainServiceProvider
import com.sum.common.provider.UserServiceProvider
import com.sum.framework.base.BaseMvvmActivity
import com.sum.framework.log.LogUtil
import com.sum.framework.toast.TipsToast
import com.sum.framework.utils.DeviceInfoUtils
import com.sum.framework.utils.PermissionUtil
import com.sum.framework.utils.StatusBarSettingHelper
import com.sum.login.R
import com.sum.login.databinding.ActivityLogin2Binding
import com.sum.login.databinding.ActivityLoginBinding
import com.tbruyelle.rxpermissions3.RxPermissions

/**
 *
 * @author why
 * @date 2023/11/18 17:25
 */
@Route(path = LOGIN_ACTIVITY_LOGIN)
class LoginActivity2  : BaseMvvmActivity<ActivityLogin2Binding, LoginViewModel>(){
    private var isShowDialog = false


    override fun initView(savedInstanceState: Bundle?) {
        StatusBarSettingHelper.setStatusBarTranslucent(this)
        modPhone()
//        if(LoginServiceProvider.isLogin()) {
//            mViewModel.login()
//            MainServiceProvider.toMain(context = this)
//            finish()
//        }
//        val use = ARouter.getInstance().build("/main/activity/home").navigation();
        mViewModel.loginMode.observe(this) { phone ->
            DeviceInfoUtils.hideInputView(mBinding.flContent)
            when(phone) {
                LoginViewModel.LOGIN_PHONE -> {
                    modPhone()
                }
                LoginViewModel.LOGIN_REGISTER -> {
                    modRegister()
                }
                LoginViewModel.LOGIN_LOGIN -> {
                    modLogin()
                }
                LoginViewModel.LOGIN_FOREGT -> {
                    modForget()
                }
            }
        }
        mViewModel.loginLiveData.observe(this) { user ->
            //登录成功
            dismissLoading()
            user?.let {
                UserServiceProvider.saveUserInfo(user)
                UserServiceProvider.saveUserPhone(mViewModel.phoneNum.value)
                TipsToast.showTips(R.string.success_login)
                MainServiceProvider.toMain(context = this)
                finish()
            } ?: kotlin.run {

            }
        }
    }

    private fun modPhone() {
        mBinding.phoneInput.visibility = View.VISIBLE
        mBinding.phoneRegister.visibility = View.GONE
        mBinding.phoneLogin.visibility = View.GONE
        mBinding.phoneForget.visibility = View.GONE
    }

    private fun modRegister() {
        mBinding.phoneInput.visibility = View.GONE
        mBinding.phoneRegister.visibility = View.VISIBLE
        mBinding.phoneLogin.visibility = View.GONE
        mBinding.phoneForget.visibility = View.GONE
    }

    private fun modLogin() {
        mBinding.phoneInput.visibility = View.GONE
        mBinding.phoneRegister.visibility = View.GONE
        mBinding.phoneLogin.visibility = View.VISIBLE
        mBinding.phoneLogin.update(mViewModel.phoneNum.value ?: "")
        mBinding.phoneForget.visibility = View.GONE
    }

    private fun modForget() {
        mBinding.phoneInput.visibility = View.GONE
        mBinding.phoneRegister.visibility = View.GONE
        mBinding.phoneLogin.visibility = View.GONE
        mBinding.phoneForget.visibility = View.VISIBLE
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
        ).subscribe ({ permission ->
            if (permission.granted) {
                // 授权成功
                if(mBinding.phoneInput.visibility == View.VISIBLE) {
                    mBinding.phoneInput.hasPermission()
                }
            } else if (permission.shouldShowRequestPermissionRationale) {
                // 用户拒绝授权，但未勾选“不再询问”
                showPermissionDialog(this as FragmentActivity)
            } else {
                // 用户拒绝授权，且勾选了“不再询问”
                showPermissionDialog(this as FragmentActivity)
            }
        }, {
            it.printStackTrace();
        })
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


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            DeviceInfoUtils.CREDENTIAL_PICKER_REQUEST ->
                // Obtain the phone number from the result
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val credential = data.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
                    LogUtil.d("show hone  ${credential?.id}")
                    mBinding.phoneInput.updatePhone(credential)
                    // credential.getId();  <-- will need to process phone number string
                }
            // ...
        }
    }
}