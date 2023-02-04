package com.hindu.cunow.Callback

import com.hindu.cunow.Model.CourseModel

interface ICourseCallback {
    fun onCourseCallbackLoadFailed(str:String)
    fun onCourseCallbackLoadSuccess(list:List<CourseModel>)


}