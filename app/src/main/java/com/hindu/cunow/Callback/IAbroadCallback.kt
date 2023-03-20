package com.hindu.cunow.Callback

import com.hindu.cunow.Model.AbroadModel

interface IAbroadCallback {
    fun onAbroadListLoadFailed(str:String)
    fun onAbroadListLoadSuccess(list:List<AbroadModel>)
}