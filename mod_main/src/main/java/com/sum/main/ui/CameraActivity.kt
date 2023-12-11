package com.sum.main.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.sum.common.camera.CameraSurfaceView2
import com.sum.common.dialog.XDialog
import com.sum.common.dialog.base.BindViewHolder
import com.sum.common.dialog.listener.OnBindViewListener
import com.sum.common.dialog.listener.OnViewClickListener
import com.sum.common.provider.MainServiceProvider
import com.sum.framework.base.BaseDataBindActivity
import com.sum.framework.ext.onClick
import com.sum.framework.log.LogUtil.d
import com.sum.framework.toast.TipsToast
import com.sum.framework.weights.ClearEditText
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

    private var serviceDialog: XDialog? = null
    private var phone = ""


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
        mBinding.ivService.onClick {
            showPhoneDialog(this)
        }
        mBinding.tvPrivasi.onClick {
            MainServiceProvider.toArticleDetail(
                context = this,
                url = "http://106.14.161.98/contract/privacyagreement.html",
                title = "Perjanjian Privasi"
            )
        }
        mViewModel.customerServiceInfo()
        mViewModel.servicePhone.observe(this) {
            phone = it ?: "***"
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

    private fun showMessageDialog(activity: FragmentActivity) {
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

    private fun showPhoneDialog(activity: FragmentActivity) {
        dialogDismiss()
        serviceDialog = XDialog.Builder(activity.supportFragmentManager)
            .setLayoutRes(R.layout.show_service_dialog)
            .setScreenWidthAspect(activity, 0.9f)
            .setTag("PermissionDialog")
            .setDimAmount(0.6f)
            .setCancelableOutside(false)
            .setGravity(Gravity.CENTER)
            .setOnBindViewListener(object : OnBindViewListener {
                override fun bindView(viewHolder: BindViewHolder?) {
                    val view = viewHolder?.getView<ClearEditText>(R.id.et_feedback)
                    val tvPhone = viewHolder?.getView<TextView>(R.id.tv_phone)
                    val tvCount = viewHolder?.getView<TextView>(R.id.tv_feedback_count)
                    view?.addTextChangedListener {
                        val length = it?.length ?: 0
                        tvCount?.text = "$length/200"
                    }
                    tvPhone?.text = phone
                }
            })
            .addOnClickListener(R.id.tv_kirim, R.id.iv_copy, R.id.iv_close)
            .setOnViewClickListener(object : OnViewClickListener {
                override fun onViewClick(
                    viewHolder: BindViewHolder?,
                    view: View?,
                    xDialog: XDialog?
                ) {
                    when (view?.id) {
                        R.id.tv_kirim -> {
                            val view = viewHolder?.getView<ClearEditText>(R.id.et_feedback)
                            mViewModel.reqComplaint(
                                mViewModel.contractStep.value?.step ?: "",
                                view?.text.toString()
                            )
                            dialogDismiss()
                        }

                        R.id.iv_copy -> {
                            val manager: ClipboardManager =
                                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val mClipData: ClipData = ClipData.newPlainText("Label", phone)
                            manager.setPrimaryClip(mClipData)
                            TipsToast.showTips("Tersalin")
                        }

                        R.id.iv_close -> {
                            dialogDismiss()
                        }
                    }
                }
            })
            .create()
            .show()
    }

    private fun dialogDismiss() {
        serviceDialog?.dismiss()
        serviceDialog = null
    }


}