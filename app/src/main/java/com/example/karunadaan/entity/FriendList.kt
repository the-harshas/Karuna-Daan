package com.example.karunadaan.entity

class FriendList {

    var userId:String
    var userName:String
    constructor(){
        userId="null"
        userName=""
    }
    constructor(userId:String, userName:String){
        this.userId = userId
        this.userName = userName
    }
}