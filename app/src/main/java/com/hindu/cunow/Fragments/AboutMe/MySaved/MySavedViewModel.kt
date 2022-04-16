package com.hindu.cunow.Fragments.AboutMe.MySaved

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.Callback.IPostCallback
import com.hindu.cunow.Model.PostModel

class MySavedViewModel : ViewModel(), IPostCallback {
    private var postLiveData: MutableLiveData<List<PostModel>>? = null

    var mySavedImg: List<String>? = null

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
        mySavedImg = ArrayList()
        val dataReference = FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("SavedPosts")

        dataReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    for (snapshot in snapshot.children){
                        (mySavedImg as ArrayList<String>).add(snapshot.key!!)
                    }
                    readSaved()
                }

               /* for (snapshot in snapshot.children){
                    val postModel = snapshot.getValue<PostModel>(PostModel::class.java) as PostModel
                        postList.add(postModel)

                }*/
                postLoadCallback.onPostPCallbackLoadSuccess(postList)
            }

            override fun onCancelled(error: DatabaseError) {
                postLoadCallback.onPostCallbackLoadFailed(error.message)
            }

        })
    }

    private fun readSaved(){
        val postList=ArrayList<PostModel>()
        val postRef = FirebaseDatabase.getInstance().reference.child("Post")
        postRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    postList.clear()
                    for (snapshot in snapshot.children){
                        val post = snapshot.getValue(PostModel::class.java)

                        for (key in mySavedImg!!){
                            if (post!!.postId == key){
                                postList.add(post)
                            }
                        }
                    }
                }
                postLoadCallback.onPostPCallbackLoadSuccess(postList)
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