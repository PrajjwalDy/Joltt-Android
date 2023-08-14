package com.hindu.joltt.Callback

import com.hindu.joltt.Model.VibesModel

interface IVibesCallback {
    fun onVibesLoadFailed(str:String)
    fun onVibesLoadSuccess(list:List<VibesModel>)
}