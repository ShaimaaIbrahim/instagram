package com.example.instagram.ui.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.R
import com.example.instagram.databinding.AddStoryItemBinding
import com.example.instagram.databinding.StoryLayoutBinding
import com.example.instagram.model.Story
import com.example.instagram.model.User
import com.example.instagram.ui.activities.AddStoryActivity
import com.example.instagram.ui.activities.StoryActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class StoryAdapter  (private var context: Context ) : ListAdapter<Story , RecyclerView.ViewHolder >(DiffCallback){
    private val ITEM_VIEW_TYPE_HEADER = 0
    private val ITEM_VIEW_TYPE_ITEM = 1

    class viewHolder(var binding : StoryLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(story: Story){

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

      return when(viewType){

    ITEM_VIEW_TYPE_HEADER -> ItemHeaderViewHolder(AddStoryItemBinding.inflate(LayoutInflater.from(parent.context)))
    ITEM_VIEW_TYPE_ITEM-> viewHolder(StoryLayoutBinding.inflate(LayoutInflater.from(parent.context)))
    else ->   throw ClassCastException("Unknown viewType ${viewType}")
      }
    }

    private class ItemHeaderViewHolder(var binding: AddStoryItemBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val  story = getItem(position)

        when(holder){


            is viewHolder -> {

                holder.bind(story)
                seenStory(holder , story.getUserId()!!)

                holder.itemView.setOnClickListener {

                    var intent = Intent(context , StoryActivity::class.java)
                    intent.putExtra("userId" , story.getUserId())

                    Toast.makeText(context , story.getUserId() , Toast.LENGTH_LONG).show()

                    intent.putExtra("counter" , position)
                    context.startActivity(intent)

                    seenStory(holder , story.getUserId()!!)
                }
                userInfo(holder ,  story.getUserId().toString() , position)


            }

            is ItemHeaderViewHolder -> {

                val imageUser = holder.itemView.findViewById<CircleImageView>(R.id.story_image_seen)
                val nameUser = holder.itemView.findViewById<TextView>(R.id.add_stoty_text)


                holder.itemView.setOnClickListener {

                    myStories(nameUser , imageUser , true)

                }
                val imageAdd = holder.itemView.findViewById<ImageView>(R.id.story_add)

                myStories(nameUser , imageAdd , false)
                userInfo(holder,  FirebaseAuth.getInstance().currentUser!!.uid , position)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
     return when(position){
         0 -> ITEM_VIEW_TYPE_HEADER
         else -> ITEM_VIEW_TYPE_ITEM
     }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Story>() {
        override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem.getStoryId() == newItem.getStoryId()
        }
    }

    private fun userInfo( holder: RecyclerView.ViewHolder , userId : String  , position : Int){

        val userRef = FirebaseDatabase.getInstance().reference.child("users")
            .child(userId)

        userRef.addListenerForSingleValueEvent(object  : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    val user : User = snapshot.getValue(User::class.java)!!

                    if (position==0){
                        val imageView = holder.itemView.findViewById<ImageView>(R.id.story_image_seen)
                        Picasso.get().load(user.image).placeholder(R.drawable.profile).into(imageView)
                    }else{
                        val imageUserSeen = holder.itemView.findViewById<CircleImageView>(R.id.story_image_seen)
                        val imageUser = holder.itemView.findViewById<CircleImageView>(R.id.story_image)
                        val nameUser = holder.itemView.findViewById<TextView>(R.id.story_username)

                        nameUser.text = user.userName
                        Picasso.get().load(user.image).placeholder(R.drawable.profile).into(imageUserSeen)
                        Picasso.get().load(user.image).placeholder(R.drawable.profile).into(imageUser)


                    }



                }

            }

        })
    }

    private fun seenStory(viewHolder : RecyclerView.ViewHolder , userId : String){

        val storyRef = FirebaseDatabase.getInstance().reference.child("story")
                .child(userId)

        storyRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                 var i =0
                for (sh in snapshot.children){

                    if (!sh.child("views").child(FirebaseAuth.getInstance().currentUser!!.uid).exists()
                            && System.currentTimeMillis() < sh.getValue(Story::class.java)!!.getTimeEnd()!! &&
                            System.currentTimeMillis() > sh.getValue(Story::class.java)!!.getTimeStar()!!){
                        ++i

                    }
                }
                if(i>0){
                    viewHolder.itemView.findViewById<CircleImageView>(R.id.story_image).visibility= android.view.View.VISIBLE
                    viewHolder.itemView.findViewById<CircleImageView>(R.id.story_image_seen).visibility= android.view.View.GONE

                }else{
                    viewHolder.itemView.findViewById<CircleImageView>(R.id.story_image).visibility= android.view.View.GONE
                    viewHolder.itemView.findViewById<CircleImageView>(R.id.story_image_seen).visibility= android.view.View.VISIBLE
                }
            }

        })

    }

    private fun myStories(textView: TextView , imageView: ImageView , click : Boolean){
        val storyRef = FirebaseDatabase.getInstance().reference.child("story")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)

        storyRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
              var counter =0
              var currentTime = System.currentTimeMillis()

                for (sh in snapshot.children){
                    var story= sh.getValue(Story::class.java)

                    if (currentTime> story!!.getTimeStar()!! &&  currentTime<story!!.getTimeEnd()!!){

                        ++counter
                    }
                }
                if (click){

                    if (counter>0){

                        val alertDialog = AlertDialog.Builder(context).create()

                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL , "view story"){
                            dialogInterface, i ->
                            val intent = Intent(context ,  StoryActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.putExtra("userId" , FirebaseAuth.getInstance().currentUser!!.uid)
                            context.startActivity(intent)
                             dialogInterface.dismiss()
                        }

                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE , "Add story"){
                            dialogInterface, i ->
                            val intent = Intent(context ,  AddStoryActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.putExtra("userId" , FirebaseAuth.getInstance().currentUser!!.uid)
                            context.startActivity(intent)
                            dialogInterface.dismiss()
                        }
                        alertDialog.show()
                    }
                    else{
                        val intent = Intent(context ,  AddStoryActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.putExtra("userId" , FirebaseAuth.getInstance().currentUser!!.uid)
                        context.startActivity(intent)
                    }

                }else{

                    if (counter>0){

                        textView.text = "My Story"
                        imageView.visibility=android.view.View.GONE

                    }else{
                        textView.text = "Add Story"
                        imageView.visibility=android.view.View.VISIBLE
                    }
                }
            }
        })
    }

}