package com.hindu.cunow.Callback

import com.hindu.cunow.Model.MyAppModel

interface IApplicationCallback {
    fun onAbroadListLoadFailed(str:String)
    fun onAbroadListLoadSuccess(list:List<MyAppModel>)
}