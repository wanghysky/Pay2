package com.sum.main.ui.order

import android.os.Bundle
import android.view.View
import com.sum.framework.base.BaseMvvmFragment
import com.sum.framework.ext.onClick
import com.sum.main.databinding.FragmentOrderBinding
import com.sum.main.ui.ContractActivity
import com.sum.main.ui.home.viewmodel.HomeViewModel

class OrderFragment : BaseMvvmFragment<FragmentOrderBinding, HomeViewModel>() {

    private var mType: String? = "1"
    override fun initView(view: View, savedInstanceState: Bundle?) {
        showFragment("1")
        mBinding?.tvPesananSaatIni?.setOnClickListener {
            showFragment("1")
        }
        mBinding?.tvRiwayatPesanan?.setOnClickListener {
            showFragment("2")
        }
    }

    private fun isShowType() = ("1" == mType)

    private fun showFragment(type: String) {
        mType = type
        mBinding?.tvRiwayatPesanan?.isSelected = !isShowType()
        mBinding?.tvPesananSaatIni?.isSelected = isShowType()
    }

}