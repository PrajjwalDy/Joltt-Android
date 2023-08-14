package com.hindu.joltt.Callback

import com.hindu.joltt.Model.MyAppModel

interface IApplicationCallback {
    fun onAbroadListLoadFailed(str:String)
    fun onAbroadListLoadSuccess(list:List<MyAppModel>)
}