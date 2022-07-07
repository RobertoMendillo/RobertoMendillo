package it.unisannio.studenti.mendillo.teamwork.model

class Message{
    var text: String? = null
    var name: String? = null

    // Costruttore vuoto necessario per la serializzazione di Firestore
    constructor()

    constructor(text:String?, name:String?){
        this.text=text
        this.name=name
    }

}
