package com.hindu.cunow.Callback

import com.hindu.cunow.Model.HashTagModel

interface IHashTagCallBack {
    fun onTagListLoadFailed(str:String)
    fun onTagListLoadSuccess(list:List<HashTagModel>)

}