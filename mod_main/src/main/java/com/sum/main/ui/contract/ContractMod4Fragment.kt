package com.sum.main.ui.contract

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.sum.common.camera.MCamerActivity
import com.sum.common.dialog.XDialog
import com.sum.common.dialog.base.BindViewHolder
import com.sum.common.dialog.listener.OnBindViewListener
import com.sum.common.dialog.listener.OnViewClickListener
import com.sum.common.provider.MainServiceProvider
import com.sum.framework.base.BaseMvvmFragment
import com.sum.framework.ext.onClick
import com.sum.framework.toast.TipsToast
import com.sum.framework.utils.FormatUtil
import com.sum.main.R
import com.sum.main.databinding.FragmentContract4ViewBinding
import com.sum.main.ui.contract.viewmodel.ContractViewModel
import com.weigan.loopview.LoopView
import com.weigan.loopview.OnItemSelectedListener

/**
 *
 * @author why
 * @date 2023/11/29 19:37
 */
class ContractMod4Fragment : BaseMvvmFragment<FragmentContract4ViewBinding, ContractViewModel>() {
    private var dialog: XDialog? = null

    private val listWork = arrayListOf<String>()
    private val listInCom = arrayListOf<String>()

    private var startTime = ""
    private var endTime = ""
    private var sceneType = "LIVENESS"
    override fun initView(view: View, savedInstanceState: Bundle?) {
        startTime = FormatUtil.distime(System.currentTimeMillis())
//        mBinding.
        mBinding?.flKtp?.onClick {
            mViewModel.check {
                if(it) {
                    val intent = Intent(context, MCamerActivity::class.java)
                    (context as FragmentActivity)?.startActivityForResult(intent, 100)
                } else {
                    TipsToast.showTips("Lo sentimos. Aún no es la fecha del evento")
                }
            }
        }
        mBinding?.tvPrivasi?.onClick {
            MainServiceProvider.toArticleDetail(
                context = requireContext(),
                url = "http://106.14.161.98/contract/privacyagreement.html",
                title = "Perjanjian Privasi"
            )
        }
        mViewModel.positive.observe(this) {
            it?.apply {
                if(tongDunOCRResponseEntity.success) {
                    mBinding?.etName?.text = tongDunOCRResponseEntity.idCardData.name
                    mBinding?.etAlamat?.text = tongDunOCRResponseEntity.idCardData.nik
                    mBinding?.flEmpty?.visibility = View.GONE
                    mBinding?.flSuccess?.visibility = View.VISIBLE

                } else {
                    mBinding?.flEmpty?.visibility = View.VISIBLE
                    mBinding?.flSuccess?.visibility = View.GONE
                }
                updateLoginState()
            }
        }
//        mBinding?.flBulanan?.onClick {
//            showMessageDialog(requireActivity(), 2) {
//                mBinding?.etBulanan?.text = it?.let { it1 -> listInCom[it1] }
                updateLoginState()
//            }
//        }
        mBinding?.tvRenzheng?.onClick {
//
            val nik = mViewModel.positive.value?.tongDunOCRResponseEntity?.idCardData?.nik ?: ""
            val name = mViewModel.positive.value?.tongDunOCRResponseEntity?.idCardData?.name ?: ""
            val livenessId =mViewModel.positive.value?.photoName ?: ""

            val data = mutableMapOf<String, String>()
            data.put("idNo", nik)
            data.put("realName", name)
            data.put("identityImg", livenessId)

            mViewModel.saveUserInfo(data, "identify"){
                endTime = FormatUtil.distime(System.currentTimeMillis())
                mViewModel.addRiskControlTracking(startTime, endTime, sceneType)
            }
        }

    }


    private fun updateLoginState() {
        val work = mBinding?.etName?.text.toString()
        val income = mBinding?.etAlamat?.text.toString()


        val workEnable = !work.isNullOrEmpty()
        val incomeEnable = !income.isNullOrEmpty()

        mBinding?.tvRenzheng?.isSelected = workEnable && incomeEnable
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