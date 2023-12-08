package com.sum.main.ui.home

import android.os.Bundle
import android.view.View
import com.sum.framework.base.BaseMvvmFragment
import com.sum.framework.ext.onClick
import com.sum.main.databinding.FragmentHomeBinding
import com.sum.main.databinding.FragmentHomeLayoutBinding
import com.sum.main.ui.ContractActivity
import com.sum.main.ui.contract.ContractView
import com.sum.main.ui.home.viewmodel.HomeViewModel

/**
 *
 * @author why
 * @date 2023/11/28 16:32
 */
class HomeFragment2 : BaseMvvmFragment<FragmentHomeBinding, HomeViewModel>() {
    override fun initView(view: View, savedInstanceState: Bundle?) {
        mBinding?.tvRenzheng?.onClick {
            ContractActivity.start(requireContext())
        }
    }


}