package com.sum.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import com.alibaba.android.arouter.facade.annotation.Route
import com.sum.common.constant.KEY_INDEX
import com.sum.common.constant.MAIN_ACTIVITY_HOME
import com.sum.framework.base.BaseDataBindActivity
import com.sum.framework.ext.onClick
import com.sum.framework.log.LogUtil
import com.sum.framework.toast.TipsToast
import com.sum.framework.utils.AppExit
import com.sum.framework.utils.StatusBarSettingHelper
import com.sum.main.databinding.ActivityMainBinding
import com.sum.main.ui.home.HomeFragment2

/**
 *
 * @author why
 * @date 2023/11/27 15:27
 */
@Route(path = MAIN_ACTIVITY_HOME)
class MainActivity : BaseDataBindActivity<ActivityMainBinding>() {


    companion object {
        fun start(context: Context, index: Int = 0) {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(KEY_INDEX, index)
            context.startActivity(intent)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        StatusBarSettingHelper.setStatusBarTranslucent(this)
        StatusBarSettingHelper.statusBarLightMode(this@MainActivity, true)
        selectHome()
        mBinding.ivHome.onClick {
            selectHome()
        }
        mBinding.ivMe.onClick {
            selectMe()
        }
        mBinding.ivList.onClick {
            selectMList()
        }

        val fragment = HomeFragment2()
        supportFragmentManager.beginTransaction().add(R.id.fl_home, fragment, "HomeFragment2").commitAllowingStateLoss()
//        val fragment2 = MeFragment()
//        supportFragmentManager.beginTransaction().add(R.id.fl_home, fragment, HomeFragment.TAG).commitAllowingStateLoss()

    }

    private fun selectHome() {
        mBinding.ivHome.isSelected = true
        mBinding.ivList.isSelected = false
        mBinding.ivMe.isSelected = false
        mBinding.flHome.visibility = View.VISIBLE
        mBinding.flMe.visibility = View.GONE
        mBinding.flList.visibility = View.GONE
    }

    private fun selectMe() {
        mBinding.ivHome.isSelected = false
        mBinding.ivList.isSelected = false
        mBinding.ivMe.isSelected = true
        mBinding.flHome.visibility = View.GONE
        mBinding.flMe.visibility = View.VISIBLE
        mBinding.flList.visibility = View.GONE
    }

    private fun selectMList() {
        mBinding.ivHome.isSelected = false
        mBinding.ivList.isSelected = true
        mBinding.ivMe.isSelected = false
        mBinding.flHome.visibility = View.GONE
        mBinding.flMe.visibility = View.GONE
        mBinding.flList.visibility = View.VISIBLE
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            val index = intent.getIntExtra(KEY_INDEX, 0)
//            navController.navigate(R.id.navi_home)
            LogUtil.e("onNewIntent:index:$index", tag = "smy")
        }
    }

    //延迟初始化执行的任务 此处的时机应该是在页面渲染首帧后执行 这里暂时放在onWindowFocusChanged()
    //利用 IDLEHandler 特性，主线程空闲时机执行
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        //延迟执行启动器
//        DelayInitDispatcher().addTask(InitTaskA())
//                .addTask(InitTaskB())
//                .start()
    }

    override fun onBackPressed() {
//        if (mCurFragment?.onBackPressed() == true) {
//            return
//        }
        AppExit.onBackPressed(
            this,
            { TipsToast.showTips(getString(R.string.app_exit_one_more_press)) }
        )
    }


}