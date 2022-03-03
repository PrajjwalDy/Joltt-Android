package com.hindu.cunow.Callback

import com.hindu.cunow.Model.CircleModel
import com.hindu.cunow.Model.ConfessionModel

interface ICircleDisplayCallback {
    fun onCircleDisplayLoadFailed(str:String)
    fun onCircleDisplayLoadSuccess(list:List<CircleModel>)
}