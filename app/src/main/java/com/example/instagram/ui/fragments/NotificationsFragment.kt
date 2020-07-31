package com.example.instagram.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.instagram.databinding.FragmentNotificationsBinding
import com.example.instagram.model.Notification
import com.example.instagram.model.viewModels.NotificationViewModel
import com.example.instagram.ui.adapter.NotificationAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList


class NotificationsFragment : Fragment() {

private lateinit var binding : FragmentNotificationsBinding
private lateinit var viewModel : NotificationViewModel
private lateinit var firebaseUser: FirebaseUser
private var arrayList : ArrayList<Notification> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentNotificationsBinding.inflate(inflater)
        viewModel = ViewModelProviders.of(this).get(NotificationViewModel::class.java)
        firebaseUser = FirebaseAuth.getInstance().currentUser!!


        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        binding.recyclerViewNotification.adapter = NotificationAdapter(context!!)

        readNotifications()

        return binding.root
    }

    private fun readNotifications() {

        val notificationsRef = FirebaseDatabase.getInstance().reference.child("notifications")
                .child(firebaseUser.uid)

        notificationsRef.addValueEventListener(object : ValueEventListener{

            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                   (arrayList as ArrayList<Notification>) .clear()

                   for (sh in snapshot.children){
                       val notification = sh.getValue(Notification::class.java)

                       Log.e("insta" , notification!!.getIsPost().toString())

                      arrayList.add(notification!!)

                   }
                   Collections.reverse(arrayList)

                   viewModel._notificationsList.value= arrayList.toList()

               } })
    }


}