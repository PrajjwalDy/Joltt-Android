package com.hindu.cunow.Fragments.Circle.JoinedCircles

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Callback.ICircleDisplayCallback
import com.hindu.cunow.Callback.IJoinedCircleCallback
import com.hindu.cunow.Model.CircleModel
import com.hindu.cunow.Model.JoinedCircleModel

class JoinedCirclesViewModel : ViewModel(), IJoinedCircleCallback {
    private var circleDisplayLiveData: MutableLiveData<List<JoinedCircleModel>>? = null

    private val circleDisplayCallback: IJoinedCircleCallback = this
    private var messageError: MutableLiveData<String>? = null




    val circleViewModel: MutableLiveData<List<JoinedCircleModel>>
        get() {
            if (circleDisplayLiveData == null){
                circleDisplayLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadCircles()
            }
            val mutableLiveData = circleDisplayLiveData
            return mutableLiveData!!
        }

    private fun loadCircles() {
        val circleList = ArrayList<JoinedCircleModel>()
        val database = FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("Joined_Circles")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(snapshot in snapshot.children){
                    val dataModel = snapshot.getValue(JoinedCircleModel::class.java)
                    circleList.add(dataModel!!)
                }
                circleList.reverse()
                circleDisplayCallback.onJoinedCircleLoadSuccess(circleList)
            }

            override fun onCancelled(error: DatabaseError) {
                circleDisplayCallback.onJoinedCircleLoadFailed(error.message)
            }

        })
    }

    override fun onJoinedCircleLoadFailed(str: String) {
        val mutableLiveData = messageError
        mutableLiveData!!.value = str
    }

    override fun onJoinedCircleLoadSuccess(list: List<JoinedCircleModel>) {
        val mutableLiveData = circleDisplayLiveData
        mutableLiveData!!.value = list
    }

}