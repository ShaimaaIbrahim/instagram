package com.example.instagram.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.MainActivity
import com.example.instagram.R
import com.example.instagram.databinding.UserItemLayoutBinding
import com.example.instagram.model.User
import com.example.instagram.ui.fragments.ProfileFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserAdapter(private var context: Context , private val isFragment : Boolean =false) : ListAdapter<User , UserAdapter.UserAdapterViewHolder>(DiffCallback) {

    private var firebaseUser : FirebaseUser ? = FirebaseAuth.getInstance().currentUser

    class UserAdapterViewHolder(private var binding : UserItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User ) {
            binding.user = user
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapterViewHolder {
        return UserAdapterViewHolder(UserItemLayoutBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: UserAdapterViewHolder, position: Int) {
       val user = getItem(position)
        holder.bind(user)
        
      val follow_btn = holder.itemView.findViewById<Button>(R.id.follow_btn_search)
        /**
         * when click on follow button 
         */
        checkFollowingState(user.uid , follow_btn)

        holder.itemView.setOnClickListener {
            if (isFragment){
                val pref = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                pref.putString("profileId" , user.uid)
                pref.apply()

                (context as FragmentActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container , ProfileFragment()).commit()
            }else{
                var intent = Intent(context , MainActivity::class.java)
                intent.putExtra("publisherId" , user.uid)
                context.startActivity(intent)
            }

        }

        follow_btn.setOnClickListener {
            if (follow_btn.text=="Follow"){

               firebaseUser!!.uid.let {
                   FirebaseDatabase.getInstance().reference.child("Follow")
                       .child(it.toString()).child("Following").child(user.uid)
                       .setValue(true).addOnCompleteListener { task ->
                           if (task.isSuccessful){

                               firebaseUser!!.uid.let {
                                   FirebaseDatabase.getInstance().reference.child("Follow")
                                       .child(user.uid).child("Followers").child(it.toString())
                                       .setValue(true).addOnCompleteListener { task ->
                                           if (task.isSuccessful) {

                                           }
                                       }}
                       }
                       }
               }
                addNotification(user.uid)
            }else{
                firebaseUser!!.uid.let {
                    FirebaseDatabase.getInstance().reference.child("Follow")
                        .child(it.toString()).child("Following").child(user.uid)
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful){

                                firebaseUser!!.uid.let {
                                    FirebaseDatabase.getInstance().reference.child("Follow")
                                        .child(user.uid).child("Followers").child(it.toString())
                                        .removeValue().addOnCompleteListener { task ->
                                            if (task.isSuccessful) {

                                            }
                                        }}
                            }
                        }
                }
            }
        }
    }

    private fun checkFollowingState(uid: String, followBtn: Button?) {
     val followingReference=   firebaseUser!!.uid.let {
            FirebaseDatabase.getInstance().reference.child("Follow")
                .child(it.toString()).child("Following")
        }
        followingReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
               if (snapshot.child(uid).exists()){
                   followBtn!!.text= "Following"
               }else{
                   followBtn!!.text= "Follow"
               }
            }

        })
    }


    companion object DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }
    }

    private fun addNotification(userId : String ){

        val notificationsRef = FirebaseDatabase.getInstance().reference.child("notifications")
                .child(userId)

        val notificationMap = HashMap<String , Any>()
        notificationMap.put("userId" , firebaseUser!!.uid)
        notificationMap.put("postId" , "")
        notificationMap.put("text" , " started following you!")
        notificationMap.put("isPost" , false)

        notificationsRef.push().setValue(notificationMap)

    }
}
