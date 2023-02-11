package com.hindu.cunow.Callback

import com.hindu.cunow.Model.ProjectModel

interface IProjectCallback {
    fun onProjectLoadFailed(str:String)
    fun onProjectLoadSuccess(list:List<ProjectModel>)
}