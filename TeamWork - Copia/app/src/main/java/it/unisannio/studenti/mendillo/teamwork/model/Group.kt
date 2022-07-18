package it.unisannio.studenti.mendillo.teamwork.model

import com.google.firebase.database.Exclude
import it.unisannio.studenti.mendillo.teamwork.ChatMessage
import java.io.Serializable

class Group: Serializable{
    @Exclude
    var messages: ArrayList<ChatMessage>? = null
    @Exclude
    var members: ArrayList<String>? = null

    var id : String? = null
    var name: String? = null
    var description: String? = null
    var owner: String? = null


    init {
        members = ArrayList()
        messages = ArrayList()
    }

    constructor(){
    }

    override fun toString(): String {
        return name +":"+ description+":"+owner
    }

    @Exclude
    fun membersToMap(): HashMap<String, Any?>{
        var result : HashMap<String, Any?> = HashMap()
        if(members?.isNotEmpty()!!){
            result["members"] = members
        }
        return result
    }

}


