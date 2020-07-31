package com.example.instagram.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.instagram.MainActivity
import com.example.instagram.R
import com.example.instagram.databinding.ActivityVideosBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kohii.v1.exoplayer.Kohii

class VideosActivity : AppCompatActivity() {

    private lateinit var binding : ActivityVideosBinding
    private lateinit var mediaList : ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

      binding = DataBindingUtil.setContentView(this , R.layout.activity_videos)

        val intent = intent

        val title = intent.getStringExtra("title")

        setSupportActionBar(binding.toolBar)
        supportActionBar!!.title = title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.toolBar.setNavigationOnClickListener {
            startActivity(Intent(this , MainActivity::class.java))
        }
        if (title=="Music"){
            getMusic()
        }


        val kohii = Kohii[this]
        val manager = kohii.register(this)
            .addBucket(binding.container)

    }

    private fun getMusic(){

        mediaList = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("music")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                mediaList.clear()

                for (sh in snapshot.children){
                    mediaList.add(sh.getValue(String::class.java)!!.toString())
                }

            }

        })
    }

}