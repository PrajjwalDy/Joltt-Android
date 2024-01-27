package com.hindu.joltt.Fragments.AboutMe.MyDetailsUsers

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.joltt.Adapter.ExpAdapter
import com.hindu.joltt.Adapter.InterestAdapter_Prof
import com.hindu.joltt.Adapter.SkillAdapter
import com.hindu.joltt.Model.ESModel
import com.hindu.joltt.Model.InterestModel
import com.hindu.joltt.Model.UserModel

class UserDetails : Fragment() {
    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser
    var myInterest: List<String>? = null

    var recyclerView: RecyclerView? = null
    private var interestAdapter: InterestAdapter_Prof? = null

    private var expAdapter_Prof: ExpAdapter? = null
    private var skillAdapter: SkillAdapter? = null
    private var interestList: MutableList<InterestModel>? = null
    private var ESList: MutableList<ESModel>? = null


    //Animations
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            context,
            R.anim.to_bottom_anim
        )
    }
    private val layout: Animation by lazy {
        AnimationUtils.loadAnimation(
            context,
            R.anim.from_bottom2
        )
    }



    private lateinit var skill_txt_user: TextView
    private lateinit var skill_btn_user: CardView

    private lateinit var experience_btn_user: CardView
    private lateinit var experience_txt_user: TextView

    private lateinit var interest_btn_user: CardView
    private lateinit var interest_txt_user: TextView

    private lateinit var ll_skills_user: LinearLayout
    private lateinit var ll_interests_user: LinearLayout

    private lateinit var ll_about_user: LinearLayout

    private lateinit var ll_experience_user: LinearLayout

    private lateinit var post_btn_user: CardView


    private lateinit var from_users: TextView
    private lateinit var institutionName_user: TextView
    private lateinit var student_course_user: TextView
    private lateinit var branch_user: TextView
    private lateinit var year_user: TextView

    private lateinit var genderImage_user: ImageView
    private lateinit var gender_txt_user: TextView
    private lateinit var RS_Image_user: ImageView
    private lateinit var relation_txt_user: TextView






    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root: View = inflater.inflate(R.layout.fragment_user_details, container, false)
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            this.profileId = pref.getString("uid", "none")!!
        }



        skill_btn_user = root.findViewById(R.id.skill_btn_user)
        skill_txt_user = root.findViewById(R.id.skill_txt_user)

        experience_btn_user = root.findViewById(R.id.experience_btn_user)
        experience_txt_user = root.findViewById(R.id.experience_txt_user)
        interest_btn_user = root.findViewById(R.id.interest_btn_user)

        interest_txt_user = root.findViewById(R.id.interest_txt_user)
        ll_skills_user = root.findViewById(R.id.ll_skills_user)
        ll_interests_user = root.findViewById(R.id.ll_interests_user)

        ll_about_user = root.findViewById(R.id.ll_about_user)
        ll_experience_user = root.findViewById(R.id.ll_experience_user)

        post_btn_user = root.findViewById(R.id.post_btn_user)

        relation_txt_user = root.findViewById(R.id.relation_txt_user)

        from_users = root.findViewById(R.id.from_users)
        institutionName_user = root.findViewById(R.id.institutionName_user)
        student_course_user = root.findViewById(R.id.student_course_user)
        branch_user = root.findViewById(R.id.branch_user)
        year_user = root.findViewById(R.id.year_user)
        genderImage_user = root.findViewById(R.id.genderImage_user)

        gender_txt_user = root.findViewById(R.id.gender_txt_user)
        RS_Image_user = root.findViewById(R.id.RS_Image_user)




        retrieveUserData(root)

        //Skill Button
        skill_btn_user.setOnClickListener {
            val recyclerView: RecyclerView = root.findViewById(R.id.user_skill_rv)

            skill_btn_user.setCardBackgroundColor(Color.parseColor("#FF3A63"))
            skill_txt_user.setTextColor(Color.WHITE)

            experience_btn_user.setCardBackgroundColor(Color.parseColor("#54CBAF"))
            experience_txt_user.setTextColor(Color.parseColor("#226880"))

            interest_btn_user.setCardBackgroundColor(Color.parseColor("#54CBAF"))
            interest_txt_user.setTextColor(Color.parseColor("#226880"))

            //visibility of the Layouts

            ll_skills_user.visibility = View.VISIBLE
            ll_skills_user.startAnimation(layout)

            ll_interests_user.visibility = View.GONE
            ll_interests_user.startAnimation(toBottom)

            ll_about_user.visibility = View.GONE
            ll_about_user.startAnimation(toBottom)

            ll_experience_user.visibility = View.GONE
            ll_experience_user.startAnimation(toBottom)

            initView2(recyclerView)
            ESList = ArrayList()
            skillAdapter = context?.let { it1 -> SkillAdapter(it1, ESList as ArrayList<ESModel>) }
            recyclerView.adapter = skillAdapter
            loadSkills()
        }
        //Interest Button
        interest_btn_user.setOnClickListener {
            val recyclerView1: RecyclerView = root.findViewById(R.id.user_interest_rv)

            interest_btn_user.setCardBackgroundColor(Color.parseColor("#FF3A63"))
            interest_txt_user.setTextColor(Color.WHITE)

            experience_btn_user.setCardBackgroundColor(Color.parseColor("#54CBAF"))
            experience_txt_user.setTextColor(Color.parseColor("#226880"))

            skill_btn_user.setCardBackgroundColor(Color.parseColor("#54CBAF"))
            skill_txt_user.setTextColor(Color.parseColor("#226880"))


            //visibility of the Layouts
            ll_interests_user.visibility = View.VISIBLE
            ll_interests_user.startAnimation(layout)

            ll_about_user.visibility = View.GONE
            ll_about_user.startAnimation(toBottom)

            ll_experience_user.visibility = View.GONE
            ll_experience_user.startAnimation(toBottom)

            ll_skills_user.visibility = View.GONE
            ll_skills_user.startAnimation(toBottom)


            //loadData
            initView(recyclerView1, 3)
            interestList = ArrayList()
            interestAdapter = context?.let { it1 ->
                InterestAdapter_Prof(
                    it1,
                    interestList as ArrayList<InterestModel>
                )
            }
            recyclerView1.adapter = interestAdapter
            loadInterest()
        }
        //Experience button
        experience_btn_user.setOnClickListener {

            val recyclerView: RecyclerView = root.findViewById(R.id.user_experience_rv)

            experience_btn_user.setCardBackgroundColor(Color.parseColor("#FF3A63"))
            experience_txt_user.setTextColor(Color.WHITE)

            skill_btn_user.setCardBackgroundColor(Color.parseColor("#54CBAF"))
            skill_txt_user.setTextColor(Color.parseColor("#226880"))

            interest_btn_user.setCardBackgroundColor(Color.parseColor("#54CBAF"))
            interest_txt_user.setTextColor(Color.parseColor("#226880"))

            //visibility and animation of the layouts

            ll_experience_user.visibility = View.VISIBLE
            ll_experience_user.startAnimation(layout)

            ll_skills_user.visibility = View.GONE
            ll_skills_user.startAnimation(toBottom)

            ll_interests_user.visibility = View.GONE
            ll_interests_user.startAnimation(toBottom)

            ll_about_user.visibility = View.GONE
            ll_about_user.startAnimation(toBottom)



            initView(recyclerView, 2)
            ESList = ArrayList()
            expAdapter_Prof = context?.let { it1 -> ExpAdapter(it1, ESList as ArrayList<ESModel>) }
            recyclerView.adapter = expAdapter_Prof
            loadExperience()
        }

        post_btn_user.setOnClickListener {
            val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
            pref?.putString("profile", profileId)
            pref?.apply()

            Navigation.findNavController(root)
                .navigate(R.id.action_userDetails_to_userPostsFragment)

        }

        return root
    }

    private fun retrieveUserData(root: View) {
        val dataRef = FirebaseDatabase
            .getInstance().reference
            .child("Users")
            .child(
                profileId
            )

        dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val users = snapshot.getValue(UserModel::class.java)
                    from_users.text = users!!.place
                    branch_user.text = users.branch
                    year_user.text = "Year: " + users.year
                    institutionName_user.text = users.college
                    student_course_user.text = users.course

                    if (users.male) {
                        genderImage_user.setImageResource(R.drawable.male)
                        gender_txt_user.text = "Male"
                        if (users.single) {
                            RS_Image_user.setImageResource(R.drawable.single_male)
                            relation_txt_user.text = "Single"
                        } else if (users.committed) {
                            RS_Image_user.setImageResource(R.drawable.com_male)
                            relation_txt_user.text = "Committed"
                        } else if (users.crush) {
                            RS_Image_user.setImageResource(R.drawable.crush_male)
                            relation_txt_user.text = "Have a Crush"
                        }
                    } else if (users.female) {
                        genderImage_user.setImageResource(R.drawable.female)
                        gender_txt_user.text = "Female"
                        if (users.single) {
                            RS_Image_user.setImageResource(R.drawable.single)
                            relation_txt_user.text = "Single"
                        } else if (users.committed) {
                            RS_Image_user.setImageResource(R.drawable.com_female)
                            relation_txt_user.text = "Committed"
                        } else if (users.crush) {
                            RS_Image_user.setImageResource(R.drawable.crush_female)
                            relation_txt_user.text = "Have a Crush"
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
    private fun initView(recyclerView: RecyclerView, spanCount: Int) {
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(context, spanCount)
        recyclerView.layoutManager = linearLayoutManager
    }
    private fun initView2(recyclerView: RecyclerView) {
        recyclerView.isNestedScrollingEnabled = false
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager
    }

    private fun readInterest() {
        val intRef = FirebaseDatabase.getInstance().reference.child("interests")
        intRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    interestList!!.clear()
                    for (snapshot in snapshot.children) {
                        val interest = snapshot.getValue(InterestModel::class.java)
                        for (key in myInterest!!) {
                            if (interest!!.inteID == key) {
                                interestList!!.add(interest)
                            }
                            interestList!!.reverse()
                        }
                        interestAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun loadInterest() {
        val interestList = ArrayList<InterestModel>()
        myInterest = ArrayList()
        val database = FirebaseDatabase.getInstance().reference.child("InterestUser")
            .child(profileId)

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snapshot in snapshot.children) {
                        (myInterest as ArrayList<String>).add(snapshot.key!!)
                    }
                    readInterest()
                    interestAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun loadSkills() {
        val skillData = FirebaseDatabase.getInstance().getReference("Skills")
            .child(profileId)
        skillData.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (snapshot in snapshot.children){
                        val data = snapshot.getValue(ESModel::class.java)
                        ESList?.add(data!!)
                    }
                    skillAdapter!!.notifyDataSetChanged()
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun loadExperience() {
        val expData = FirebaseDatabase.getInstance().getReference("Experience")
            .child(profileId)
        expData.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (snapshot in snapshot.children){
                        val data = snapshot.getValue(ESModel::class.java)
                        ESList?.add(data!!)
                    }
                    expAdapter_Prof!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

}