package com.sum.main.ui

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.sum.common.camera.CameraSurfaceView2
import com.sum.common.dialog.XDialog
import com.sum.common.dialog.base.BindViewHolder
import com.sum.common.dialog.listener.OnBindViewListener
import com.sum.common.dialog.listener.OnViewClickListener
import com.sum.framework.base.BaseDataBindActivity
import com.sum.framework.log.LogUtil.d
import com.sum.framework.toast.TipsToast
import com.sum.main.R
import com.sum.main.databinding.ActivityCamera2Binding
import com.sum.main.ui.contract.viewmodel.ContractViewModel
import com.weigan.loopview.LoopView

/**
 *
 * @author why
 * @date 2023/12/3 22:39
 */
class CameraActivity : BaseDataBindActivity<ActivityCamera2Binding>() {

    val mViewModel by lazy {
        ViewModelProvider(this).get(ContractViewModel::class.java)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.takePic.setOnClickListener {
            showLoading()
            mBinding.cameraSurfaceView2.takePicture()
            mBinding.cameraSurfaceView2.onPathChangedListener =
                CameraSurfaceView2.OnPathChangedListener { path: String ->
                    d("-----拍摄的照片路径：$path")
                    mViewModel.upload(path)
                }
        }
        mViewModel.positive.observe(this) {
            if (it == null) {
                dismissLoading()
                showMessageDialog(this)
            } else {
                showSuccess()
                mBinding.takePic.postDelayed({
                    dismissSuccess()
                    val intent = Intent()
                    intent.putExtra("path", it.toString())
                    setResult(RESULT_OK, intent)
                    finish()
                }, 2000)
            }
        }
    }


    fun onClick(v: View) {
        if (v.id == R.id.takePic) {
            mBinding.cameraSurfaceView2.takePicture()
        }
    }

    fun showMessageDialog(activity: FragmentActivity) {
        val dialog = XDialog.Builder(activity.supportFragmentManager)
            .setLayoutRes(R.layout.show_camera_fail_dialog)
            .setScreenWidthAspect(activity, 0.9f)
            .setTag("PermissionDialog")
            .setDimAmount(0.6f)
            .setCancelableOutside(false)
            .setGravity(Gravity.CENTER)
            .addOnClickListener(R.id.iv_close)
            .setOnViewClickListener(object : OnViewClickListener {
                override fun onViewClick(
                    viewHolder: BindViewHolder?,
                    view: View?,
                    xDialog: XDialog?
                ) {
                    when (view?.id) {
                        R.id.iv_close -> {
                            xDialog?.dismiss()
                        }
                    }
                }
            })
            .create()
            .show()
    }


}