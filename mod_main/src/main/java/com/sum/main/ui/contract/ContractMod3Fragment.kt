package com.sum.main.ui.contract

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
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
import com.sum.main.databinding.FragmentContract3ViewBinding
import com.sum.main.ui.contract.viewmodel.ContractViewModel
import com.tbruyelle.rxpermissions3.RxPermissions
import com.weigan.loopview.LoopView
import com.weigan.loopview.OnItemSelectedListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.json.JSONArray
import org.json.JSONObject


/**
 *
 * @author why
 * @date 2023/11/29 19:37
 */
class ContractMod3Fragment : BaseMvvmFragment<FragmentContract3ViewBinding, ContractViewModel>() {
    private var dialog: XDialog? = null

    private val listStatus = arrayListOf<String>()
    private val listContract = arrayListOf<String>()

    private var clickType = 0

    override fun initView(view: View, savedInstanceState: Bundle?) {
        mBinding?.clStatus1?.onClick {
            showMessageDialog(requireActivity(), 1) {
                mBinding?.tvStatus1?.text = it?.let { it1 -> listStatus[it1] }
                updateLoginState()
            }
        }
        mBinding?.clStatus2?.onClick {
            showMessageDialog(requireActivity(), 1) {
                mBinding?.tvStatus2?.text = it?.let { it1 -> listStatus[it1] }
                updateLoginState()
            }
        }

        mViewModel.contractList.observe(this) { it ->
            val type = clickType
            val list = it.first
            if(list?.size ?: 0 >= 2) {
                listContract.clear()
                list?.map {
                    listContract.add(it.phone)
                }
                showMessageDialog(requireActivity(), 2) {
                    if(type == 1000) {
                        mBinding?.etName?.setText(it?.let { it1 -> list?.get(it1)?.name })
                        mBinding?.etAlamat?.setText(it?.let { it1 -> list?.get(it1)?.phone })
                    } else {
                        mBinding?.etName2?.setText(it?.let { it1 -> list?.get(it1)?.name })
                        mBinding?.etAlamat2?.setText(it?.let { it1 -> list?.get(it1)?.phone })
                    }
                }
            } else {
                list?.map {
                    if(type == 1000) {
                        mBinding?.etName?.setText(it.name)
                        mBinding?.etAlamat?.setText(it.phone)
                    } else {
                        mBinding?.etName2?.setText(it.name)
                        mBinding?.etAlamat2?.setText(it.phone)
                    }
                }
            }

        }

        mBinding?.ivConcat?.onClick {
            clickType = 1000
            // 跳到通讯录
            requestPermission(1000)
        }
        mBinding?.ivConcat2?.onClick {
            clickType = 1001
            // 跳到通讯录
            requestPermission(1001)
        }
        mBinding?.tvRenzheng?.onClick {
//
            val name = mBinding?.etName?.text.toString()
            val name2 = mBinding?.etName2?.text.toString()
            val etAlamat = mBinding?.etAlamat?.text.toString()
            val etAlamat2 = mBinding?.etAlamat2?.text.toString()
            val status1 = mBinding?.tvStatus1?.text.toString()
            val status2 = mBinding?.tvStatus1?.text.toString()

            val data = mutableMapOf<String, String>()

            val jsonArr = JSONArray()
            val json = JSONObject()
            json.put("phone", name)
            json.put("name", etAlamat)
            json.put("relation", status1)
            val json2 = JSONObject()
            json2.put("phone", name2)
            json2.put("name", etAlamat2)
            json2.put("relation", status2)
            jsonArr.put(json)
            jsonArr.put(json2)

            data.put("contacts", jsonArr.toString())


            mViewModel.saveUserInfo(data, "contacts")
        }
//        for (i in 18..60) {
//            listYear.add("$i")
//        }
        mViewModel.reqDictList("RELATIONSHIP", listStatus)
        mBinding?.etName?.let { setEditTextChange(it) }
        mBinding?.etName2?.let { setEditTextChange(it) }
        mBinding?.etAlamat?.let { setEditTextChange(it) }
        mBinding?.etAlamat2?.let { setEditTextChange(it) }
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
        val name = mBinding?.etName?.text.toString()
        val name2 = mBinding?.etName2?.text.toString()
        val etAlamat = mBinding?.etAlamat?.text.toString()
        val etAlamat2 = mBinding?.etAlamat2?.text.toString()
        val status1 = mBinding?.tvStatus1?.text.toString()
        val status2 = mBinding?.tvStatus1?.text.toString()

        val nameEn = !name.isNullOrEmpty()
        val name2En = !name2.isNullOrEmpty()
        val etAlamatEN = !etAlamat.isNullOrEmpty()
        val etAlamat2EN = !etAlamat2.isNullOrEmpty()
        val status1En = !status1.isNullOrEmpty()
        val status2En = !status2.isNullOrEmpty()

        mBinding?.tvRenzheng?.isSelected = nameEn && name2En && etAlamatEN && etAlamat2EN && status1En && status2En
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
                            it, if(type == 1) listStatus else listContract
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

    private fun requestPermission(requestCode: Int) {
        RxPermissions(this).requestEachCombined(
            Manifest.permission.READ_CONTACTS,
        ).subscribe { permission ->
            if (permission.granted) {
                // 授权成功
                val INTENT_CONTACT = 1000
                val intent = Intent()
                intent.action = Intent.ACTION_PICK
                intent.data = ContactsContract.Contacts.CONTENT_URI
                startActivityForResult(intent, requestCode)
            } else {

            }
        }
    }
}