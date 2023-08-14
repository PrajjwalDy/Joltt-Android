package com.hindu.joltt.Callback

import com.hindu.joltt.Model.CircleModel

interface ICircleDisplayCallback {
    fun onCircleDisplayLoadFailed(str:String)
    fun onCircleDisplayLoadSuccess(list:List<CircleModel>)
}