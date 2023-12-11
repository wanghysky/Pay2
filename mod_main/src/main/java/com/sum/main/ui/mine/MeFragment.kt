package com.sum.main.ui.mine

import android.os.Bundle
import android.view.View
import com.sum.framework.base.BaseMvvmFragment
import com.sum.framework.ext.onClick
import com.sum.main.databinding.FragmentHomeBinding
import com.sum.main.ui.ContractActivity
import com.sum.main.ui.home.viewmodel.HomeViewModel
import com.sum.main.ui.mine.viewmodel.MineViewModel

/**
 *
 * @author why
 * @date 2023/12/10 23:37
 */
class MeFragment: BaseMvvmFragment<FragmentHomeBinding, MineViewModel>() {
    override fun initView(view: View, savedInstanceState: Bundle?) {
        mBinding?.tvRenzheng?.onClick {
            ContractActivity.start(requireContext())
        }
    }


}