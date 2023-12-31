package com.sum.framework.utils

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import com.sum.framework.loading.CenterLoadingView
import com.sum.framework.R
import com.sum.framework.loading.CenterSuccessView

/**
 * 等待提示框
 */
class LoadSuccessUtils(private val mContext: Context) {
    private var loadView: CenterSuccessView? = null

    /**
     * 统一耗时操作Dialog
     */
    fun showLoading(txt: String?) {
        if (loadView == null) {
            loadView = CenterSuccessView(mContext, R.style.dialog)
            // loadView.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        if (loadView?.isShowing == true) {
            loadView?.dismiss()
        }
        if (!TextUtils.isEmpty(txt)) {
            loadView?.setTitle(txt as CharSequence)
        }
        if (mContext is Activity && mContext.isFinishing) {
            return
        }
        loadView?.show()
    }

    /**
     * 关闭Dialog
     */
    fun dismissLoading() {
        if (mContext is Activity && mContext.isFinishing) {
            return
        }

        loadView?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }
}
