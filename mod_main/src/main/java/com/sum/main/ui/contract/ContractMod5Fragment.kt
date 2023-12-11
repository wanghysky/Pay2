package com.sum.main.ui.contract

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.sum.common.dialog.XDialog
import com.sum.common.dialog.base.BindViewHolder
import com.sum.common.dialog.listener.OnBindViewListener
import com.sum.common.dialog.listener.OnViewClickListener
import com.sum.common.model.BankList
import com.sum.common.provider.MainServiceProvider
import com.sum.framework.base.BaseMvvmFragment
import com.sum.framework.ext.onClick
import com.sum.framework.ext.textChangeFlow
import com.sum.framework.utils.FormatUtil
import com.sum.main.R
import com.sum.main.databinding.FragmentContract5ViewBinding
import com.sum.main.ui.CameraActivity
import com.sum.main.ui.PackageActivity
import com.sum.main.ui.contract.viewmodel.ContractViewModel
import com.weigan.loopview.LoopView
import com.weigan.loopview.OnItemSelectedListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.random.Random

/**
 *
 * @author why
 * @date 2023/11/29 19:37
 */
class ContractMod5Fragment : BaseMvvmFragment<FragmentContract5ViewBinding, ContractViewModel>() {
    private var dialog: XDialog? = null

    private val listWork = arrayListOf<BankList.BanksDTO>()

    private var selectPosition = -1

    private var startTime = ""
    private var endTime = ""
    private var sceneType = "BANK"

    override fun initView(view: View, savedInstanceState: Bundle?) {
        startTime = FormatUtil.distime(System.currentTimeMillis())
        mBinding?.tvPrivasi?.onClick{
            MainServiceProvider.toArticleDetail(
                context = requireContext(),
                url = "http://106.14.161.98/contract/privacyagreement.html",
                title = "Perjanjian Privasi"
            )
        }
        mBinding?.flLahir?.onClick {
            showMessageDialog(requireActivity(), 1) {
                if (it != null) {
                    selectPosition = it
                }
                mBinding?.etLahir?.text = it?.let { it1 -> listWork[it1].bankName }
                updateLoginState()
            }
        }

        mBinding?.tvRenzheng?.onClick {
//
            val work = mBinding?.etLahir?.text.toString()
            val income = mBinding?.etBank?.text.toString()
            val companyName = mBinding?.etRekening?.text.toString()

            if(income != companyName) {
                return@onClick
            }

            val data = mutableMapOf<String, String>()
            data.put("bankCode", listWork[selectPosition].bankCode)
            data.put("bankName", listWork[selectPosition].bankName)
            data.put("bankCard", companyName)

            mViewModel.saveUserInfo(data, "bankCard"){
                endTime = FormatUtil.distime(System.currentTimeMillis())
                mViewModel.addRiskControlTracking(startTime, endTime, sceneType)
            }
        }

        mViewModel.contractStep.observe(this) {
            when (it?.step ?: "") {
                "baseInfo",
                "jobInfo",
                "contacts",
                "identify",
                "bankCard",-> {

                }
                else -> {
                    showEndDialog(requireActivity())
                }
            }
        }

        mViewModel.reqBankList( listWork)
        mBinding?.etBank?.let { setEditTextChange(it) }
        mBinding?.etRekening?.let { setEditTextChange(it) }
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
        val work = mBinding?.etLahir?.text.toString()
        val income = mBinding?.etBank?.text.toString()
        val companyName = mBinding?.etRekening?.text.toString()

        val workEnable = !work.isNullOrEmpty()
        val incomeEnable = !income.isNullOrEmpty()
        val comEnable = !companyName.isNullOrEmpty()

        val com = if(income != companyName) {
//                TipsToast.showTips("*Nomor rekening yang dimasukkan salah")
            mBinding?.tvError?.visibility = View.VISIBLE
            false
        } else {
            mBinding?.tvError?.visibility = View.GONE
            true
        }

        mBinding?.tvRenzheng?.isSelected = workEnable && incomeEnable && comEnable && com
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

    private fun initYearView(loopView: LoopView, list: ArrayList<BankList.BanksDTO>) {
        //滚动监听
        loopView.setListener(object : OnItemSelectedListener {
            override fun onItemSelected(index: Int) {

//                dialogDismiss()
            }
        })
        val newList = arrayListOf<String>()
        list.map {
            newList.add(it.bankName)
        }
        //设置原始数据
        loopView.setItems(newList)
        //设置初始位置
        loopView.setInitPosition(list.size / 2)
    }

    private fun showEndDialog(activity: FragmentActivity) {
        XDialog.Builder(activity.supportFragmentManager)
            .setLayoutRes(R.layout.show_end_dialog)
            .setScreenWidthAspect(activity, 0.9f)
            .setTag("PermissionDialog")
            .setDimAmount(0.6f)
            .setCancelableOutside(false)
            .setGravity(Gravity.CENTER)
            .addOnClickListener(com.sum.common.R.id.btn_right, com.sum.common.R.id.iv_dialog_close)
            .setOnViewClickListener(object : OnViewClickListener {
                override fun onViewClick(
                    viewHolder: BindViewHolder?,
                    view: View?,
                    xDialog: XDialog?
                ) {
                    when (view?.id) {
                        com.sum.common.R.id.btn_right -> {
                            requireActivity().startActivity(Intent(requireActivity(), PackageActivity::class.java))
                            requireActivity().finish()
                            xDialog?.dismiss()
                        }

                        com.sum.common.R.id.iv_dialog_close -> {
                            requireActivity().startActivity(Intent(requireActivity(), PackageActivity::class.java))
                            requireActivity().finish()
                            xDialog?.dismiss()
                        }
                    }
                }
            })
            .create()
            .show()
    }
}