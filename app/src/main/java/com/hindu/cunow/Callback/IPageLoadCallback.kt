package com.hindu.cunow.Callback
import com.hindu.cunow.Model.PageModel

interface IPageLoadCallback {
    fun onPageLoadFailed(str:String)
    fun onPageLoadSuccess(list:List<PageModel>)
}