package com.hindu.joltt.Callback

import com.hindu.joltt.Model.RequestModel

interface IFollowRequestCallback {
    fun onRequestCallbackLoadFailed(str:String)
    fun onRequestCallbackLoadSuccess(list:List<RequestModel>)
}