package com.example.instagram.ui.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.instagram.MainActivity
import com.example.instagram.R
import com.example.instagram.databinding.ActivityAddPostBinding
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage

class AddPostActivity : AppCompatActivity() {
    /**
    ** created by shaimaa salama
     */
    private lateinit var binding : ActivityAddPostBinding
    private var myUrl=""
    private var imageUri : Uri? = null
    private var storagePostsRef : StorageReference? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_post)

        storagePostsRef = FirebaseStorage.getInstance().reference.child("Posts Pictures")

        binding.saveNewPostsBtn.setOnClickListener {
            uploadImage()
        }
        CropImage.activity().setAspectRatio(2, 1)
                .start(this)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode== Activity.RESULT_OK && data!=null){
            val result = CropImage.getActivityResult(data)
            imageUri=result.uri
            binding.imagePost.setImageURI(imageUri)
        }
    }
    private fun uploadImage() {
   when{
 TextUtils.isEmpty(binding.descriptionPost.text) -> Toast.makeText(this , "Description should not be Empty!!" , Toast.LENGTH_LONG).show()
  imageUri == null -> Toast.makeText(this, "please selec image ", Toast.LENGTH_LONG).show()
       else -> {

           var progressDialoge: ProgressDialog = ProgressDialog(this)
           progressDialoge.setTitle("Adding new post")
           progressDialoge.setMessage("Please wait ! , we are uploading your post ")
           progressDialoge.show()

           val fileRef = storagePostsRef!!.child(System.currentTimeMillis().toString() + ".jpg")
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

                   val postRef = FirebaseDatabase.getInstance().reference.child("posts")
                   val postrMap = HashMap<String, Any>()
                   val postId = postRef.push().key.toString()

                   postrMap["postId"] = postId
                   postrMap["description"] = binding.descriptionPost.text.toString()
                   postrMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                   postrMap["postImage"] = myUrl.toString()

                   postRef.child(postId).setValue(postrMap)

                   Toast.makeText(this, "post uploaded  Successfully !", Toast.LENGTH_LONG).show()

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
}