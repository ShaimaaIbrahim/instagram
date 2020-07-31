package com.example.instagram.ui.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.instagram.MainActivity
import com.example.instagram.R
import com.example.instagram.databinding.ActivityAddStoryBinding
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage

class AddStoryActivity : AppCompatActivity() {

    private var myUrl=""
    private var imageUri : Uri? = null
    private var storageStoryRef : StorageReference? =null
    private lateinit var binding : ActivityAddStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_story)

        storageStoryRef = FirebaseStorage.getInstance().reference.child("Story Pictures")


        CropImage.activity().setAspectRatio(9, 16)
                .start(this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode== Activity.RESULT_OK && data!=null){
            val result = CropImage.getActivityResult(data)
            imageUri=result.uri
         uploadImage()
        }
    }

    private fun uploadImage() {
        when{
            imageUri == null -> Toast.makeText(this, "please selec image ", Toast.LENGTH_LONG).show()
            else -> {

                var progressDialoge: ProgressDialog = ProgressDialog(this)
                progressDialoge.setTitle("Adding Story")
                progressDialoge.setMessage("Please wait ! , we are uploading your story ")
                progressDialoge.show()

                val fileRef = storageStoryRef!!.child(System.currentTimeMillis().toString() + ".jpg")
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

                        val storyRef = FirebaseDatabase.getInstance().reference.child("story")
                        val storyMap = HashMap<String, Any>()
                        val storyId = storyRef.push().key.toString()
                        val timeEnd = System.currentTimeMillis() + 86400000


                        storyMap["storyId"] = storyId
                        storyMap["timeEnd"] = timeEnd
                        storyMap["timeStar"] = ServerValue.TIMESTAMP

                        Log.e("tata" , ServerValue.TIMESTAMP.toString())

                        storyMap["imageUrl"] = myUrl
                        storyMap["userId"]= FirebaseAuth.getInstance().currentUser!!.uid

                        storyRef.child(FirebaseAuth.getInstance().currentUser!!.uid).child(storyId).setValue(storyMap)

                        Toast.makeText(this, "story has been  uploaded  Successfully !", Toast.LENGTH_LONG).show()

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