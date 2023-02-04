package com.hindu.cunow.Callback

import com.hindu.cunow.Model.HackathonModel

interface IHackathonCallback {
    fun onHackathonLoadFailed(str:String)
    fun onHackathonLoadSuccess(list:List<HackathonModel>)
}