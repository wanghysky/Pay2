package com.sum.main.ui.contract

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.sum.common.dialog.XDialog
import com.sum.common.dialog.base.BindViewHolder
import com.sum.common.dialog.listener.OnBindViewListener
import com.sum.common.dialog.listener.OnViewClickListener
import com.sum.framework.base.BaseMvvmFragment
import com.sum.framework.ext.onClick
import com.sum.framework.ext.textChangeFlow
import com.sum.main.R
import com.sum.main.databinding.FragmentContract2ViewBinding
import com.sum.main.ui.contract.viewmodel.ContractViewModel
import com.weigan.loopview.LoopView
import com.weigan.loopview.OnItemSelectedListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 *
 * @author why
 * @date 2023/11/29 19:37
 */
class ContractMod2Fragment : BaseMvvmFragment<FragmentContract2ViewBinding, ContractViewModel>() {
    private var dialog: XDialog? = null

    private val listWork = arrayListOf<String>()
    private val listInCom = arrayListOf<String>()

    override fun initView(view: View, savedInstanceState: Bundle?) {
        mBinding?.flPekerjaan?.onClick {
            showMessageDialog(requireActivity(), 1) {
                mBinding?.etPekerjaan?.text = it?.let { it1 -> listWork[it1] }
                updateLoginState()
            }
        }
        mBinding?.flBulanan?.onClick {
            showMessageDialog(requireActivity(), 2) {
                mBinding?.etBulanan?.text = it?.let { it1 -> listInCom[it1] }
                updateLoginState()
            }
        }
        mBinding?.tvRenzheng?.onClick {
//
            val work = mBinding?.etPekerjaan?.text.toString()
            val income = mBinding?.etBulanan?.text.toString()
            val companyName = mBinding?.etTvPerusahaan?.text.toString()
            val tel = mBinding?.etTelepon?.text.toString()
            val address = mBinding?.etAlamat?.text.toString()

            val data = mutableMapOf<String, String>()
            data.put("job", work)
            data.put("salary", income)
            data.put("companyName", companyName)
            data.put("companyPhone", tel)
            data.put("companyAddr", address)
            mViewModel.saveUserInfo(data, "jobInfo")
        }
//        for (i in 18..60) {
//            listYear.add("$i")
//        }
        mViewModel.reqDictList("JOB_STATUE", listWork)
        mViewModel.reqDictList("MONTHLY_INCOME", listInCom)
        mBinding?.etTvPerusahaan?.let { setEditTextChange(it) }
        mBinding?.etTelepon?.let { setEditTextChange(it) }
        mBinding?.etAlamat?.let { setEditTextChange(it) }
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

    private fun updateLoginState() {
        val work = mBinding?.etPekerjaan?.text.toString()
        val income = mBinding?.etBulanan?.text.toString()
        val companyName = mBinding?.etTvPerusahaan?.text.toString()
        val tel = mBinding?.etTelepon?.text.toString()
        val address = mBinding?.etAlamat?.text.toString()

        val workEnable = !work.isNullOrEmpty()
        val incomeEnable = !income.isNullOrEmpty()
        val comEnable = !companyName.isNullOrEmpty()
        val telEnable = !tel.isNullOrEmpty()
        val addressEnable = !address.isNullOrEmpty()

        mBinding?.tvRenzheng?.isSelected = workEnable && incomeEnable && comEnable && telEnable && addressEnable
    }

    fun showMessageDialog(activity: FragmentActivity, type: Int, select: (info: Int?) -> Unit) {
        dialogDismiss()
        dialog = XDialog.Builder(activity.supportFragmentManager)
            .setLayoutRes(R.layout.show_contract_dialog)
            .setScreenWidthAspect(activity, 1f)
            .setTag("PermissionDialog")
            .setDimAmount(0.6f)
            .setCancelableOutside(false)
            .setGravity(Gravity.BOTTOM)
            .setOnBindViewListener(object : OnBindViewListener {
                override fun bindView(viewHolder: BindViewHolder?) {
                    val view = viewHolder?.getView<LoopView>(R.id.loopView)
                    view?.setDividerColor(resources.getColor(com.sum.common.R.color.transparent))
                    view?.setCenterTextColor(resources.getColor(com.sum.common.R.color._e5e5e5))
                    view?.setOuterTextColor(resources.getColor(com.sum.common.R.color._A8A7A6))
                    view?.setItemsVisibleCount(7)
                    view?.let {
                        initYearView(
                            it, when (type) {
                                1 -> listWork
                                2 -> listInCom
                                else -> listWork
                            }
                        )
                    }
                }
            })
            .addOnClickListener(R.id.iv_close, R.id.tv_success, R.id.tv_cancle)
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

                       R.id.tv_success -> {
                            val view = viewHolder?.getView<LoopView>(R.id.loopView)
                            select(view?.selectedItem)
                            xDialog?.dismiss()
                        }

                        R.id.tv_cancle -> {
                            xDialog?.dismiss()
                        }
                    }
                }
            })
            .create()
            .show()
    }

    private fun dialogDismiss() {
        if (dialog != null) {
            dialog?.dismiss()
            dialog = null
        }
    }

    private fun initYearView(loopView: LoopView, list: List<String>) {
        //滚动监听
        loopView.setListener(object : OnItemSelectedListener {
            override fun onItemSelected(index: Int) {

//                dialogDismiss()
            }
        })
        //设置原始数据
        loopView.setItems(list)
        //设置初始位置
        loopView.setInitPosition(list.size / 2)
    }
}