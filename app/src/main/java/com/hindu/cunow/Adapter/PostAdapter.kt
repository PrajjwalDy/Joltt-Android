package com.hindu.cunow.Adapter

import android.content.Context
import android.os.AsyncTask
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hindu.cunow.Model.PostModel
import com.hindu.cunow.Model.UserModel
import com.hindu.cunow.R
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class PostAdapter(private val mContext: Context,
                  private val mPost:List<PostModel>):RecyclerView.Adapter<PostAdapter.ViewHolder>()
{
                      private var firebaseUser:FirebaseUser? = null



    companion object{
        const val NUM_CACHED_VIEWS = 5

    }
    private val asyncLayoutInflater = AsyncLayoutInflater(mContext)
    private val cachedViews = Stack<View>()

    init {
        for (i in 0..NUM_CACHED_VIEWS){
            asyncLayoutInflater.inflate(R.layout.post_layout,null){view,layoutRes, viewGroup ->
                cachedViews.push(view)
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        val view = if (cachedViews.isEmpty()){
            LayoutInflater.from(mContext).inflate(R.layout.post_layout,parent,false)
        }else{
            cachedViews.pop().also { it.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT) }
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mPost[position],mContext)

        //AutoSync(holder.toString(),holder.publisherName,mPost[position].publisher!!,
           // holder.publisherImage,false,holder.verification,).execute()
    }

    override fun getItemCount(): Int {
        return mPost.size
    }


    inner class AutoSync(
                         val value:String,val publisherName:TextView,val publisherId:String, val profileImage:CircleImageView,
                         val verification: Boolean,val verifImg: CircleImageView):AsyncTask<Any?,Void,String>(){


        override fun doInBackground(vararg p0: Any?): String? {
            publisher(profileImage,publisherName,publisherId,verification,verifImg)
            return value
        }

        override fun onPreExecute() {
            publisher(profileImage,publisherName,publisherId,verification,verifImg)
            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            publisher(profileImage,publisherName,publisherId,verification,verifImg)
            super.onPostExecute(result)
        }

    }

    fun publisher(profileImage:CircleImageView, name:TextView,publisherId:String,verification:Boolean,verifImage:CircleImageView){

        val userDataRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherId)

        userDataRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(UserModel::class.java)
                    Glide.with(mContext).load(data!!.profileImage).into(profileImage)
                    name.text = data.fullName
                    if (verification){
                        Glide.with(mContext).load(data.verifImage).into(verifImage)
                    }else{
                        verifImage.visibility =View.GONE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun loadPost(postImage:ImageView,caption:TextView,postId:String){
        val postData = FirebaseDatabase.getInstance().reference.child("Post").child(postId)

        postData.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    val data = snapshot.getValue(PostModel::class.java)
                    if (data!!.image == ""){
                        postImage.visibility = View.GONE
                    }else{
                        Glide.with(mContext).load(data.image).into(postImage)
                    }
                    caption.text = data.caption
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    private fun enablePersistence(){
        Firebase.database.setPersistenceEnabled(true)
    }

    private fun keepSynced(){
        val postData = Firebase.database.getReference("Post")
        postData.keepSynced(true)
    }


    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
        val image: ImageView = itemView.findViewById(R.id.postImage)
        val caption: TextView = itemView.findViewById(R.id.caption)
        val publisherImage:CircleImageView = itemView.findViewById(R.id.publisher_profile_image)
        val publisherName: TextView = itemView.findViewById(R.id.publisher_Name)
        val verification:CircleImageView = itemView.findViewById(R.id.verification_image)

        fun bind(list:PostModel,context: Context){
            caption.text = list.caption
            if (list!!.image == ""){
                image.visibility = View.GONE
            }else{
                Glide.with(mContext).load(list.image).into(image)
            }

        }
    }
}