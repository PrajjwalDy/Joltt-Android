package com.hindu.joltt.Callback

import com.hindu.joltt.Model.HashTagModel

interface IHashTagCallBack {
    fun onTagListLoadFailed(str:String)
    fun onTagListLoadSuccess(list:List<HashTagModel>)

}