package com.sum.main.ui

import android.Manifest
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.sum.common.dialog.XDialog
import com.sum.common.dialog.base.BindViewHolder
import com.sum.common.dialog.listener.OnViewClickListener
import com.sum.framework.base.BaseDataBindActivity
import com.sum.framework.utils.PermissionUtil
import com.sum.framework.utils.StatusBarSettingHelper
import com.sum.main.databinding.ActivitySplashBinding
import com.tbruyelle.rxpermissions3.RxPermissions

/**
 * @author mingyan.su
 * @date   2023/3/29 14:25
 * @desc   启动页
 */
class SplashActivity : BaseDataBindActivity<ActivitySplashBinding>() {

    private var isShowDialog = false
    override fun initView(savedInstanceState: Bundle?) {
        StatusBarSettingHelper.setStatusBarTranslucent(this)
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
                showPermissionDialog(this)
            } else {
                // 用户拒绝授权，且勾选了“不再询问”
                showPermissionDialog(this)
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
                    finish()
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

    override fun onResume() {
        super.onResume()
//        requestPermission()
        if(!isShowDialog) {
            requestPermission()
        }
    }
}