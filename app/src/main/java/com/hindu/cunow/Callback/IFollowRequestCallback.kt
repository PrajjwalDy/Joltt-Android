package com.hindu.cunow.Callback

import com.hindu.cunow.Model.ConfessionModel
import com.hindu.cunow.Model.RequestModel

interface IFollowRequestCallback {
    fun onRequestCallbackLoadFailed(str:String)
    fun onRequestCallbackLoadSuccess(list:List<RequestModel>)
}