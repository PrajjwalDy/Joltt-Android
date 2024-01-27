package com.hindu.joltt.Fragments.Chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.joltt.Callback.IChatListCallback
import com.hindu.joltt.Model.UserModel

class ChatViewModel : ViewModel(), IChatListCallback {
    private var userLiveData: MutableLiveData<List<UserModel>>? = null
    var chatList: List<String>? = null
    private val userCallback: IChatListCallback = this
    private var messageError: MutableLiveData<String>? =null


    val chatListModel:MutableLiveData<List<UserModel>>?
    get() {
        if(userLiveData == null){
            userLiveData = MutableLiveData()
            messageError = MutableLiveData()
            loadChatList()
        }
        val mutableLiveData = userLiveData
        return mutableLiveData

    }

    private fun loadChatList() {
        val userList = ArrayList<UserModel>()
        chatList = ArrayList()

        val dataReference  = FirebaseDatabase.getInstance().reference
            .child("ChatList")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        dataReference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for(snapshot  in snapshot.children){
                        (chatList as ArrayList<String>).add(snapshot.key!!)
                    }
                    loadUser()
                }
                userCallback.onChatListLoadSuccess(userList)
            }

            override fun onCancelled(error: DatabaseError) {
                userCallback.onChatListLoadFailed(error.message)
            }

        })
        dataReference.keepSynced(true)
    }

    private fun loadUser() {
        val userList = ArrayList<UserModel>()
        val userData = FirebaseDatabase.getInstance().reference.child("Users")
        userData.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    (userList as ArrayList<UserModel>).clear()
                    for(snapshot in snapshot.children){
                        val user = snapshot.getValue(UserModel::class.java)
                        for (key in chatList!!){
                            if(user!!.uid == key){
                                (userList as ArrayList<UserModel>).add(user)
                            }
                        }
                    }
                }
                userCallback.onChatListLoadSuccess(userList)

            }

            override fun onCancelled(error: DatabaseError) {
                userCallback.onChatListLoadFailed(error.message)
            }

        })
        userData.keepSynced(true)
    }

    override fun onChatListLoadFailed(str: String) {
        val mutableLiveData = messageError
        mutableLiveData!!.value = str
    }

    override fun onChatListLoadSuccess(list: List<UserModel>) {
        val mutableLiveData = userLiveData
        mutableLiveData!!.value = list
    }


}