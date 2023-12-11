package com.sum.main.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.sum.common.dialog.XDialog
import com.sum.common.dialog.base.BindViewHolder
import com.sum.common.dialog.listener.OnBindViewListener
import com.sum.common.dialog.listener.OnViewClickListener
import com.sum.common.model.Contract
import com.sum.framework.base.BaseDataBindActivity
import com.sum.framework.ext.onClick
import com.sum.framework.toast.TipsToast
import com.sum.framework.utils.AppExit
import com.sum.framework.utils.StatusBarSettingHelper
import com.sum.framework.utils.getColorFromResource
import com.sum.framework.utils.getStringFromResource
import com.sum.framework.weights.ClearEditText
import com.sum.main.R
import com.sum.main.databinding.ActivityContractBinding
import com.sum.main.ui.contract.viewmodel.ContractViewModel
import com.sum.main.ui.contract.ContractMod1Fragment
import com.sum.main.ui.contract.ContractMod2Fragment
import com.sum.main.ui.contract.ContractMod3Fragment
import com.sum.main.ui.contract.ContractMod4Fragment
import com.sum.main.ui.contract.ContractMod5Fragment
import com.weigan.loopview.LoopView
import kotlin.random.Random


/**
 *
 * @author why
 * @date 2023/11/28 22:23
 */
class ContractActivity : BaseDataBindActivity<ActivityContractBinding>() {

    private var serviceDialog: XDialog? = null

    private var phone = ""

    private var perExit = true

    val mViewModel by lazy {
        ViewModelProvider(this).get(ContractViewModel::class.java)
    }

