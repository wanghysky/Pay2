package com.sum.main.ui.system

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.sum.framework.base.BaseMvvmFragment
import com.sum.framework.decoration.NormalItemDecoration
import com.sum.framework.ext.toJson
import com.sum.framework.ext.visible
import com.sum.framework.utils.dpToPx
import com.sum.main.R
import com.sum.main.databinding.FragmentSystemBinding
import com.sum.main.ui.system.adapter.SystemAdapter
import com.sum.main.ui.system.viewmodel.SystemViewModel

/**
 * @author mingyan.su
 * @date   2023/3/3 8:18
 * @desc   体系
 */
class SystemFragment : BaseMvvmFragment<FragmentSystemBinding, SystemViewModel>() {
    private lateinit var mAdapter: SystemAdapter

    override fun initView(view: View, savedInstanceState: Bundle?) {
        mAdapter = SystemAdapter()
        mBinding?.recyclerView?.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
            addItemDecoration(NormalItemDecoration().apply {
                setBounds(left = dpToPx(8), top = dpToPx(10), right = dpToPx(8), bottom = dpToPx(10))
                setLastBottom(true)
            })
        }
        mAdapter.onItemClickListener = { view: View, position: Int ->
            val item = mAdapter.getItem(position)
            ArticleTabActivity.startIntent(requireContext(), item?.toJson(true))
        }
    }

    override fun initData() {
        showLoading()
        mViewModel.getSystemList().observe(this) {
            mAdapter.setData(it)
            dismissLoading()
        }
        mViewModel.errorListLiveData.observe(this) {
            //空数据视图
            mBinding?.viewEmptyData?.visible()
        }
    }
}