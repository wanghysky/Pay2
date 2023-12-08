package com.sum.common.dialog

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.sum.common.R
import com.sum.common.basedialog.BaseDialog
import com.sum.common.basedialog.LAnimationsType
import com.sum.common.dialog.base.BindViewHolder
import com.sum.common.dialog.listener.OnBindViewListener
import com.sum.common.dialog.listener.OnViewClickListener
import com.sum.framework.utils.PermissionUtil
import kotlin.system.exitProcess

/**
 *
 * @author why
 * @date 2023/11/16 17:27
 */
object DialogUtil {

    fun showMessageDialog(activity: FragmentActivity, sendSms:() -> Unit, sendVoice:() -> Unit) {
         XDialog.Builder(activity.supportFragmentManager)
            .setLayoutRes(R.layout.show_sms_dialog)
            .setScreenWidthAspect(activity, 0.9f)
            .setTag("PermissionDialog")
            .setDimAmount(0.6f)
            .setCancelableOutside(false)
            .setGravity(Gravity.CENTER)
            .addOnClickListener(R.id.btn_right, R.id.btn_left, R.id.iv_dialog_close)
            .setOnViewClickListener(object : OnViewClickListener {
                override fun onViewClick(
                    viewHolder: BindViewHolder?,
                    view: View?,
                    xDialog: XDialog?
                ) {
                    when (view?.id) {
                        R.id.btn_right -> {
                            sendVoice.invoke()
                            xDialog?.dismiss()
                        }

                        R.id.btn_left -> {
                            sendSms.invoke()
                            xDialog?.dismiss()
                        }

                        R.id.iv_dialog_close -> {
                            xDialog?.dismiss()
                        }
                    }
                }
            })
            .create()
            .show()
    }

    fun showPayDialog(activity: FragmentActivity) {
        val dialog = BaseDialog.newInstance(activity, R.layout.check_permission_dialog) //设置你的布局
            .setGravity(Gravity.CENTER, 0, 0)
            .setAnimations(LAnimationsType.DEFAULT) //
//            .setBgColor(context.resources.getColor(com.varlens.lib_base.R.color.gray_282828)) //
//            .setBgRadius(25f, 25f, 25f, 25f)
            .setMaskValue(0.5f) //设置布局控件的值
//            .setText(R.id.tv_content, )
            .setCancelBtn(R.id.iv_dialog_close, R.id.btn_left)
            .setOnTouchOutside(false)
            .setOnClickListener(object : BaseDialog.DialogOnClickListener {
                override fun onClick(v: View?, lDialog: BaseDialog?) { //可以根据viewId判断
                    when (v?.id) {
                        R.id.btn_right -> {
                            PermissionUtil.openAppSettings(activity)
                            lDialog?.dismiss()
                        }

                        R.id.btn_left -> {
                            activity.finish()
                        }

                        R.id.iv_dialog_close -> {
//                            exitProcess(0);
                            activity.finish()
                        }
                    }
                }
            }, R.id.btn_right, R.id.btn_left, R.id.iv_dialog_close) //可以设多控件
        dialog.show()
    }

}