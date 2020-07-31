package com.example.instagram.model

import android.provider.ContactsContract

class User() {


     var uid: String = ""
     var fullName: String = ""
     var userName: String = ""
     var image: String = ""
     var bio: String = ""
     var email: String = ""
     var password: String = ""


      constructor(uid : String  , fullName : String , userName: String
     , image : String , bio : String , email: String , password: String):this(){
         this.uid= uid
         this.bio=bio
         this.fullName=fullName
         this.email=email
         this.password=password
         this.userName=userName
          this.image=image
     }


 }