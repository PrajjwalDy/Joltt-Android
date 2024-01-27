package com.hindu.joltt.Fragments.AboutMe.MyDetails

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hindu.cunow.R
import com.hindu.cunow.databinding.MyDetailsFragmentBinding
import com.hindu.joltt.Adapter.ExpAdapter
import com.hindu.joltt.Adapter.InterestAdapter_Prof
import com.hindu.joltt.Adapter.SkillAdapter
import com.hindu.joltt.Model.UserModel

class MyDetailsFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    private var interestAdapter: InterestAdapter_Prof? = null
    private lateinit var viewModel: MyDetailsViewModel

    private var expAdapter_Prof: ExpAdapter? = null
    private var skillAdapter: SkillAdapter? = null

    private var _binding: MyDetailsFragmentBinding? = null
    private val binidng get() = _binding!!

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
    private val navigation: Animation by lazy {
        AnimationUtils.loadAnimation(
            context,
            R.anim.nav_anim
        )
    }

    private lateinit var skill_btn_cv:CardView
    private lateinit var skill_btn_txt:TextView
    private lateinit var skill_btn:RelativeLayout

    private lateinit var experience_btn_cv:CardView
    private lateinit var experience_btn_tv:TextView

    private lateinit var interest_btn_cv:CardView
    private lateinit var interest_btn_tv:TextView

    private lateinit var ll_skills:LinearLayout
    private lateinit var ll_interests:LinearLayout

    private lateinit var ll_about_profile:LinearLayout

    private lateinit var ll_experience:LinearLayout

    private lateinit var experience_btn:RelativeLayout

    private lateinit var postBtn_btn_cv:CardView


    private lateinit var from_profile:TextView
    private lateinit var institutionName:TextView
    private lateinit var student_course:TextView
    private lateinit var branch_profile:TextView
    private lateinit var year_profile:TextView

    private lateinit var gender_txt:TextView
    private lateinit var genderImage_profile:ImageView
    private lateinit var RS_Image_profile:ImageView
    private lateinit var relation_txt:TextView




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(MyDetailsViewModel::class.java)
        _binding = MyDetailsFragmentBinding.inflate(inflater, container, false)
        val root: View = binidng.root


        skill_btn = root.findViewById(R.id.skill_btn)
        skill_btn_cv = root.findViewById(R.id.skill_btn_cv)
        skill_btn_txt = root.findViewById(R.id.skill_btn_txt)

        experience_btn_cv = root.findViewById(R.id.experience_btn_cv)
        experience_btn_tv = root.findViewById(R.id.experience_btn_tv)
        interest_btn_cv = root.findViewById(R.id.interest_btn_cv)

        interest_btn_tv = root.findViewById(R.id.interest_btn_tv)
        ll_skills = root.findViewById(R.id.ll_skills)
        ll_interests = root.findViewById(R.id.ll_interests)

        ll_about_profile = root.findViewById(R.id.ll_about_profile)
        ll_experience = root.findViewById(R.id.ll_experience)

        experience_btn = root.findViewById(R.id.experience_btn)

        postBtn_btn_cv = root.findViewById(R.id.postBtn_btn_cv)

        from_profile = root.findViewById(R.id.from_profile)
        institutionName = root.findViewById(R.id.institutionName)
        student_course = root.findViewById(R.id.student_course)
        branch_profile = root.findViewById(R.id.branch_profile)
        year_profile = root.findViewById(R.id.year_profile)
        gender_txt = root.findViewById(R.id.gender_txt)

        genderImage_profile = root.findViewById(R.id.genderImage_profile)
        RS_Image_profile = root.findViewById(R.id.RS_Image_profile)

        relation_txt = root.findViewById(R.id.relation_txt)

        


        //Top Function Call
        retrieveUserData(root)

        //Skill Button
        skill_btn.setOnClickListener {
            val recyclerView: RecyclerView = root.findViewById(R.id.profile_skill_rv)

            skill_btn_cv.setCardBackgroundColor(Color.parseColor("#FF3A63"))
            skill_btn_txt.setTextColor(Color.WHITE)

            experience_btn_cv.setCardBackgroundColor(Color.parseColor("#54CBAF"))
            experience_btn_tv.setTextColor(Color.parseColor("#226880"))

            interest_btn_cv.setCardBackgroundColor(Color.parseColor("#54CBAF"))
            interest_btn_tv.setTextColor(Color.parseColor("#226880"))

            //visibility of the Layouts

            ll_skills.visibility = View.VISIBLE
            ll_skills.startAnimation(layout)

            ll_interests.visibility = View.GONE
            ll_interests.startAnimation(toBottom)

            ll_about_profile.visibility = View.GONE
            ll_about_profile.startAnimation(toBottom)

            ll_experience.visibility = View.GONE
            ll_experience.startAnimation(toBottom)

            viewModel.mySkillModel!!.observe(viewLifecycleOwner, Observer {
                initView2(recyclerView)
                skillAdapter = context?.let { it1 -> SkillAdapter(it1, it) }
                recyclerView.adapter = skillAdapter
                skillAdapter!!.notifyDataSetChanged()
            })

        }

        //Interest Button
        interest_btn_cv.setOnClickListener {

            //Changing the color theme of the Card View
            interest_btn_cv.setCardBackgroundColor(Color.parseColor("#FF3A63"))
            interest_btn_tv.setTextColor(Color.WHITE)

            experience_btn_cv.setCardBackgroundColor(Color.parseColor("#54CBAF"))
            experience_btn_tv.setTextColor(Color.parseColor("#226880"))

            skill_btn_cv.setCardBackgroundColor(Color.parseColor("#54CBAF"))
            skill_btn_txt.setTextColor(Color.parseColor("#226880"))


            val recyclerView1: RecyclerView = root.findViewById(R.id.profile_interest_rv)


            //visibility of the Layouts
            ll_interests.visibility = View.VISIBLE
            ll_interests.startAnimation(layout)

            ll_about_profile.visibility = View.GONE
            ll_about_profile.startAnimation(toBottom)

            ll_experience.visibility = View.GONE
            ll_experience.startAnimation(toBottom)

            ll_skills.visibility = View.GONE
            ll_skills.startAnimation(toBottom)


            //loadData
            viewModel.myInterestModel!!.observe(viewLifecycleOwner, Observer {
                initView(recyclerView1, 3)
                interestAdapter = context?.let { it1 -> InterestAdapter_Prof(it1, it) }
                recyclerView1.adapter = interestAdapter
                interestAdapter!!.notifyDataSetChanged()
            })
        }

        //Experience button
        experience_btn.setOnClickListener {

            val recyclerView: RecyclerView = root.findViewById(R.id.profile_experience_rv)

            experience_btn_cv.setCardBackgroundColor(Color.parseColor("#FF3A63"))
            experience_btn_tv.setTextColor(Color.WHITE)

            interest_btn_cv.setCardBackgroundColor(Color.parseColor("#54CBAF"))
            interest_btn_tv.setTextColor(Color.parseColor("#226880"))

            skill_btn_cv.setCardBackgroundColor(Color.parseColor("#54CBAF"))
            skill_btn_txt.setTextColor(Color.parseColor("#226880"))

            //visibility and animation of the layouts

            ll_experience.visibility = View.VISIBLE
            ll_experience.startAnimation(layout)

            ll_skills.visibility = View.GONE
            ll_skills.startAnimation(toBottom)

            ll_interests.visibility = View.GONE
            ll_interests.startAnimation(toBottom)

            ll_about_profile.visibility = View.GONE
            ll_about_profile.startAnimation(toBottom)

            viewModel.myExpModel!!.observe(viewLifecycleOwner, Observer {
                initView(recyclerView, 2)
                expAdapter_Prof = context?.let { it1 -> ExpAdapter(it1, it) }
                recyclerView.adapter = expAdapter_Prof
                expAdapter_Prof!!.notifyDataSetChanged()
            })

        }

        postBtn_btn_cv.setOnClickListener {

            Navigation.findNavController(root)
                .navigate(R.id.action_myDetailsFragment_to_myPostsFragemt)

        }

        return root

    }

    //Retrieve User Data
    private fun retrieveUserData(root: View) {
        val dataRef = FirebaseDatabase
            .getInstance().reference
            .child("Users")
            .child(
                FirebaseAuth
                    .getInstance()
                    .currentUser!!.uid
            )

        dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val users = snapshot.getValue(UserModel::class.java)
                    from_profile.text = users?.place
                    institutionName.text = users?.college
                    student_course.text= users?.course
                    branch_profile.text = users?.branch
                    year_profile.text = "Year: " + users?.year

                    if (users?.male == true) {
                        genderImage_profile.setImageResource(R.drawable.male)
                        gender_txt.text = "Male"
                        if (users.single) { RS_Image_profile.setImageResource(R.drawable.single_male)
                            relation_txt.text = "Single"
                        } else if (users.committed) {
                            RS_Image_profile.setImageResource(R.drawable.com_male)
                            relation_txt.text = "Committed"
                        } else if (users.crush) {
                            RS_Image_profile.setImageResource(R.drawable.crush_male)
                            relation_txt.text = "Have a Crush"
                        }
                    } else if (users?.female == true) {
                        genderImage_profile.setImageResource(R.drawable.female)
                        gender_txt.text = "Female"
                        if (users.single) {
                            RS_Image_profile.setImageResource(R.drawable.single)
                            relation_txt.text = "Single"
                        } else if (users.committed) {
                            RS_Image_profile.setImageResource(R.drawable.com_female)
                            relation_txt.text = "Committed"
                        } else if (users.crush) {
                            RS_Image_profile.setImageResource(R.drawable.crush_female)
                            relation_txt.text = "Have a Crush"
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    //Global Init function for RecyclerView
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

}