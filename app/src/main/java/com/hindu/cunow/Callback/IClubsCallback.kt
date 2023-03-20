package com.hindu.cunow.Callback

import com.hindu.cunow.Model.ClubModel

interface IClubsCallback {
    fun onClubListLoadFailed(str:String)
    fun onClubListLoadSuccess(list:List<ClubModel>)
}