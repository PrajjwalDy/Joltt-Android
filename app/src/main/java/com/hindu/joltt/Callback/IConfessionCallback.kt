package com.hindu.joltt.Callback

import com.hindu.joltt.Model.ConfessionModel

interface IConfessionCallback {
    fun onConfessionCallbackLoadFailed(str:String)
    fun onConfessionCallbackLoadSuccess(list:List<ConfessionModel>)
}