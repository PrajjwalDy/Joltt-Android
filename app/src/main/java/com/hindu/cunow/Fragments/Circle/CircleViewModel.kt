package com.hindu.cunow.Fragments.Circle

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hindu.cunow.Callback.ICircleDisplayCallback
import com.hindu.cunow.Model.CircleModel

class CircleViewModel : ViewModel() {
    private var circleDisplayLiveData:MutableLiveData<List<CircleModel>>? = null

    private val circleDisplayCallback: ICircleDisplayCallback = this
    private var messageError:MutableLiveData<String>? = null

    val circleViewModel:MutableLiveData<List>

}