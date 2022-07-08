package it.unisannio.studenti.mendillo.teamwork

import it.unisannio.studenti.mendillo.teamwork.model.Message
import java.util.*
import kotlin.collections.ArrayList

class Group(var name: String? = null,
            var description: String? = null,
            //var messages: List<Message>? = null,
            /*var owner: String? = null*/){

    // Costruttore vuoto necessario per la serializzazione di Firestore





    override fun toString(): String {
        return name +":"+ description
    }

}