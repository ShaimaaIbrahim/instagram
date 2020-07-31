package com.example.instagram.ui.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.instagram.MainActivity
import com.example.instagram.R
import com.example.instagram.databinding.ActivitySignUpBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignUpActivity : AppCompatActivity() {
    /**
     *created by shaimaa salama
     */
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var   progressDialoge : ProgressDialog
    private  lateinit var  mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this , R.layout.activity_sign_up)

        /**
         * start logIn activity
         */
        binding.signLinkBtn.setOnClickListener {
            startActivity(Intent(this , SignInActivity::class.java))
        }
        /**
         * click to create new account
         */
        binding.signupBtn.setOnClickListener {
            createAccount()
        }
    }

    private fun createAccount() {
      val fullName = binding.fullnameSignup.text.toString()
      val userName = binding.usernameSignup.text.toString()
      val email = binding.emailSignup.text.toString()
      val password = binding.passwordSignup.text.toString()

        when{
            TextUtils.isEmpty(fullName) -> binding.fullnameSignup.setError("Field is Empty")
            TextUtils.isEmpty(userName) -> binding.usernameSignup.setError("Field is Empty")
            TextUtils.isEmpty(email) -> binding.emailSignup.setError("Field is Empty")
            TextUtils.isEmpty(password) -> binding.passwordSignup.setError("Field is Empty")
            else -> {
                progressDialoge = ProgressDialog(this)
                progressDialoge.setTitle("sign up")
                progressDialoge.setMessage("please wait , this message may take a while")
                progressDialoge.setCanceledOnTouchOutside(false)
                progressDialoge.show()

                mAuth = FirebaseAuth.getInstance()
                mAuth.createUserWithEmailAndPassword(email , password).addOnCompleteListener(
                    OnCompleteListener {
                     if(it.isSuccessful){
                    saveUserInfo(fullName , userName , email , password)

                     }else{
                         mAuth.signOut()
                         progressDialoge.dismiss()
                         Toast.makeText(this , it.exception.toString() , Toast.LENGTH_LONG).show()
                     }
                    })
            }
        }

    }

    private fun saveUserInfo(fullName: String, userName: String, email: String, password: String) {
      val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
      val userRef : DatabaseReference= FirebaseDatabase.getInstance().reference.child("users")

        val userMap = HashMap<String , Any>()
         userMap["uid"] = currentUserId
         userMap["fullName"] = fullName.toLowerCase()
         userMap["userName"] = userName.toLowerCase()
         userMap["bio"] = "hey i am using shaimaa instagram app"
         userMap["email"] = email
         userMap["password"] = password
         userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/socialmediaapp-733da.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=02ef7ba9-bf8d-44da-b0f6-f578552419d0"

        userRef.child(currentUserId).setValue(userMap).addOnCompleteListener {
            task ->
            if (task.isSuccessful){
                progressDialoge.dismiss()
                Toast.makeText(this , "Account created Successfully !" , Toast.LENGTH_LONG).show()

                FirebaseDatabase.getInstance().reference.child("Follow")
                        .child(currentUserId).child("Following").child(currentUserId)
                        .setValue(true)


                val intent = Intent(this , MainActivity::class.java)
                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                 startActivity(intent)
                 finish()
            }else{
                mAuth.signOut()
                progressDialoge.dismiss()
                val message = task.exception
                Toast.makeText(this , "Error $message", Toast.LENGTH_LONG).show()
            }
        }


    }
}