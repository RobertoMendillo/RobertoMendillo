package it.unisannio.studenti.mendillo.teamwork

import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class Group(
    var id : String? = null,
    var name: String? = null,
    var description: String? = null,
    var owner: String? = null,
    var members: ArrayList<String>? = null,
    var messages: Stack<ChatMessage>? = null
    ) : Serializable{

    init {
        members = ArrayList()
        messages = Stack()
    }

    override fun toString(): String {
        return name +":"+ description+":"+owner
    }

}