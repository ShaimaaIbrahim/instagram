package com.example.instagram.ui.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.instagram.MainActivity
import com.example.instagram.R
import com.example.instagram.databinding.ActivityAccountSettingsBinding
import com.example.instagram.model.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage


class AccountSettingsActivity : AppCompatActivity() {

    /**
    ** created by shaimaa salama
     */
    private lateinit var binding : ActivityAccountSettingsBinding
    private lateinit var firebaseUser: FirebaseUser
    private var checked =""
    private var myUrl=""
    private var imageUri : Uri? = null
    private var storageProfilePicRef : StorageReference ? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef= FirebaseStorage.getInstance().reference.child("Profile Pictures")

        binding = DataBindingUtil.setContentView(this , R.layout.activity_account_settings)
        /**
        * set user info
         */
        userInfo()
        binding.logoutBtn.setOnClickListener {

            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this , SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        binding.changeImgTextBtn.setOnClickListener {
            CropImage.activity().setAspectRatio(1 , 1 )
                .start(this)
            checked = "clicked"
        }

        binding.saveProfileInfoBtn.setOnClickListener {
            if (checked=="clicked"){
         uploadImageAndUploadInfo()
            }else{
       updateUserInfoOnly()
            }
        }
    }

    private fun uploadImageAndUploadInfo() {

        when {
            TextUtils.isEmpty(binding.fullNameProfileFrag.text) -> binding.fullNameProfileFrag.setError("field is empty")
            TextUtils.isEmpty(binding.userNameProfileFrag.text) -> binding.userNameProfileFrag.setError("field is empty")
            TextUtils.isEmpty(binding.BioProfileFrag.text) -> binding.BioProfileFrag.setError("field is empty")
            imageUri == null -> Toast.makeText(this, "please selec image ", Toast.LENGTH_LONG).show()
            else -> {

                var progressDialoge: ProgressDialog = ProgressDialog(this)
                progressDialoge.setTitle("Account Setting")
                progressDialoge.setMessage("Please wait ! , we are updating your profile")
                progressDialoge.show()

                val fileRef = storageProfilePicRef!!.child(firebaseUser.uid + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception.let {
                            throw it!!
                            progressDialoge.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener(OnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val userRef = FirebaseDatabase.getInstance().reference.child("users")
                        val userMap = HashMap<String, Any>()
                        userMap["fullName"] = binding.fullNameProfileFrag.text.toString().toLowerCase()
                        userMap["userName"] = binding.userNameProfileFrag.text.toString().toLowerCase()
                        userMap["bio"] = binding.BioProfileFrag.text.toString().toLowerCase()
                        userMap["image"] = myUrl.toString()

                        userRef.child(firebaseUser.uid).updateChildren(userMap)

                        Toast.makeText(this, "Account Information updated Successfully !", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                        progressDialoge.dismiss()
                    }else{
                        progressDialoge.dismiss()
                    }
                })
            }

        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode== Activity.RESULT_OK && data!=null){
            val result = CropImage.getActivityResult(data)
         imageUri=result.uri
            binding.profileImageViewProfileFrag.setImageURI(imageUri)
        }

    }
    private fun updateUserInfoOnly() {

        if (TextUtils.isEmpty(binding.fullNameProfileFrag.text) ){
            binding.fullNameProfileFrag.setError("field is empty")
        }
       else if (TextUtils.isEmpty(binding.userNameProfileFrag.text)){
            binding.userNameProfileFrag.setError("field is empty")
        }
        else if (TextUtils.isEmpty(binding.BioProfileFrag.text)){
            binding.BioProfileFrag.setError("field is empty")
        }
        else{
            val userRef : DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")

            val userMap = HashMap<String , Any>()
            userMap["fullName"] = binding.fullNameProfileFrag.text.toString().toLowerCase()
            userMap["userName"] = binding.userNameProfileFrag.text.toString().toLowerCase()
            userMap["bio"] = binding.BioProfileFrag.text.toString().toLowerCase()

            userRef.child(firebaseUser.uid).updateChildren(userMap)

            Toast.makeText(this , "Account Information updated Successfully !" , Toast.LENGTH_LONG).show()
            val intent = Intent(this , MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }


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

                      binding.fullNameProfileFrag.setText(user.fullName)
                      binding.BioProfileFrag.setText(user.bio)
                      binding.userNameProfileFrag.setText(user.userName)

                      Picasso.get().load(user.image).placeholder(R.drawable.profile).into(binding.profileImageViewProfileFrag)

                } } }) }

}