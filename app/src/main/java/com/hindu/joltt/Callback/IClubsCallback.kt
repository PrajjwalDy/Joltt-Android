package com.hindu.joltt.Callback

import com.hindu.joltt.Model.ClubModel

interface IClubsCallback {
    fun onClubListLoadFailed(str:String)
    fun onClubListLoadSuccess(list:List<ClubModel>)
}