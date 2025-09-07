package com.example.karunadaan.entity

class User() {

    var uid: String=""
    var fullName: String=""
    var email: String=""
    var role: String=""
    var mobile: String?=""
    var phone =""
    var verified=false
    var score=0
    constructor(uid: String, fullName: String, email: String, role: String) : this() {
        this.uid=uid
        this.fullName=fullName
        this.email=email
        this.role=role
    }
    constructor(uid: String, fullName: String, email: String, role: String, mobile: String) : this() {
        this.uid=uid
        this.fullName=fullName
        this.email=email
        this.role=role
        this.mobile=mobile
    }
    fun updateScore(){
        score+=5
    }
}