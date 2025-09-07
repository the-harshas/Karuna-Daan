package com.example.karunadaan.entity

import android.net.Uri

class DonateEntiy {
    var uid:String?=null
    var userName:String?=null
    var category:String?=null
    var address:String?=null
    var imageUri:String?=null
    var itemName:String?=null
    var itemDescription:String?=null
    var time:String?=null
    var quantity:String?=null
    var mobileNumber:String?=""
    var lat:String?=null
    var log:String?=null
    var amount:Int=0


    constructor(){}
    constructor(uid:String,userName:String, category: String,
                address:String, imageUri:String, time:String,
        itemDescription:String, itemName: String, quantity:String ){
        this.uid=uid
        this.category=category
        this.address=address
        this.imageUri=imageUri
        this.userName=userName
        this.time=time
        this.itemDescription=itemDescription
        this.itemName=itemName
        this.quantity=quantity
    }
    constructor(uid:String,userName:String, category: String,
                address:String, imageUri:String, time:String,
                itemDescription:String, itemName: String, quantity:String, mobileNumber:String ){
        this.uid=uid
        this.category=category
        this.address=address
        this.imageUri=imageUri
        this.userName=userName
        this.time=time
        this.itemDescription=itemDescription
        this.itemName=itemName
        this.quantity=quantity
        this.mobileNumber=mobileNumber
    }
    constructor(uid:String,userName:String, category: String,
                address:String, imageUri:String, time:String,
                itemDescription:String, itemName: String, quantity:String, mobileNumber:String,lat: String, log:String ){
        this.uid=uid
        this.category=category
        this.address=address
        this.imageUri=imageUri
        this.userName=userName
        this.time=time
        this.itemDescription=itemDescription
        this.itemName=itemName
        this.quantity=quantity
        this.mobileNumber=mobileNumber
        this.lat=lat
        this.log=log
    }
    constructor(uid:String,userName:String, category: String,
                address:String, imageUri:String, time:String,
                itemDescription:String, itemName: String, quantity:String, mobileNumber:String,lat: String, log:String, amount:Int ){
        this.uid=uid
        this.category=category
        this.address=address
        this.imageUri=imageUri
        this.userName=userName
        this.time=time
        this.itemDescription=itemDescription
        this.itemName=itemName
        this.quantity=quantity
        this.mobileNumber=mobileNumber
        this.lat=lat
        this.log=log
        this.amount=amount
    }
}