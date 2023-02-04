package com.hindu.cunow.Callback

import com.hindu.cunow.Model.CommunityModel

interface ICommunityCallback {
    fun onCommunityCallbackLoadFailed(str:String)
    fun onCommunityCallbackLoadSuccess(list:List<CommunityModel>)
}