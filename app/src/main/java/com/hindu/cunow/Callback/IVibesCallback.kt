package com.hindu.cunow.Callback

import com.hindu.cunow.Model.CircleModel
import com.hindu.cunow.Model.VibesModel

interface IVibesCallback {
    fun onVibesLoadFailed(str:String)
    fun onVibesLoadSuccess(list:List<VibesModel>)
}