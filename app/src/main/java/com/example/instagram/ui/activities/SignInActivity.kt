package com.example.instagram.ui.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.instagram.MainActivity
import com.example.instagram.R
import com.example.instagram.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import java.awt.font.TextAttribute

class SignInActivity : AppCompatActivity() {
/**
** created by shaimaa salama
 */

    private lateinit var binding : ActivitySignInBinding
    private lateinit var progressDialoge : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       binding = DataBindingUtil.setContentView(this , R.layout.activity_sign_in)
        /**
        ** sign up to new account
         */
        binding.signLinkBtn.setOnClickListener {
        startActivity(Intent(this , SignUpActivity::class.java))
        }
        binding.signupBtn.setOnClickListener {
            logInUser()
        }
    }

    private fun logInUser() {
        val email = binding.emailAddress.text.toString()
        val password = binding.password.text.toString()

        when{
            TextUtils.isEmpty(email) -> binding.emailAddress.setError("field is empty")
            TextUtils.isEmpty(password) -> binding.password.setError("field is empty")

            else -> {
                progressDialoge = ProgressDialog(this)
                progressDialoge.setTitle("sign up")
                progressDialoge.setMessage("please wait , this message may take a while")
                progressDialoge.setCanceledOnTouchOutside(false)
                progressDialoge.show()
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email , password).addOnCompleteListener {
                    if (it.isSuccessful){
                        progressDialoge.dismiss()
                        val intent = Intent(this , MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }else{
                        FirebaseAuth.getInstance().signOut()
                        progressDialoge.dismiss()
                        val message = it.exception
                        Toast.makeText(this , "Error $message", Toast.LENGTH_LONG).show()
                    }
                }
            } } }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser!=null){
            val intent = Intent(this , MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }
}