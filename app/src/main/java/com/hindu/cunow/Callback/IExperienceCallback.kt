package com.hindu.cunow.Callback

import com.hindu.cunow.Model.ESModel

interface IExperienceCallback {
    fun onExpListLoadFailed(str: String)
    fun onExpListLoadSuccess(list: List<ESModel>)
}