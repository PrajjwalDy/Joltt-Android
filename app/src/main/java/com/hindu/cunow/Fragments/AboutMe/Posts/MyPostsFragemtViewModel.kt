package com.hindu.cunow.Fragments.AboutMe.Posts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Callback.IPostCallback
import com.hindu.cunow.Model.PostModel

class MyPostsFragemtViewModel : ViewModel(), IPostCallback {
    private var postLiveData: MutableLiveData<List<PostModel>>? = null

    private val postLoadCallback: IPostCallback = this
    private var messageError: MutableLiveData<String>? = null


    val postModel: MutableLiveData<List<PostModel>>?
        get() {
            if (postLiveData == null){
                postLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadPost()
            }
            val mutableLiveData = postLiveData
            return mutableLiveData

        }



    private fun loadPost() {
        val postList=ArrayList<PostModel>()
        val dataReference = FirebaseDatabase.getInstance().reference.child("Post")
        dataReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snapshot in snapshot.children){
                    val postModel = snapshot.getValue<PostModel>(PostModel::class.java) as PostModel
                    if(postModel.publisher == FirebaseAuth.getInstance().currentUser!!.uid){
                        postList.add(postModel)
                    }
                }
                postLoadCallback.onPostPCallbackLoadSuccess(postList)
            }

            override fun onCancelled(error: DatabaseError) {
                postLoadCallback.onPostCallbackLoadFailed(error.message)
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

//    override fun onPostCallbackLoadFailed(str: String) {
//        val mutableLiveData = messageError
//        mutableLiveData!!.value = str
//    }
//
//    override fun onPostPCallbackLoadSuccess(list: List<PostModel>) {
//        val mutableLiveData = postLiveData
//
//        mutableLiveData!!.value = list
//    }
}