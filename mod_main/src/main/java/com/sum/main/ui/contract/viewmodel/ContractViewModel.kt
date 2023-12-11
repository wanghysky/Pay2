package com.sum.main.ui.contract.viewmodel

import androidx.lifecycle.MutableLiveData
import com.sum.common.model.AwardAmount
import com.sum.common.model.BankList
import com.sum.common.model.Complaint
import com.sum.common.model.Contract
import com.sum.common.model.ContractNextStep
import com.sum.common.model.Positive
import com.sum.common.model.SaveUserInfo
import com.sum.common.model.Track
import com.sum.framework.toast.TipsToast
import com.sum.network.callback.IApiErrorCallback
import com.sum.network.manager.ApiManager
import com.sum.network.viewmodel.BaseViewModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * @author mingyan.su
 * @date   2023/3/3 8:12
 * @desc   分类ViewModel
 */
class ContractViewModel : BaseViewModel() {
    val contractStep = MutableLiveData<ContractNextStep?>()
    val positive = MutableLiveData<Positive?>()
    val awardAmountList = MutableLiveData<MutableList<AwardAmount>?>()
    val contractList = MutableLiveData<Pair<MutableList<Contract>?, Int>>()

    val servicePhone = MutableLiveData<String?>()

    /**
     * 获取分类信息
     * 不依赖repository,错误回调实现IApiErrorCallback
     */
    fun getCategoryData() {
        launchUIWithResult(responseBlock = {
            ApiManager.apis.getNextStep()
        }, errorCall = object : IApiErrorCallback {
            override fun onError(code: Int?, error: String?) {
                super.onError(code, error)

            }
        }) {
//            if(it?.livenConfig?.skipLiven == true) {
//                saveUserInfo(mapOf(), it.step)
//            } else {
                contractStep.value = it
//            }
        }
    }

    fun getAwardAmount() {
        launchUIWithResult(responseBlock = {
            ApiManager.apis.getAwardAmount()
        }, errorCall = object : IApiErrorCallback {
            override fun onError(code: Int?, error: String?) {
                super.onError(code, error)

            }
        }) {
            awardAmountList.value = it
        }
    }

    fun reqDictList(type: String, list: MutableList<String>) {
        launchUIWithResult(responseBlock = {
            ApiManager.apis.reqDictList(type)
        }, errorCall = object : IApiErrorCallback {
            override fun onError(code: Int?, error: String?) {
                super.onError(code, error)

            }
        }) { it ->
            it?.dictList?.map {
                list.add(it.typeName)
            }
        }
    }

    fun reqBankList(list: MutableList<BankList.BanksDTO>) {
        launchUIWithResult(responseBlock = {
            ApiManager.apis.reqBankList()
        }, errorCall = object : IApiErrorCallback {
            override fun onError(code: Int?, error: String?) {
                super.onError(code, error)

            }
        }) { it ->
            it?.banks?.map {
                list.add(it)
            }
        }
    }

    fun saveUserInfo(type: Map<String, String>, step: String, next : () -> Unit) {
        launchUIWithResult(responseBlock = {
            ApiManager.apis.saveUserInfo(SaveUserInfo().apply {
                this.data = type
                this.step = step
            })
        }, errorCall = object : IApiErrorCallback {
            override fun onError(code: Int?, error: String?) {
                super.onError(code, error)

            }
        }) { it ->
            next()
            getCategoryData()
        }
    }

    fun check(next:(can: Boolean) -> Unit) {
        launchUIWithResult(responseBlock = {
            ApiManager.apis.check("OCR")
        }, errorCall = object : IApiErrorCallback {
            override fun onError(code: Int?, error: String?) {
                super.onError(code, error)

            }
        }) { it ->
            next(it?.checkResult ?: false)
        }
    }

    fun addRiskControlTracking(startTime: String, endTime: String, sceneType: String) {
        launchUIWithResult(responseBlock = {
            ApiManager.apis.addRiskControlTracking(Track().apply {
                this.startTime = startTime
                this.endTime = endTime
                this.sceneType = sceneType
            })
        }, errorCall = object : IApiErrorCallback {
            override fun onError(code: Int?, error: String?) {
                super.onError(code, error)
            }
        }) { it ->
        }
    }

    fun customerServiceInfo() {
        launchUIWithResult(responseBlock = {
            ApiManager.apis.customerServiceInfo()
        }, errorCall = object : IApiErrorCallback {
            override fun onError(code: Int?, error: String?) {
                super.onError(code, error)

            }
        }) { it ->
            servicePhone.value = it?.phone
        }
    }

    fun reqComplaint(title: String, content: String) {
        launchUIWithResult(responseBlock = {
            ApiManager.apis.complaint(Complaint().apply {
                this.title = title
                this.content = content
            })
        }, errorCall = object : IApiErrorCallback {
            override fun onError(code: Int?, error: String?) {
                super.onError(code, error)

            }
        }) { it ->
            TipsToast.showTips("Operasi yang sukses")
        }
    }

    fun uploads(path: String) {
        val file = File(path)
        val fileRQ: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val part = MultipartBody.Part.createFormData("photo", file.getName(), fileRQ)
        launchUIWithResult(responseBlock = {
            ApiManager.apis.positive2(part)
        }, errorCall = object : IApiErrorCallback {
            override fun onError(code: Int?, error: String?) {
                super.onError(code, error)

            }
        }) { it ->
            positive.value = it

        }
    }

    fun upload(path: String) {
        val file = File(path)
        val fileRQ: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val part = MultipartBody.Part.createFormData("photo", file.getName(), fileRQ)
        launchUIWithResult(responseBlock = {
            ApiManager.apis.upload(part)
        }, errorCall = object : IApiErrorCallback {
            override fun onError(code: Int?, error: String?) {
                super.onError(code, error)

            }
        }) { it ->
            positive.value = it

        }
    }
}