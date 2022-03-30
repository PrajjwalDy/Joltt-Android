package com.hindu.cunow.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.hindu.cunow.Callback.IJoinedCircleCallback
import com.hindu.cunow.Callback.INotificationCallback
import com.hindu.cunow.Model.JoinedCircleModel
import com.hindu.cunow.Model.NotificationModel

class NotificationsViewModel : ViewModel(), INotificationCallback {
    private var notificationLiveData: MutableLiveData<List<NotificationModel>>? = null

    private val notificationCallback: INotificationCallback = this
    private var messageError: MutableLiveData<String>? = null

    val notificationViewModel: MutableLiveData<List<NotificationModel>>
    get() {
        if (notificationLiveData == null){
            notificationLiveData = MutableLiveData()
            messageError = MutableLiveData()
            loadNotification()
        }
        val mutableLiveData = notificationLiveData
        return mutableLiveData!!
    }

    private fun loadNotification(){
        val notificationList = ArrayList<NotificationModel>()
        val notificationData = FirebaseDatabase.getInstance().reference.child("Notification")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        notificationData.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snapshot in snapshot.children){
                    val nData = snapshot.getValue(NotificationModel::class.java)
                    notificationList.add(nData!!)
                }
                notificationCallback.onNotificationCallbackLoadSuccess(notificationList)
            }

            override fun onCancelled(error: DatabaseError) {
                notificationCallback.onNotificationCallbackLoadFailed(error.message)
            }

        })
    }

    override fun onNotificationCallbackLoadFailed(str: String) {
        val mutableLiveData = messageError
        mutableLiveData!!.value =str
    }

    override fun onNotificationCallbackLoadSuccess(list: List<NotificationModel>) {
        val mutableLiveData = notificationLiveData
        mutableLiveData!!.value = list
    }
}