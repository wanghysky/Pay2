package com.sum.network.api

import com.sum.common.model.AwardAmount
import com.sum.common.model.BankList
import com.sum.common.model.Check
import com.sum.common.model.Complaint
import com.sum.common.model.ContractNextStep
import com.sum.common.model.DistList
import com.sum.common.model.LoginRequest
import com.sum.common.model.PasswordKey
import com.sum.common.model.PhoneExit
import com.sum.common.model.Positive
import com.sum.common.model.Register
import com.sum.common.model.ResetPassword
import com.sum.common.model.SaveUserInfo
import com.sum.common.model.Service
import com.sum.common.model.SmsCount
import com.sum.common.model.SmsReq
import com.sum.common.model.Track
import com.sum.common.model.UserInfo
import com.sum.network.response.BaseResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 *
 * @author why
 * @date 2023/11/18 23:19
 */
interface PayApi {

    @POST("api/user/phone-exist")
    @FormUrlEncoded
    suspend fun checkPhoneExist(
        @Field("phone") phone: String,
    ): BaseResponse<PhoneExit>?

    @POST("api/user/send-sms-count")
    suspend fun checkSmsTimes(
        @Body gwSmsCountReq: SmsCount,
    ): BaseResponse<Int>?

    @POST("api/user/send-sms")
    suspend fun sendSms(
        @Body gwSmsCountReq: SmsReq,
    ): BaseResponse<Int>?

    @POST("api/user/rsa-key")
    suspend fun getRsaKey(): BaseResponse<PasswordKey>?

    @POST("api/user/register")
    suspend fun register(
        @Body register: Register
    ): BaseResponse<UserInfo>?

    @POST("api/user/login")
    suspend fun login(
        @Body register: LoginRequest
    ): BaseResponse<UserInfo>?


    @POST("api/user/reset-pwd")
    suspend fun resetPwd(
        @Body register: ResetPassword
    ): BaseResponse<Any>?


    @POST("api/loan/get-next-step")
    suspend fun getNextStep(
    ): BaseResponse<ContractNextStep>?

    @POST("api/auth/getAwardAmount")
    suspend fun getAwardAmount(
    ): BaseResponse<MutableList<AwardAmount>>?

    @POST("api/common/dict-list")
    @FormUrlEncoded
    suspend fun reqDictList(
        @Field("type") type: String
    ): BaseResponse<DistList>?

    @POST("api/common/bank-list")
    suspend fun reqBankList(
    ): BaseResponse<BankList>?


    @POST("api/auth/save-user-info")
    suspend fun saveUserInfo(
        @Body body: SaveUserInfo
    ): BaseResponse<DistList>?

    @POST("api/auth/check")
    @FormUrlEncoded
    suspend fun check(
        @Field("authType") authType: String
    ): BaseResponse<Check>?

    @POST("api/common/addRiskControlTracking")
    suspend fun addRiskControlTracking(
        @Body body: Track
    ): BaseResponse<Any>?

    @POST("api/common/customerServiceInfo")
    suspend fun customerServiceInfo(
    ): BaseResponse<Service>?

    @POST("api/user/complaint")
    suspend fun complaint(
        @Body complaintRes: Complaint
    ): BaseResponse<Any>?

    @Multipart
    @POST("api/common/oss-upload-positive2")
    suspend fun positive2(
        @Part file: MultipartBody.Part
    ): BaseResponse<Positive>?

    @Multipart
    @POST("api/common/oss-upload-live2")
    suspend fun upload(
        @Part file: MultipartBody.Part
    ): BaseResponse<Positive>?

}