package it.unisannio.studenti.mendillo.teamwork

data class ChatMessage(
    var userName: String? = null,
    var message: String? = null
){


    override fun toString(): String {
        return userName + "::" +message
    }

}
