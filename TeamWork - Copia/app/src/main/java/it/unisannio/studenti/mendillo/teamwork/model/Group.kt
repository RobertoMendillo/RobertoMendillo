package it.unisannio.studenti.mendillo.teamwork.model

import com.google.apphosting.datastore.testing.DatastoreTestTrace
import com.google.firebase.database.Exclude
import it.unisannio.studenti.mendillo.teamwork.ChatMessage
import it.unisannio.studenti.mendillo.teamwork.GroupCreationFragment
import java.io.Serializable
import it.unisannio.studenti.mendillo.teamwork.GroupCreationFragment.Role

class Group: Serializable{
    @Exclude
    var messages: ArrayList<ChatMessage>? = null

    @Exclude
    var members: HashMap<String, String>? = null

    var id : String? = null
    var name: String? = null
    var description: String? = null
    var owner: String? = null

    init {
        members = HashMap()
        messages = ArrayList()
    }

    constructor(){
        members = HashMap()
        messages = ArrayList()
    }

    override fun toString(): String {
        return name +":"+ description+":"+owner
    }

    /*@Exclude
    fun membersToList(): ArrayList<String>{
        var result : ArrayList<String> = ArrayList()
        if(members?.isNotEmpty()!!) {
            members!!.forEach { entry ->
                result.add(entry.key)
            }
        }
        return result
    }
*/
}


