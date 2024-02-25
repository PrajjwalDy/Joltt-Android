package com.hindu.joltt.Callback

import com.hindu.joltt.Model.EventModel
import com.hindu.joltt.Model.ResultModel

interface IResultCallback {
    fun onEventLoadFailed(str:String)
    fun onEventLoadSuccess(list:List<ResultModel>)
}