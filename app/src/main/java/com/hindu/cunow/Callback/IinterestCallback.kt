package com.hindu.cunow.Callback

import com.hindu.cunow.Model.AbroadModel
import com.hindu.cunow.Model.InterestModel

interface IinterestCallback {
    fun onInterestListLoadFailed(str:String)
    fun onInterestListLoadSuccess(list:List<InterestModel>)
}