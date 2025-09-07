package com.example.karunadaan.entity

import java.util.Date

class Message {
    var message: String? = null
    var senderId: String? = null
    var uri :String ?= null
    var audio = 0;
    var messageTime : Long  ?= null;
    var read: Boolean ?=false

    constructor(){}

    constructor(message: String?,senderId: String?) {
        this.message = message
        this.senderId = senderId
        this.messageTime =  Date().getTime()
    }
    constructor(message: String?,senderId: String?,uri :String?) {
        this.message = message
        this.senderId = senderId
        this.uri = uri
        this.audio = 1
        this.messageTime =  Date().getTime()
    }

    override fun equals(other: Any?): Boolean {
        return (other is Message) && (other.senderId == this.senderId) &&
                (other.messageTime == this.messageTime) && (other.message == this.message)
    }
    override fun hashCode(): Int {
        return senderId.hashCode() * 31 + messageTime.hashCode() * 31 + message.hashCode()
    }


}