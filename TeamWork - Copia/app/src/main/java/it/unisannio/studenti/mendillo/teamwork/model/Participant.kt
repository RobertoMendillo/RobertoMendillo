package it.unisannio.studenti.mendillo.teamwork.model

class Participant{

    var uid: String? = null
    var email: String? = null

    constructor()

    override fun toString(): String {
        return "Parecipant:[uid:$uid;email:$email]"
    }

}