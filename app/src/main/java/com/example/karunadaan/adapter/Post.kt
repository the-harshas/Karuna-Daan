package com.example.karunadaan.adapter

data class Post(
    var postedBy:String,
    var postedByUid: String,
    var postContent: String,
    var postLikeCount:Int ?=0,
    var postCommentCount:Int ?=0,
    var commentsId: String ?=null,
    var postTime: String,
    var postImages: ArrayList<String> ?=null
) {
    private fun inceaseLike(){
        postLikeCount = postLikeCount!! + 1;
    }
    private fun inceaseComment(){
        postLikeCount = postLikeCount!! + 1;
    }
}