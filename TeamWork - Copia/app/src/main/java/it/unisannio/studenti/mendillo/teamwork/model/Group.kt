package it.unisannio.studenti.mendillo.teamwork.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.media.Image
import com.google.firebase.database.Exclude
import java.io.File
import java.io.Serializable

class Group: Serializable{
    @Exclude
    var messages: ArrayList<ChatMessage>? = null

    @Exclude
    var members: HashMap<String, String>? = null

    var id : String? = null
    var name: String? = null
    var description: String? = null
    var owner: String? = null
    var picture: File? = null

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


