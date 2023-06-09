package com.hindu.cunow.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Callback.IPostCallback
import com.hindu.cunow.Model.PostModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel(), IPostCallback {

    private var postLiveData:MutableLiveData<List<PostModel>>? = null

    var followingList:MutableList<String>? = null
    var pageList:MutableList<String>? = null
    private val postLoadCallback: IPostCallback = this
    private var messageError: MutableLiveData<String>? = null
    var lastLoadedKey: String? = null
    var loading = false


    val postModel: MutableLiveData<List<PostModel>>?
        get() {
            if (postLiveData == null) {
                postLiveData = MutableLiveData()
                messageError = MutableLiveData()

                viewModelScope.launch(Dispatchers.IO) {
                    pageList()
                    checkFollowing()
                }
            }
            return postLiveData

        }



    private suspend fun loadPost() {
        val postList=ArrayList<PostModel>()
        val dataReference = FirebaseDatabase.getInstance().reference.child("Post")
        dataReference.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                postList.clear()
                for (snapshot in snapshot.children){
                    val postModel = snapshot.getValue<PostModel>(PostModel::class.java) as PostModel
                    for (id in (followingList as ArrayList<String>)){
                        if (postModel.publisher == id){
                            postList.add(postModel)
                        }
                    }
                    for (id in (pageList as ArrayList<String>)) {
                        if (postModel.publisher == id) {
                            postList.add(postModel)
                        }
                    }

                }
                postLoadCallback.onPostPCallbackLoadSuccess(postList)
            }

            override fun onCancelled(error: DatabaseError) {
                postLoadCallback.onPostCallbackLoadFailed(error.message)
            }



        })
        dataReference.keepSynced(true)
    }

    private suspend fun checkFollowing(){
        followingList = ArrayList()

        val userDataRef = FirebaseDatabase.getInstance().reference.child("Follow")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Following")
        userDataRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    (followingList as ArrayList<String>).clear()

                    for (snapshot in snapshot.children){
                        snapshot.key?.let { (followingList as ArrayList<String>).add(it) }
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        loadPost()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        userDataRef.keepSynced(true)
    }

    /*private fun loadPagePost(){
        val postList=ArrayList<PostModel>()
        val dataReference = FirebaseDatabase.getInstance().reference.child("Post")
        dataReference.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                postList.clear()
                for (snapshot in snapshot.children){
                    val postModel = snapshot.getValue<PostModel>(PostModel::class.java) as PostModel
                    for (id in (pageList as ArrayList<String>)){
                        if (postModel.publisher == id){
                            postList.add(postModel)
                        }
                    }
                }
                postLoadCallback.onPostPCallbackLoadSuccess(postList)
            }

            override fun onCancelled(error: DatabaseError) {
                postLoadCallback.onPostCallbackLoadFailed(error.message)
            }

        })
    }*/

    private suspend fun pageList(){
        pageList = ArrayList()
        val data = FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("FollowingPages")

        data.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    (pageList as ArrayList<String>).clear()
                    for (snapshot in snapshot.children){
                        snapshot.key?.let { (pageList as ArrayList<String>).add(it) }
                        //loadPagePost()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }



    override fun onPostCallbackLoadFailed(str: String) {
        val mutableLiveData = messageError
        mutableLiveData!!.value = str
    }

    override fun onPostPCallbackLoadSuccess(list: List<PostModel>) {
        val mutableLiveData = postLiveData
        mutableLiveData!!.value = list
    }


}