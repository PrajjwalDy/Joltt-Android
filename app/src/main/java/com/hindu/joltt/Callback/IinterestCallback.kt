package com.hindu.joltt.Callback

import com.hindu.joltt.Model.InterestModel

interface IinterestCallback {
    fun onInterestListLoadFailed(str:String)
    fun onInterestListLoadSuccess(list:List<InterestModel>)
}