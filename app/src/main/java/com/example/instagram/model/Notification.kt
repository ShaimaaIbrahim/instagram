package com.example.instagram.model

class Notification {

  private  var userId : String =""
    private var postId : String =""
   private var text : String =""
    private var isPost : Boolean= false

constructor()

    constructor(userId: String , postId:String , text:String , isPost:Boolean):this(){
        this.userId=userId
        this.postId=postId
        this.text=text
        this.isPost=isPost
    }
     fun getText() : String{
        return text
    }
   fun getUserId() : String{
        return userId
    }
    fun getIsPost() : Boolean{
       return isPost
   }
     fun getPostId() : String{
        return postId
    }
}