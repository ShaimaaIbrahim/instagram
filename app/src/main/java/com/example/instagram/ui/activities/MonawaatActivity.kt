package com.example.instagram.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.instagram.MainActivity
import com.example.instagram.R
import com.example.instagram.databinding.ActivityMonawaatBinding
import com.example.instagram.model.viewModels.MonawatViewModel
import com.example.instagram.ui.adapter.MonawatAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MonawaatActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMonawaatBinding
    private lateinit var viewModel: MonawatViewModel
    private var mediaList  : ArrayList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= DataBindingUtil.setContentView(this , R.layout.activity_monawaat)

         viewModel = ViewModelProviders.of(this).get(MonawatViewModel::class.java)

        val intent = intent

        val title = intent.getStringExtra("title")

        setSupportActionBar(binding.toolBar)
        supportActionBar!!.title = title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.toolBar.setNavigationOnClickListener {
            startActivity(Intent(this , MainActivity::class.java))
        }

        if (title=="Foods"){
            getFoods()
        }
        else if (title=="Sports"){
            getSports()
        }
        else if (title=="Travelling"){
            getTravelling()
        }
        else if (title=="Tv"){
            getTv()
        }
        else{
      //      getMusic()
        }

        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        binding.recyclerViewMonawat.adapter = MonawatAdapter(this , mediaList)
        binding.recyclerViewMonawat.setHasFixedSize(true)

    }

    private fun getSports(){

        viewModel._monawatList.value= null
        val ref = FirebaseDatabase.getInstance().reference.child("sports")

        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
             mediaList.clear()

                for (sh in snapshot.children){
                    mediaList.add(sh.getValue(String::class.java)!!.toString())
                    Log.e("sh" , sh.getValue(String::class.java).toString())
                }
                viewModel._monawatList.value = mediaList
                binding.progressBar.visibility=View.GONE
            }

        })
    }
    private fun getTravelling(){
        viewModel._monawatList.value= null
        val ref = FirebaseDatabase.getInstance().reference.child("travelling")

        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                mediaList.clear()

                for (sh in snapshot.children){
                    mediaList.add(sh.getValue(String::class.java)!!.toString())
                }
                viewModel._monawatList.value = mediaList
                binding.progressBar.visibility=View.GONE
            }

        })
    }
    private fun getFoods(){
        viewModel._monawatList.value= null
        val ref = FirebaseDatabase.getInstance().reference.child("foods")

        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                mediaList.clear()

                for (sh in snapshot.children){
                    mediaList.add(sh.getValue(String::class.java)!!.toString())
                }
                viewModel._monawatList.value = mediaList
                binding.progressBar.visibility=View.GONE
            }

        })
    }

    private fun getTv(){

        viewModel._monawatList.value= null
        val ref = FirebaseDatabase.getInstance().reference.child("tv")

        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                mediaList.clear()

                for (sh in snapshot.children){
                    mediaList.add(sh.getValue(String::class.java)!!.toString())
                }
                viewModel._monawatList.value = mediaList
                binding.progressBar.visibility=View.GONE
            }

        })
    }
}











