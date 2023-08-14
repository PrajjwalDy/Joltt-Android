package com.hindu.joltt.Callback

import com.hindu.joltt.Model.AbroadModel

interface IAbroadCallback {
    fun onAbroadListLoadFailed(str:String)
    fun onAbroadListLoadSuccess(list:List<AbroadModel>)
}