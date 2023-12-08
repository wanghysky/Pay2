package com.sum.common.dialog.listener

import com.sum.common.dialog.XDialog
import com.sum.common.dialog.base.BindViewHolder

/**
 * name：cl
 * date：2023/4/24
 * desc：
 */
interface OnAdapterItemClickListener<T> {
     fun onItemClick(holder: BindViewHolder?, position: Int, t: T, tDialog: XDialog?)
}