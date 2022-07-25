package it.unisannio.studenti.mendillo.teamwork.model

import java.util.*

class ChatMessage{

    var userName: String? = null
    var message: String? = null
    var date: Date = Date()

    constructor()

    constructor(username: String, text: String){
        this.userName = username
        this.message = text
    }

    override fun toString(): String {
        return userName + "::" +message + "::"+ date
    }

}
