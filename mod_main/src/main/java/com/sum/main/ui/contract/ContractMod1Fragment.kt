package com.sum.main.ui.contract

import android.os.Bundle
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.view.Gravity
import android.view.View
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.sum.common.dialog.XDialog
import com.sum.common.dialog.base.BindViewHolder
import com.sum.common.dialog.listener.OnBindViewListener
import com.sum.common.dialog.listener.OnViewClickListener
import com.sum.common.provider.MainServiceProvider
import com.sum.framework.base.BaseMvvmFragment
import com.sum.framework.ext.onClick
import com.sum.framework.ext.textChangeFlow
import com.sum.framework.utils.FormatUtil
import com.sum.main.R
import com.sum.main.databinding.FragmentContract1ViewBinding
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
class ContractMod1Fragment : BaseMvvmFragment<FragmentContract1ViewBinding, ContractViewModel>() {
    private var dialog: XDialog? = null

    private val listYear = arrayListOf<String>()
    private val listEdu = arrayListOf<String>()
    private val listWed = arrayListOf<String>()

    private var startTime = ""
    private var endTime = ""
    private var sceneType = "INFORMATION"

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
                mBinding?.etLahir?.text = it?.let { it1 -> listYear[it1] }
                updateLoginState()
            }
        }
        mBinding?.flPendidikan?.onClick {
            showMessageDialog(requireActivity(), 2) {
                mBinding?.etPendidikan?.text = it?.let { it1 -> listEdu[it1] }
                updateLoginState()
            }
        }
        mBinding?.flNikah?.onClick {
            showMessageDialog(requireActivity(), 3) {
                mBinding?.etNikah?.text = it?.let { it1 -> listWed[it1] }
                updateLoginState()
            }
        }
        mBinding?.tvRenzheng?.onClick {
//            "liveAddr":"aaa", // 居住地址
//  "education":"Tidak bersekolah", // 教育程度
//  "marital":"Belum kawin", // 婚姻状况
//  "birthYear":"1990" //出生年
            val phone = mBinding?.etTinggal?.text.toString()
            val year = mBinding?.etLahir?.text.toString()
            val edu = mBinding?.etPendidikan?.text.toString()
            val wed = mBinding?.etNikah?.text.toString()

            val data = mutableMapOf<String, String>()
            data.put("liveAddr", phone)
            data.put("education", edu)
            data.put("marital", wed)
            data.put("birthYear", year)
            mViewModel.saveUserInfo(data, "baseInfo") {
                endTime = FormatUtil.distime(System.currentTimeMillis())
                mViewModel.addRiskControlTracking(startTime, endTime, sceneType)
            }
        }
        for (i in 18..60) {
            listYear.add("$i")
        }
        mViewModel.reqDictList("EDUCATION", listEdu)
        mViewModel.reqDictList("WEDDING_STATUS", listWed)
        mBinding?.etTinggal?.let { setEditTextChange(it) }
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
        val phone = mBinding?.etTinggal?.text.toString()
        val year = mBinding?.etLahir?.text.toString()
        val edu = mBinding?.etPendidikan?.text.toString()
        val wed = mBinding?.etNikah?.text.toString()

        val phoneEnable = !phone.isNullOrEmpty()
        val yearEnable = !year.isNullOrEmpty()
        val eduEnable = !edu.isNullOrEmpty()
        val wedEnable = !wed.isNullOrEmpty()

        mBinding?.tvRenzheng?.isSelected = phoneEnable && yearEnable && eduEnable && wedEnable
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
                                1 -> listYear
                                2 -> listEdu
                                3 -> listWed
                                else -> listYear
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