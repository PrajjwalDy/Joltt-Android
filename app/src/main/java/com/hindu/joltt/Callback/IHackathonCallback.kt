package com.hindu.joltt.Callback

import com.hindu.joltt.Model.HackathonModel

interface IHackathonCallback {
    fun onHackathonLoadFailed(str:String)
    fun onHackathonLoadSuccess(list:List<HackathonModel>)
}