    private val exitString = arrayListOf("Satu langkah lagi untuk mendapatkan pinjaman",
        "Hanya membutuhkan waktu 3 menit untuk menyelesaikan sertifikasi",
        "Apakah Anda yakin tidak membutuhkan pinjaman?")


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
        mViewModel.customerServiceInfo()
        mBinding.ivService.onClick {
            showMessageDialog(this)
        }
        mViewModel.servicePhone.observe(this) {
            phone = it ?: "***"
        }
        mViewModel.contractStep.observe(this) {
            val fragment = when (it?.step ?: "") {
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
                    if (it?.livenConfig?.livenChannel == "SILENCE_LIVENESS") {
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
                    if(supportFragmentManager.findFragmentByTag("ContractModFragment") == null) {
                        startActivity(Intent(this, PackageActivity::class.java))
                        finish()
                    }
                    null
                }
            }
            if (fragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fl_content, fragment, "ContractModFragment")
                    .commitAllowingStateLoss()
            }
        }
        mViewModel.awardAmountList.observe(this) {
            it?.map {
                when (it.step) {
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
        initSelect(1)
    }

    fun showStep2() {
        mBinding.tvCc1.isSelected = true
        mBinding.tvCc2.isSelected = true
        mBinding.tvCc3.isSelected = false
        mBinding.tvCc4.isSelected = false
        mBinding.tvCc5.isSelected = false
        initSelect(2)
    }

    fun showStep3() {
        mBinding.tvCc1.isSelected = true
        mBinding.tvCc2.isSelected = true
        mBinding.tvCc3.isSelected = true
        mBinding.tvCc4.isSelected = false
        mBinding.tvCc5.isSelected = false
        initSelect(3)
    }

    fun showStep4() {
        mBinding.tvCc1.isSelected = true
        mBinding.tvCc2.isSelected = true
        mBinding.tvCc3.isSelected = true
        mBinding.tvCc4.isSelected = true
        mBinding.tvCc5.isSelected = false
        initSelect(4)
    }

    fun showStep5() {
        mBinding.tvCc1.isSelected = true
        mBinding.tvCc2.isSelected = true
        mBinding.tvCc3.isSelected = true
        mBinding.tvCc4.isSelected = true
        mBinding.tvCc5.isSelected = true
        initSelect(5)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
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
        phonecursor?.let {
            val contractList: MutableList<Contract> = ArrayList()
            while (it.moveToNext()) {
                val phoneName: String =
                    it.getString(it.getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME))

                val phoneNum =
                    it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))

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

    private fun showMessageDialog(activity: FragmentActivity) {
        dialogDismiss()
        serviceDialog = XDialog.Builder(activity.supportFragmentManager)
            .setLayoutRes(R.layout.show_service_dialog)
            .setScreenWidthAspect(activity, 0.9f)
            .setTag("PermissionDialog")
            .setDimAmount(0.6f)
            .setCancelableOutside(false)
            .setGravity(Gravity.CENTER)
            .setOnBindViewListener(object : OnBindViewListener {
                override fun bindView(viewHolder: BindViewHolder?) {
                    val view = viewHolder?.getView<ClearEditText>(R.id.et_feedback)
                    val tvPhone = viewHolder?.getView<TextView>(R.id.tv_phone)
                    val tvCount = viewHolder?.getView<TextView>(R.id.tv_feedback_count)
                    view?.addTextChangedListener {
                        val length = it?.length ?: 0
                        tvCount?.text = "$length/200"
                    }
                    tvPhone?.text = phone
                }
            })
            .addOnClickListener(R.id.tv_kirim, R.id.iv_copy, R.id.iv_close)
            .setOnViewClickListener(object : OnViewClickListener {
                override fun onViewClick(
                    viewHolder: BindViewHolder?,
                    view: View?,
                    xDialog: XDialog?
                ) {
                    when (view?.id) {
                        R.id.tv_kirim -> {
                            val view = viewHolder?.getView<ClearEditText>(R.id.et_feedback)
                            mViewModel.reqComplaint(
                                mViewModel.contractStep.value?.step ?: "",
                                view?.text.toString()
                            )
                            dialogDismiss()
                        }

                        R.id.iv_copy -> {
                            val manager: ClipboardManager =
                                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val mClipData: ClipData = ClipData.newPlainText("Label", phone)
                            manager.setPrimaryClip(mClipData)
                            TipsToast.showTips("Tersalin")
                        }

                        R.id.iv_close -> {
                            dialogDismiss()
                        }
                    }
                }
            })
            .create()
            .show()
    }

    private fun dialogDismiss() {
        serviceDialog?.dismiss()
        serviceDialog = null
    }

    private fun initSelect(count: Int) {
        val spaBuilder = SpannableStringBuilder("$count/5")
        spaBuilder.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {

                }

                @RequiresApi(Build.VERSION_CODES.M)
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = getColorFromResource(com.sum.common.R.color.color_f2ee90)
                    ds.isUnderlineText = false
                    ds.clearShadowLayer()
                }
            },
            0,
            1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        mBinding.tvPage.setText(spaBuilder, TextView.BufferType.SPANNABLE)
    }

    fun showExitDialog(activity: FragmentActivity) {
        XDialog.Builder(activity.supportFragmentManager)
            .setLayoutRes(R.layout.show_exit_dialog)
            .setScreenWidthAspect(activity, 0.9f)
            .setTag("PermissionDialog")
            .setDimAmount(0.6f)
            .setCancelableOutside(false)
            .setGravity(Gravity.CENTER)
            .setOnBindViewListener(object : OnBindViewListener {
                override fun bindView(viewHolder: BindViewHolder?) {
                    val view = viewHolder?.getView<TextView>(R.id.tv_content)
                    view?.text = exitString[Random.nextInt(3)]
                }
            })
            .addOnClickListener(com.sum.common.R.id.btn_right, com.sum.common.R.id.iv_dialog_close)
            .setOnViewClickListener(object : OnViewClickListener {
                override fun onViewClick(
                    viewHolder: BindViewHolder?,
                    view: View?,
                    xDialog: XDialog?
                ) {
                    when (view?.id) {
                        com.sum.common.R.id.btn_right -> {
                            xDialog?.dismiss()
                        }

                        com.sum.common.R.id.iv_dialog_close -> {
                            xDialog?.dismiss()
                        }
                    }
                }
            })
            .create()
            .show()
    }

    override fun onBackPressed() {
        if (perExit) {
            showExitDialog(this)
            perExit = false
        } else {
            super.onBackPressed()
        }
    }

}