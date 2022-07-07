package it.unisannio.studenti.mendillo.teamwork

import it.unisannio.studenti.mendillo.teamwork.model.Message
import java.util.*
import kotlin.collections.ArrayList

class Group{
    var id: UUID = UUID.randomUUID()
    var name: String? = null
    var description: String? = null
    var messages: List<Message>? = null
    var owner: String? = null

    // Costruttore vuoto necessario per la serializzazione di Firestore
    constructor()

    constructor(name:String?, descrition:String?, messages:ArrayList<Message>?){
        this.name = name
        this.description = description
        this.messages = messages
    }


}