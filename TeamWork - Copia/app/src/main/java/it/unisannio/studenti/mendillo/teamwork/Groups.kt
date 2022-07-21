package it.unisannio.studenti.mendillo.teamwork

import java.io.Serializable
import kotlin.collections.HashMap

class Groups: Serializable {

    var groups: HashMap<String, String>? = null

    constructor(){
        groups = HashMap()
    }

    constructor(data : HashMap<String, String>){
        this.groups = data
    }

    fun put(idGroup: String, role:String){
        this.groups?.put(idGroup, role)
    }

    override fun toString(): String {
        return groups.toString()
    }
}