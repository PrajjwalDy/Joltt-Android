package com.hindu.cunow.Fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.hindu.cunow.Callback.IUserCallback
import com.hindu.cunow.Model.UserModel

class ShowUserViewModel : ViewModel() {
    /*private var userLiveData: MutableLiveData<List<UserModel>>? = null
    var showUserList:List<String>? = null
    private val userCallback: IUserCallback = this
    private var messageError: MutableLiveData<String>? =null

    val userModel:MutableLiveData<List<UserModel>>?
        get() {
            if (userLiveData == null){
                userLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadUser()
            }
            val mutableLiveData = userLiveData
            return mutableLiveData
        }

    private fun loadUser(){
        val userList = ArrayList<UserModel>()
        showUserList = ArrayList()

        val userList = FirebaseDatabase.getInstance().reference.child("Follow")

    }*/
}