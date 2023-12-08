package com.sum.main.ui

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import androidx.lifecycle.ViewModelProvider
import com.sum.common.model.Contract
import com.sum.framework.base.BaseDataBindActivity
import com.sum.framework.utils.StatusBarSettingHelper
import com.sum.main.R
import com.sum.main.databinding.ActivityContractBinding
import com.sum.main.ui.contract.viewmodel.ContractViewModel
import com.sum.main.ui.contract.ContractMod1Fragment
import com.sum.main.ui.contract.ContractMod2Fragment
import com.sum.main.ui.contract.ContractMod3Fragment
import com.sum.main.ui.contract.ContractMod4Fragment
import com.sum.main.ui.contract.ContractMod5Fragment


/**
 *
 * @author why
 * @date 2023/11/28 22:23
 */
class ContractActivity  : BaseDataBindActivity<ActivityContractBinding>() {

    val  mViewModel by lazy {
        ViewModelProvider(this).get(ContractViewModel::class.java)
    }


    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ContractActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        StatusBarSettingHelper.setStatusBarTranslucent(this)
        val fragment = ContractMod1Fragment()
        mViewModel.getAwardAmount()
        mViewModel.getCategoryData()

        mViewModel.contractStep.observe(this) {
            val fragment = when(it?.step ?: "") {
                "baseInfo" -> {
                    showStep1()
                    ContractMod1Fragment()
                }
                "jobInfo" -> {
                    showStep2()
                    ContractMod2Fragment()
                }
                "contacts" -> {
                    showStep3()
                    ContractMod3Fragment()
                }
                "identify" -> {
                    if(it?.livenConfig?.livenChannel == "SILENCE_LIVENESS"){
                        val intent = Intent(this, CameraActivity::class.java)
                        startActivityForResult(intent, 101)
                    }
                    showStep4()
                    ContractMod4Fragment()
                }
                "bankCard" -> {
                    showStep5()
                    ContractMod5Fragment()
                }
                else -> {
                    startActivity(Intent(this, PackageActivity::class.java))
                    finish()
                    null
                }
            }
            if (fragment != null) {
                supportFragmentManager.beginTransaction().replace(R.id.fl_content, fragment, "ContractMod1Fragment").commitAllowingStateLoss()
            }
        }
        mViewModel.awardAmountList.observe(this) {
            it?.map {
                when(it.step) {
                    "baseInfo" -> {
                        mBinding.tvCc1.text = it.amount
                    }
                    "jobInfo" -> {
                        mBinding.tvCc2.text = it.amount
                    }
                    "contacts" -> {
                        mBinding.tvCc3.text = it.amount
                    }
                    "identify" -> {
                        mBinding.tvCc4.text = it.amount
                    }
                    "bankCard" -> {
                        mBinding.tvCc5.text = it.amount
                    }
                    else -> {

                    }
                }
            }
        }
    }

    fun showStep1() {
        mBinding.tvCc1.isSelected = true
        mBinding.tvCc2.isSelected = false
        mBinding.tvCc3.isSelected = false
        mBinding.tvCc4.isSelected = false
        mBinding.tvCc5.isSelected = false
    }

    fun showStep2() {
        mBinding.tvCc1.isSelected = true
        mBinding.tvCc2.isSelected = true
        mBinding.tvCc3.isSelected = false
        mBinding.tvCc4.isSelected = false
        mBinding.tvCc5.isSelected = false
    }

    fun showStep3() {
        mBinding.tvCc1.isSelected = true
        mBinding.tvCc2.isSelected = true
        mBinding.tvCc3.isSelected = true
        mBinding.tvCc4.isSelected = false
        mBinding.tvCc5.isSelected = false
    }

    fun showStep4() {
        mBinding.tvCc1.isSelected = true
        mBinding.tvCc2.isSelected = true
        mBinding.tvCc3.isSelected = true
        mBinding.tvCc4.isSelected = true
        mBinding.tvCc5.isSelected = false
    }

    fun showStep5() {
        mBinding.tvCc1.isSelected = true
        mBinding.tvCc2.isSelected = true
        mBinding.tvCc3.isSelected = true
        mBinding.tvCc4.isSelected = true
        mBinding.tvCc5.isSelected = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100) {
            val path = data?.getStringExtra("path")
            if (path != null) {
                mViewModel.uploads(path)
            }
        } else {
            // 从Intent通讯录返回拿到电话号码
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    showToast("抱歉，您选择的联系人没有电话，请重新选择！")
                    return
                }
                val uri = data.data
                val id = uri!!.lastPathSegment
                setPhoneNumsByContactId(id, requestCode)
            }
        }
    }

    /** 存储选中用户名和手机  */
    private var phoneMap: HashMap<String, String>? = null

    /**
     * 设置选中联系人信息
     *
     * @param contactId
     * 联系人ID
     */
    fun setPhoneNumsByContactId(contactId: String?, requestCode: Int) {
        val phonecursor: Cursor? = getContentResolver().query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
            arrayOf<String?>(contactId),
            null
        )
        phonecursor ?.let {
            val contractList: MutableList<Contract> = ArrayList()
            while (it.moveToNext()) {
                val phoneName: String =
                    it.getString(it.getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME))

                val phoneNum = it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))

                if (phoneNum.isNotEmpty()) {
                    contractList.add(Contract().apply {
                        name = phoneName
                        phone = phoneNum
                    })
                }
            }
            if (contractList.size == 0) {
                showToast("抱歉，您选择的联系人没有电话，请重新选择！")
            } else if (contractList.size > 0) {
                mViewModel.contractList.value = Pair(contractList, requestCode)
            }
        }

    }


}