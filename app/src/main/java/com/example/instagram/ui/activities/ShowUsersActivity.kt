package com.example.instagram.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.instagram.R
import com.example.instagram.databinding.ActivityShowUsersBinding
import com.example.instagram.databinding.UserItemLayoutBinding
import com.example.instagram.model.User
import com.example.instagram.model.viewModels.UsersSearchViewModel
import com.example.instagram.ui.adapter.UserAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ShowUsersActivity : AppCompatActivity() {

    private lateinit var binding : ActivityShowUsersBinding
    private lateinit var id : String
    private lateinit var title : String
    private lateinit var usersList : ArrayList<User>
    private lateinit var idList : ArrayList<String>
    private lateinit var viewModel : UsersSearchViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = DataBindingUtil.setContentView(this , R.layout.activity_show_users)

  val intent = intent
        id = intent.getStringExtra("id")!!
        title = intent.getStringExtra("title")!!

          setSupportActionBar(binding.toolBar)
          supportActionBar!!.setTitle(title)
         supportActionBar!!.setDisplayHomeAsUpEnabled(true)
         binding.toolBar.setNavigationOnClickListener {
               finish()
         }

        viewModel = ViewModelProviders.of(this).get(UsersSearchViewModel::class.java)

        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        binding.recyclerView.adapter = UserAdapter(this , false)

        idList= ArrayList()
        usersList = ArrayList()

        when(title){

             "likes" -> getLikes()
             "following" ->  getFollowing()
             "followers" -> getFollowers()
             "views" -> getViews()
        }
    }

    private fun getViews() {

        val ref = FirebaseDatabase.getInstance().reference.child("story")
            .child(id).child(intent.getStringExtra("storyId")!!).child("views")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    idList.clear()
                    for (sh in snapshot.children) {
                        idList.add(sh.key!!)
                    }
                    showUsers()

                }
            }
        })
    }

    private fun getFollowers() {
        val followersReference = FirebaseDatabase.getInstance().reference.
        child("Follow")
                .child(id).child("Followers")

        followersReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    idList.clear()
                    for (sh in snapshot.children){
                        idList.add(sh.key!!)
                    }
                    showUsers()

                }
            }
        })
    }

    private fun getFollowing() {

        val followingReference =  FirebaseDatabase.getInstance().reference.
        child("Follow")
                .child(id).child("Following")

        followingReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    idList.clear()
                    for (sh in snapshot.children){
                        idList.add(sh.key!!)
                    }
                    showUsers()

                }
            }
        })
    }


    private fun getLikes() {

        val likesRref = FirebaseDatabase.getInstance().reference.child("Likes")
                .child(id)
        likesRref.addValueEventListener(object  : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    idList.clear()
                   for (sh in snapshot.children){
                       idList.add(sh.key!!)
                   }
                    showUsers()

                }
            } })
    }

    private fun showUsers() {
        val userRef = FirebaseDatabase.getInstance().reference.child("users")
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }
            override fun onDataChange(snapshot: DataSnapshot) {
                usersList.clear()
                for (sh in snapshot.children){
                    val user = sh.getValue(User::class.java)

                    for(id in idList){
                        if (user!!.uid == id){
                            usersList.add(user!!)
                        }
                    }
                }
             viewModel._searchUserList.value = usersList
                }
              })
    }
}