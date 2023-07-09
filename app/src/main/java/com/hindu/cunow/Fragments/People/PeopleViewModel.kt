package com.hindu.cunow.Fragments.People

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Callback.IUserCallback
import com.hindu.cunow.Model.UserModel

class PeopleViewModel : ViewModel(), IUserCallback {
    private var userLiveData: MutableLiveData<List<UserModel>>? = null

    private val userCallback: IUserCallback = this
    private var messageError: MutableLiveData<String>? = null

    val userModel: MutableLiveData<List<UserModel>>?
        get() {
            if (userLiveData == null) {
                userLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadUser()
            }
            val mutableLiveData = userLiveData
            return mutableLiveData
        }

    private fun loadUser() {
        val userList = ArrayList<UserModel>()
        val userData = FirebaseDatabase.getInstance().reference.child("Users")
        userData.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snapshot in snapshot.children) {
                    val user = snapshot.getValue(UserModel::class.java)
                    userList.add(user!!)
                }
                userCallback.onUserLoadSuccess(userList)
            }

            override fun onCancelled(error: DatabaseError) {
                userCallback.onUserLoadFailed(error.message)
            }

        })
    }

    override fun onUserLoadFailed(str: String) {
        val mutableLiveData = messageError
        mutableLiveData!!.value = str
    }

    override fun onUserLoadSuccess(list: List<UserModel>) {
        val mutableLiveData = userLiveData
        mutableLiveData!!.value = list
    }
}