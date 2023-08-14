package com.hindu.joltt.Callback

import com.hindu.joltt.Model.ESModel

interface ISkillCallback {
    fun onSkillListLoadFailed(str: String)
    fun onSkillListLoadSuccess(list: List<ESModel>)

}