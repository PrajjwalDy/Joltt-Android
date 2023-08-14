package com.hindu.joltt.Callback

import com.hindu.joltt.Model.CommunityModel

interface ICommunityCallback {
    fun onCommunityCallbackLoadFailed(str:String)
    fun onCommunityCallbackLoadSuccess(list:List<CommunityModel>)
}