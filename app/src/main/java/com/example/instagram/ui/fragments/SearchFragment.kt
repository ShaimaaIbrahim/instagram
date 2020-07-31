package com.example.instagram.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.example.instagram.databinding.FragmentSearchBinding
import com.example.instagram.model.User
import com.example.instagram.model.viewModels.MonawatViewModel
import com.example.instagram.model.viewModels.UsersSearchViewModel
import com.example.instagram.ui.activities.MonawaatActivity
import com.example.instagram.ui.activities.VideosActivity
import com.example.instagram.ui.adapter.MonawatAdapter
import com.example.instagram.ui.adapter.UserAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SearchFragment : Fragment() {

    private lateinit var binding : FragmentSearchBinding
    private lateinit var viewModel : UsersSearchViewModel
    private lateinit var viewModel1: MonawatViewModel
    private  val listUsers : ArrayList<User> = ArrayList()
    private  var listMedia : ArrayList<String> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment

        viewModel = ViewModelProviders.of(this).get(UsersSearchViewModel::class.java)
        binding=  FragmentSearchBinding.inflate(inflater)
        viewModel1=ViewModelProviders.of(this).get(MonawatViewModel::class.java)

        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        binding.recyclerViewSearch.adapter = UserAdapter(context!! , true)



        binding.horizontalScrollView.visibility=View.VISIBLE
        binding.recyclerViewMedia.visibility= View.VISIBLE


        binding.recyclerViewSearch.visibility= View.GONE

        binding.setLifecycleOwner(this)
        binding.viewModel1=viewModel1
        binding.recyclerViewMedia.adapter = MonawatAdapter(context!!, listMedia)

        getFoods()

        binding.searchEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                binding.horizontalScrollView.visibility=View.GONE
                binding.recyclerViewMedia.visibility= View.GONE
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                binding.horizontalScrollView.visibility = View.GONE
                binding.recyclerViewMedia.visibility = View.GONE
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.horizontalScrollView.visibility=View.GONE
                binding.recyclerViewMedia.visibility= View.GONE

                if (binding.searchEditText.text.toString()==""){

              }else{

                   binding.recyclerViewSearch.visibility= View.VISIBLE


                  rerieveUsers()
                  searchUser(p0.toString().toLowerCase())


              }
            }
        })

        binding.foodsBtn.setOnClickListener {
            var intent = Intent(context!! , MonawaatActivity::class.java)
            intent.putExtra("title" , "Foods")
            context!!.startActivity(intent)
        }

        binding.musicBtn.setOnClickListener {
            var intent = Intent(context!! , VideosActivity::class.java)
            intent.putExtra("title" , "Music")
            context!!.startActivity(intent)
        }

        binding.tvBtn.setOnClickListener {
            var intent = Intent(context!! , MonawaatActivity::class.java)
            intent.putExtra("title" , "Tv")
            context!!.startActivity(intent)
        }

        binding.travellingBtn.setOnClickListener {
            var intent = Intent(context!! , MonawaatActivity::class.java)
            intent.putExtra("title" , "Travelling")
            context!!.startActivity(intent)
        }

        binding.sportsBtn.setOnClickListener {
            var intent = Intent(context!! , MonawaatActivity::class.java)
            intent.putExtra("title" , "Sports")
            context!!.startActivity(intent)
        }

        return binding.root
    }


    private fun searchUser(input : String) {
        val query = FirebaseDatabase.getInstance().reference.child("users")
            .orderByChild("fullName")
            .startAt(input).endAt(input + "\uf8ff")
        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }
            override fun onDataChange(snapshot: DataSnapshot) {

                    listUsers.clear()
                    for (snap in snapshot.children){
                        val user = snap.getValue(User::class.java)
                        listUsers.add(user!!)
                       Log.e("shaimaa" , user.email)
                    }

                     viewModel._searchUserList.value = listUsers.toList()

            } })
    }


    private fun rerieveUsers() {
        val userRef = FirebaseDatabase.getInstance().reference.child("users")
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }
            override fun onDataChange(snapshot: DataSnapshot) {
                listUsers.clear()
              if (binding.searchEditText.text.toString()==""){
                    for (snap in snapshot.children){
                        val user = snap.getValue(User::class.java)
                        listUsers.add(user!!)
                        Log.e("shaimaa" , user.email)
                    }

                      viewModel._searchUserList.value = listUsers.toList()

            } }
            })
    }

    private fun getFoods(){

           listMedia = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("foods")

        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                listMedia.clear()

                for (sh in snapshot.children){

                    listMedia.add(sh.getValue(String::class.java)!!)

                }

            } })
    }
}