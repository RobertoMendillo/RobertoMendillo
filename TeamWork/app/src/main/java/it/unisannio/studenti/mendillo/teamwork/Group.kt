package it.unisannio.studenti.mendillo.teamwork

import it.unisannio.studenti.mendillo.teamwork.model.Message
import java.util.*
import kotlin.collections.ArrayList

class Group(
    var name: String? = null,
    var description: String? = null,
    var owner: String? = null,
    var members: ArrayList<String>? = null){

    override fun toString(): String {
        return name +":"+ description+":"+owner
    }

}