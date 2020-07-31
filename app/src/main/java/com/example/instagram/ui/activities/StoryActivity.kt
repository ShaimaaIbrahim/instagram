package com.example.instagram.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.instagram.R
import com.example.instagram.databinding.ActivityStoryBinding
import com.example.instagram.model.Story
import com.example.instagram.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import jp.shts.android.storiesprogressview.StoriesProgressView


class StoryActivity : AppCompatActivity(), StoriesProgressView.StoriesListener {

    private lateinit var binding : ActivityStoryBinding
    var currentUserId : String = ""
    var userId : String = ""
    private var counter  = 0
    private var imagesList : ArrayList<String>? = null
    private var storysId : ArrayList<String>? = null
    private var pressTime = 0L
    private var limitTime =500L

    @SuppressLint("ClickableViewAccessibility")
    private var onTouchListener =  View.OnTouchListener{
        view , motionEvent ->

        when(motionEvent.action){

            MotionEvent.ACTION_DOWN -> {
         pressTime=System.currentTimeMillis()
         binding.storiesProgress.pause()
          return@OnTouchListener false
            }
            MotionEvent.ACTION_UP ->{
                val now =System.currentTimeMillis()
                binding.storiesProgress.resume()
                return@OnTouchListener limitTime < now-pressTime
            }


        }
        false

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       binding = DataBindingUtil.setContentView(this , R.layout.activity_story)

        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid.toString()

         userId = intent.getStringExtra("userId")!!
      //   counter = Math.abs(intent.getIntExtra("counter" , 0)-1)

        binding.layoutSeen.visibility = View.GONE
        binding.storyDelete.visibility= View.GONE

        if (userId==currentUserId){
            binding.layoutSeen.visibility = View.VISIBLE
            binding.storyDelete.visibility= View.VISIBLE
        }

        userInfo(userId)
        getStories(userId)

        /**
         *  click on reverse view
         */
        binding.reverse.setOnClickListener {
            binding.storiesProgress.reverse()
        }

        binding.reverse.setOnTouchListener(onTouchListener)

        /**
         * click on skip view
         */
        binding.skip.setOnClickListener {
            binding.storiesProgress.skip()
        }

        binding.skip.setOnTouchListener(onTouchListener)

        /**
         * click on story number
         */
        binding.seenNumber.setOnClickListener {
            var intent = Intent(this , ShowUsersActivity::class.java)
            intent.putExtra("id" , userId)
            intent.putExtra("title" , "views")
            intent.putExtra("storyId" , storysId!![counter])
            startActivity(intent)
        }

        binding.storyDelete.setOnClickListener {
       val ref=  FirebaseDatabase.getInstance().reference.child("story")
                .child(userId).child(storysId!![counter])

            ref.removeValue().addOnCompleteListener{
                Toast.makeText(this , "Deleting...." ,Toast.LENGTH_LONG ).show()

            }
        }
    }

    private fun addViewToStory(storyId: String){

       FirebaseDatabase.getInstance().reference.child("story")
                .child(userId).child(storyId).child("views")
                .child(currentUserId)
                .setValue(true)

    }

    private fun getStories(userId: String){

        imagesList= ArrayList()
        storysId = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("story")
            .child(userId)

        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                imagesList!!.clear()
                storysId!!.clear()

                if (snapshot.exists()) {

                    for (sh in snapshot.children) {
                        var story: Story = sh.getValue(Story::class.java)!!
                        val timeCurrent = System.currentTimeMillis()

                        if (timeCurrent > story.getTimeStar()!! && timeCurrent < story.getTimeEnd()!!) {

                            imagesList!!.add(story.getImageUrl()!!)
                            story.getStoryId()?.let { storysId!!.add(it) }

                        }

                    }

                    binding.storiesProgress.setStoriesCount(imagesList!!.size)
                    binding.storiesProgress.setStoryDuration(6000L)
                    binding.storiesProgress.setStoriesListener(this@StoryActivity)
                    binding.storiesProgress.startStories()

                    Picasso.get().load(imagesList!!.get(counter)).into(binding.storyImage)

                    addViewToStory(storysId!!.get(counter))
                    seenNumber(storysId!!.get(counter))
                }
            }
        })
    }

    private fun seenNumber(storyId : String){

      val ref = FirebaseDatabase.getInstance().reference.child("story")
          .child(userId).child(storyId).child("views")

        ref.addListenerForSingleValueEvent(object  : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
              binding.seenNumber.text = "" + snapshot.childrenCount
            }

        })

    }

    private fun userInfo( userId : String){

        val userRef = FirebaseDatabase.getInstance().reference.child("users")
            .child(userId)

        userRef.addValueEventListener(object  : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    val user : User = snapshot.getValue(User::class.java)!!

                    Picasso.get().load(user.image).placeholder(R.drawable.profile).into(binding.storyProfileImage)
                    binding.storyUsername.text = user.userName

                }

            }

        })
    }

    override fun onComplete() {
       finish()
    }

    override fun onPrev() {
        if (counter -1 < 0) return

        Picasso.get().load(imagesList!![--counter]).into(binding.storyImage)
        seenNumber(storysId!![counter])
    }

    override fun onNext() {

        if (counter +1 > imagesList!!.size) return

        Picasso.get().load(imagesList!![++counter]).into(binding.storyImage)

        addViewToStory(storysId!![counter])
        seenNumber(storysId!![counter])
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.storiesProgress.destroy()
    }

    override fun onResume() {
        super.onResume()
        binding.storiesProgress.resume()

    }

    override fun onPause() {
        super.onPause()
      binding.storiesProgress.pause()
    }
}









