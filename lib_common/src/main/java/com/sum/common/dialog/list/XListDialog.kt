package com.sum.common.dialog.list

import android.app.Activity
import android.content.DialogInterface
import android.util.Log
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sum.common.dialog.adapter.TParams
import com.sum.common.dialog.base.XBaseAdapter
import com.sum.common.dialog.base.XController
import com.sum.common.dialog.listener.OnAdapterItemClickListener
import com.sum.common.dialog.listener.OnBindViewListener
import com.sum.common.dialog.listener.OnViewClickListener
import com.sum.common.R
import com.sum.common.dialog.XDialog
import com.sum.common.dialog.base.BaseDialogFragment.Companion.getScreenHeight
import com.sum.common.dialog.base.BaseDialogFragment.Companion.getScreenWidth

/**
 * 项目：xDialog
 * 版权：蒲公英公司 版权所有
 * 作者：Arry
 * 版本：1.0
 * 创建日期：2019-09-17
 * 描述：
 * 列表弹窗  与TDialog实现分开处理
 * 修订历史：
 */
class XListDialog : XDialog() {


    override fun bindView(view: View?) {
        super.bindView(view)
        if (xController.adapter != null) { //有设置列表
            //列表
            val recyclerView = view?.findViewById<RecyclerView>(R.id.recycler_view)
                ?: throw IllegalArgumentException("自定义列表xml布局,请设置RecyclerView的控件id为recycler_view")
            xController.adapter!!.setTDialog(this)
            val layoutManager: RecyclerView.LayoutManager =
                LinearLayoutManager(view.context, xController.orientation, false)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = xController.adapter
            xController.adapter!!.notifyDataSetChanged()
            if (xController.adapterItemClickListener != null) {
                xController.adapter!!.setOnAdapterItemClickListener(xController.adapterItemClickListener)
            }
        } else {
            Log.d("TDialog", "列表弹窗需要先调用setAdapter()方法!")
        }

    }


    /*********************************************************************
     * 使用Builder模式实现
     *
     */
    open class Builder(fragmentManager: FragmentManager?) {

        private val params = TParams<XBaseAdapter<*>>()

        init {
            params.mFragmentManager = fragmentManager
        }

        //各种setXXX()方法设置数据
        fun setLayoutRes(@LayoutRes layoutRes: Int): Builder {
            params.mLayoutRes = layoutRes
            return this
        }

        //设置自定义列表布局和方向
        fun setListLayoutRes(@LayoutRes layoutRes: Int, orientation: Int): Builder {
            params.listLayoutRes = layoutRes
            params.orientation = orientation
            return this
        }

        /**
         * 设置弹窗宽度是屏幕宽度的比例 0 -1
         */
        fun setScreenWidthAspect(activity: Activity, widthAspect: Float): Builder {
            params.mWidth = (getScreenWidth(activity) * widthAspect).toInt()
            return this
        }

        fun setWidth(widthPx: Int): Builder {
            params.mWidth = widthPx
            return this
        }

        /**
         * 设置屏幕高度比例 0 -1
         */
        fun setScreenHeightAspect(activity: Activity, heightAspect: Float): Builder {
            params.mHeight = (getScreenHeight(activity) * heightAspect).toInt()
            return this
        }

        fun setHeight(heightPx: Int): Builder {
            params.mHeight = heightPx
            return this
        }

        fun setGravity(gravity: Int): Builder {
            params.mGravity = gravity
            return this
        }

        fun setCancelOutside(cancel: Boolean): Builder {
            params.mIsCancelableOutside = cancel
            return this
        }

        fun setDimAmount(dim: Float): Builder {
            params.mDimAmount = dim
            return this
        }

        fun setTag(tag: String): Builder {
            params.mTag = tag
            return this
        }

        fun setOnBindViewListener(listener: OnBindViewListener?): Builder {
            params.bindViewListener = listener
            return this
        }

        fun addOnClickListener(vararg ids: Int): Builder {
            params.ids = ids
            return this
        }

        fun setOnViewClickListener(listener: OnViewClickListener?): Builder {
            params.mOnViewClickListener = listener
            return this
        }

        //列表数据,需要传入数据和Adapter,和item点击数据
        fun <A : XBaseAdapter<*>?> setAdapter(adapter: A): Builder {
            params.adapter = adapter
            return this
        }

        fun setOnAdapterItemClickListener(listener: OnAdapterItemClickListener<*>?): Builder {
            params.adapterItemClickListener = listener
            return this
        }

        fun setOnDismissListener(dismissListener: DialogInterface.OnDismissListener?): Builder {
            params.mOnDismissListener = dismissListener
            return this
        }

        fun create(): XListDialog {
            val dialog = XListDialog()
            //将数据从Buidler的DjParams中传递到DjDialog中
            params.apply(dialog.xController as XController<XBaseAdapter<*>>)
            return dialog
        }
    }
}