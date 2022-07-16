package it.unisannio.studenti.mendillo.teamwork.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.Exclude
import it.unisannio.studenti.mendillo.teamwork.ChatMessage
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Group: Serializable{

    var messages: ArrayList<ChatMessage>? = null

    var id : String? = null
    var name: String? = null
    var description: String? = null
    var owner: String? = null
    var members: ArrayList<String>? = null

    init {
        members = ArrayList()
        messages = ArrayList()
    }


    constructor(){}


    override fun toString(): String {
        return name +":"+ description+":"+owner
    }

    @Exclude
    fun toMap(): Map<String, Any?>{
        var result: HashMap<String, Any?> = HashMap()
        result["id"] = id
        result["name"] = name
        result["description"] = description
        result["owner"] = owner
        result["members"] = members
        result["messages"] = messages
        return result
    }
}


