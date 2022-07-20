package it.unisannio.studenti.mendillo.teamwork.model

import it.unisannio.studenti.mendillo.teamwork.GroupCreationFragment

class Participant{

    var role: GroupCreationFragment.Role? = null
    var email: String? = null

    constructor()

    constructor(email: String, role: GroupCreationFragment.Role){
        this.email = email
        this.role = role
    }

    override fun toString(): String {
        return "Parecipant:[email:$email;role:$role]"
    }

}