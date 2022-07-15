package it.unisannio.studenti.mendillo.teamwork

class ChatMessage{

    var userName: String? = null
    var message: String? = null

    constructor()

    constructor(username: String, text: String){
        this.userName = username
        this.message = text
    }

    override fun toString(): String {
        return userName + "::" +message
    }

}
