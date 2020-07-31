package com.example.instagram.model

class Post() {

    var description : String =""
    var postId : String= ""
    var postImage : String=""
    var publisher : String = ""

constructor( description: String , postId : String , postImage : String , publisher : String ) :this(){
    this.description=description
    this.postId=postId
    this.postImage=postImage
    this.publisher=publisher
}


}