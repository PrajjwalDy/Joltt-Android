package com.hindu.cunow.Callback

import com.hindu.cunow.Model.PageModel

interface IMyPagesLoadCallback {
    fun onPageLoadFailed(str:String)
    fun onPageLoadSuccess(list:List<PageModel>)
}