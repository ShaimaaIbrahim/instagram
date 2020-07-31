package com.example.instagram.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.instagram.R
import com.example.instagram.databinding.ActivityCommentsBinding
import com.example.instagram.model.Comment
import com.example.instagram.model.Post
import com.example.instagram.model.User
import com.example.instagram.model.viewModels.CommentActViewModel
import com.example.instagram.ui.adapter.CommentsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class CommentsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCommentsBinding
    private lateinit var postId : String
    private lateinit var publisherId : String
    private var firebaseUser  = FirebaseAuth.getInstance().currentUser!!
    private lateinit var viewModel : CommentActViewModel
    private lateinit var commentList : ArrayList<Comment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this , R.layout.activity_comments)
        viewModel= ViewModelProviders.of(this).get(CommentActViewModel::class.java)


        var intent = intent
        postId = intent.getStringExtra("postId")!!
        publisherId = intent.getStringExtra("publisherId")!!

        userInfo()
        binding.postComment.setOnClickListener {

            if (binding.addComment.text.toString() ==""){
               Toast.makeText(this , " you can not publish empty comment", Toast.LENGTH_LONG).show()
            }else{
                addComment()
            } }

        binding.setLifecycleOwner(this)
        binding.viewModel= viewModel
        binding.recyclerViewComments.adapter = CommentsAdapter(this)

        getPostImage()
        readComments()

    }

    private fun addComment() {
        val commentRef = FirebaseDatabase.getInstance().reference.child("Comments")
            .child(postId)
        val commentMap= HashMap<String , Any>()
            commentMap["comment"] = binding.addComment.text.toString()
            commentMap["publisher"] = firebaseUser.uid

        commentRef.push().setValue(commentMap)

        addNotification()

        binding.addComment.text.clear()

    }

    private fun userInfo(){

        val userRef = FirebaseDatabase.getInstance().reference.child("users")
            .child(firebaseUser.uid.toString())

        userRef.addValueEventListener(object  : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user : User = snapshot.getValue(User::class.java)!!


                    Picasso.get().load(user.image).placeholder(R.drawable.profile).into(binding.profileImageComment)

                } } }) }

    fun readComments(){
        commentList = ArrayList()
        val commentRef = FirebaseDatabase.getInstance().reference.
        child("Comments").child(postId)

        commentRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()){
                commentList.clear()
                for (sh in snapshot.children){
                    val comment  = sh.getValue(Comment::class.java)
                    commentList.add(comment!!)
                }
                viewModel._commentList.value = commentList.toList()
            }
            }
        })

    }

    private fun getPostImage(){

        val postRef = FirebaseDatabase.getInstance().reference.child("posts")
                .child(postId).child("postImage")

        postRef.addValueEventListener(object  : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val postImage : String = snapshot.value.toString()


                    Picasso.get().load(postImage).placeholder(R.drawable.profile).into(binding.postImageComment)

                } } }) }

    private fun addNotification(){

        val notificationsRef = FirebaseDatabase.getInstance().reference.child("notifications")
                .child(publisherId)

        val notificationMap = HashMap<String , Any>()
        notificationMap.put("userId" , firebaseUser.uid!!)
        notificationMap.put("postId" , postId)
        notificationMap.put("text" , "Commented : ${binding.addComment.text.toString()}")
        notificationMap.put("isPost" , true)

        notificationsRef.push().setValue(notificationMap)

    }
}