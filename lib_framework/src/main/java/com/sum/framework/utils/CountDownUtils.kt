package com.sum.framework.utils

import android.os.CountDownTimer
import android.widget.TextView

/**
 * Des    :倒计时工具类
 * Author : master.
 * Date   : 2018/9/15 .
 */
object CountDownUtils {
    private var countDown: CountDownTimer? = null
    private val textViewList = mutableListOf<TextView?>()
    private val callBackList = mutableListOf<CallBack?>()
    var type = 0
    /**
     * 石佛
     */
    var isOpenSleepSwitch = true

    /**
     *  倒计时类型
     */
    val times = intArrayOf(0, 15, 30, 45, 60)
    val selectItems = arrayListOf("不开启", "15分钟", "30分钟", "45分钟", "60分钟", "自定义")
    /**
     * 总共时间
     */
    var totalTime = 0L


    /**
     * 开始倒计时
     */
    fun starCountDownById(id: Int) {
        totalTime = times[id] * 60 * 1000L
        start(totalTime, id)
    }

    /**
     * 开始倒计时
     */
    fun starCountDownByTime(time: Int) {
        totalTime = time * 60 * 1000L
        start(totalTime, 5)
    }


    /**
     * 开始倒计时
     * @param time 时间戳
     * @param type 倒计时类型
     */
    fun start(time: Long, type: Int) {
        this.type = type
        try {
            countDown?.cancel()
        } catch (e: Throwable) {

        } finally {
            countDown = object : CountDownTimer(time, 1000) {
                override fun onFinish() {
                    callBackList.forEach {
                       it?.finish()
                    }
//                    textViewList.forEach {
//                        it?.text = "Send"
//                    }
                    this@CountDownUtils.type = -1
                }

                override fun onTick(millisUntilFinished: Long) {
                    textViewList.forEach {
                        it?.text = (millisUntilFinished/1000).toInt().toString() + "s"
                    }
                }
            }.start()
        }
    }

    /**
     * 添加TextView到 textViewList，倒计时
     */
    fun addTextView(tv: TextView, callBack: CallBack) {
        textViewList.add(tv)
        callBackList.add(callBack)
    }

    /**
     * 移除倒计时View
     */
    fun removeTextView(tv: TextView) {
        textViewList.remove(tv)
    }

    /**
     * 取消倒计时
     */
    fun cancel() {
        type = 0
        try {
            textViewList.forEach {
                it?.text = null
            }
            countDown?.cancel()
        } catch (e: Throwable) {
        }
    }

    interface CallBack {
        fun finish()
    }

}