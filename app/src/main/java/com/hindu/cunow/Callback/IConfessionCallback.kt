package com.hindu.cunow.Callback

import com.hindu.cunow.Model.ConfessionModel

interface IConfessionCallback {
    fun onConfessionCallbackLoadFailed(str:String)
    fun onConfessionCallbackLoadSuccess(list:List<ConfessionModel>)
}