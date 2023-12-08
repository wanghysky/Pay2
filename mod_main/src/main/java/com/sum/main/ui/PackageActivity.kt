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
import com.sum.main.databinding.ActivityPackageBinding
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
class PackageActivity  : BaseDataBindActivity<ActivityPackageBinding>() {

    val  mViewModel by lazy {
        ViewModelProvider(this).get(ContractViewModel::class.java)
    }


    companion object {
        fun start(context: Context) {
            val intent = Intent(context, PackageActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        StatusBarSettingHelper.setStatusBarTranslucent(this)

    }





}