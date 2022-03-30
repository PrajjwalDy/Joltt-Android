package com.hindu.cunow.Fragments.Circle.MyCircles

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Callback.ICircleDisplayCallback
import com.hindu.cunow.Model.CircleModel

class MyCirclesViewModel : ViewModel(), ICircleDisplayCallback {
    private var circleDisplayLiveData: MutableLiveData<List<CircleModel>>? = null

    private val circleDisplayCallback: ICircleDisplayCallback = this
    private var messageError: MutableLiveData<String>? = null

    val circleViewModel:MutableLiveData<List<CircleModel>>?
        get() {
            if (circleDisplayLiveData == null){
                circleDisplayLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadCircles()
            }
            val mutableLiveData = circleDisplayLiveData
            return mutableLiveData
        }

    private fun loadCircles() {
        val circleList = ArrayList<CircleModel>()
        val database = FirebaseDatabase.getInstance().reference.child("Circle")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(snapshot in snapshot.children){
                    val dataModel = snapshot.getValue(CircleModel::class.java)
                    if(dataModel!!.admin == FirebaseAuth.getInstance().currentUser!!.uid){
                        circleList.add(dataModel)
                    }
                }
                circleDisplayCallback.onCircleDisplayLoadSuccess(circleList)
            }

            override fun onCancelled(error: DatabaseError) {
                circleDisplayCallback.onCircleDisplayLoadFailed(error.message)
            }

        })
    }

    override fun onCircleDisplayLoadFailed(str: String) {
        val mutableLiveData = messageError
        mutableLiveData!!.value = str
    }

    override fun onCircleDisplayLoadSuccess(list: List<CircleModel>) {
        val mutableLiveData = circleDisplayLiveData
        mutableLiveData!!.value = list
    }


}