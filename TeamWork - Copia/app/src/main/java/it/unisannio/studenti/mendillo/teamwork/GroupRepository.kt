package it.unisannio.studenti.mendillo.teamwork

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import it.unisannio.studenti.mendillo.teamwork.model.ChatMessage
import it.unisannio.studenti.mendillo.teamwork.model.Group
import it.unisannio.studenti.mendillo.teamwork.model.Groups
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

private const val DATABASE_NAME = "Firebase-Firestore"

class GroupRepository {

    private val database : FirebaseFirestore = FirebaseFirestore.getInstance()

    // Oggetto che permette di eseguire del codice in un altro thread
    private val executor = Executors.newSingleThreadExecutor()

    val groups: HashMap<String, Group> = HashMap()

    // contiene anche il parametro adapter per poter aggiornare in tempo reale
    // la recycle view dei gruppi
    fun getGroups(user: FirebaseUser, adapter: GroupAdapter): HashMap<String, Group>{
        fetchGroups(user, adapter)
       return this.groups
    }

    /**
     * Accede alla collezione dei gruppi accessibili dall'utente e aggiunge uno snaphost listener alla
     * collezione dei gruppi, in modo da aggiornare la recycle view ogni volta che un gruppo viene
     * agiunto o rimosso
     */
    private fun fetchGroups(user: FirebaseUser, adapter: GroupAdapter){
        executor.execute {
            // pulisco la cache dei gruppi
            this.groups.clear()
            var groupsRef = database.collection(MainActivity.GROUPS)
            Log.d(TAG, "UTENTE:" + user.email)
            // accedo alla lista dei gruppi accessibili dall'utente
            database.collection("users")
                .document(user.email!!)
                .get(Source.SERVER)
                .addOnSuccessListener { value ->
                    Log.d(TAG, "" + value.toString())
                    // recupero l'HashMap<IDGruppo, ruolo>
                    val data = value.get("groups") as HashMap<String, String>
                    // per ogni elemento dell'HashMap uso la chiave per accedere
                    // al documento associato al gruppo
                    data.forEach { entry ->
                        groupsRef.document(entry.key)
                            .addSnapshotListener { value, e ->
                                if (e != null) {
                                    Log.w(TAG, "Error Listening groups", e)
                                } else {
                                    var group: Group? = null    // inizializzo group a null
                                    var ex = false              // imposto un flag per l'eccezione
                                    if (value != null) {
                                        var id = value?.id!!
                                        try {
                                            group = value.toObject(Group::class.java)
                                        }catch (e: NullPointerException){
                                            ex = true           // eccezzione catturata: imposto il flag a true
                                        }
                                        if(!ex && group != null){   // se l'eccezione non è stata catturata e l'oggetto non è null
                                            this.groups.put(        // aggiungo il gruppo alla collezione dei gruppi
                                                id,
                                                group!!
                                            )
                                            Log.d(GroupListFragment.TAG, "GROUP:" + group)
                                        }
                                    }
                                }
                                adapter.setContents(toList(groups)) // aggiorno la collezione dell'adapter
                                adapter.notifyDataSetChanged()      // notifico per aggiornare la recycle view
                            }
                    }
                }
        }
    }

    /**
     * Aggiunge un utente al database.
     * Necessario per conservare la collezione di gruppi a cui l'utente
     * ha accesso
     */
    fun addUserToDatabase(email: String){
        var data: HashMap<String, Any?> = HashMap()
        var groups: Map<String, String?> = HashMap()
        data["groups"] = groups

        database.collection(MainActivity.USERS)
            .document(email)
            .set(data)
    }

    /**
     * Aggiunge un gruppo al database, aggiornando la collezione di gruppi accessibili dall'utente
     * propretario
     */
    fun createGroup(email: String, group: Group){
        executor.execute {
            // aggiungo il gruppo alla collezione dei gruppi nel database
            database.collection(MainActivity.GROUPS)
                .document("${group.id}")
                .set(group)
                .addOnSuccessListener {
                    // aggiungo il gruppo alla collezione dei gruppi accessibili dall'utente corrente
                    addGroupToGroupList(email, group)
                    Log.d(TAG, "Group added with success "+ group.name)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Failed create group ${group.name} ", e)
                }
                .addOnCompleteListener {
                    this.groups.put(group.id!!, group) // infine aggiungo il gruppo alla collezione locale
                }

        }
    }

    /**
     * Aggiunge un membro ad un gruppo aggiornando la lista di gruppi accessibili dell'utente
     * specificato e aggiornando la collezione di membri all'interno del gruppo stesso
     * nel database
     */
    fun addMemberToGroup(email: String, group: Group){
        executor.execute {
            // aggiorno la collezione dei membri di un gruppo
            database.collection(MainActivity.GROUPS)
                .document("${group.id}")
                .update("members", group.members)
                .addOnSuccessListener { Log.d(TAG, "Document successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

            addGroupToGroupList(email, group) // aggiungo il gruppo alla collezione dei gruppi accessibili dall'utente
        }
    }

    /**
     * Rimuovo un membro da un gruppo aggiornando la collezione di gruppi accessibili dell'utente
     * specificato e aggiornando la collezione di membri all'interno del gruppo nel database
     */
    fun removeMemberToGroup(email: String, group: Group){
        executor.execute {
            var groups: Groups? = null

            /* Rimuovo il gruppo dalla mappa dei gruppi accessibili dall'utente */
            // recupero la mappa (group id: ruolo)
            //var participantGroups: HashMap<String, String> = HashMap()
            database.collection(MainActivity.USERS)
                .document(email)
                .get(Source.SERVER)
                .addOnSuccessListener { value ->
                    groups = value.toObject(Groups::class.java)
                }
                .addOnCompleteListener {
                    // rimuovo il gruppo corrente dalla mappa
                    groups?.remove(group.id!!)
                    // aggiorno la mappa nel database
                    database.collection(MainActivity.USERS)
                        .document(email)
                        .set(groups!!)
                        .addOnSuccessListener { Log.d(TAG, "group.id removed") }
                        .addOnCompleteListener {
                            group.members?.remove(email)            // rimuovo il membro dalla lista dei membri del gruppo
                            // aggiorno la collezione dei membri cancellandola e riscrivendola nel database
                            database.collection(MainActivity.GROUPS)
                                .document("${group.id}")
                                .delete()
                            database.collection(MainActivity.GROUPS)
                                .document("${group.id}")
                                .set(group)
                                .addOnSuccessListener {
                                }
                        }
                }
        }
    }

    // versione precedende del metodo addGroupToList NON FUNZIONANTE
    /*private fun addGroupToGroupList(email:String, group: Group){
        var groups: HashMap<String, String> = HashMap()
        //var groups = Groups()
        val docRef = database.collection(MainActivity.USERS)
            .document("$email")
        // recupero la lista dei gruppi a cui l'utente ha accesso
        // DEBUG: Per qualche motivo a me ignoto l'accesso non va a buon fine e il
        //        codice nel success listener non viene eseguito. Per cui non è
        //        possibile aggiugere più di un gruppo
        database.collection(MainActivity.USERS)
            .document(email)
            .get(Source.SERVER)
            .addOnSuccessListener { value ->
                var data= value.get("groups") as HashMap<String, String>
                data.forEach{ entry ->
                    groups.put(entry.key, entry.value)
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Add group to list failed", e)
            }

        // aggiorno la mappa con i nuovi dati
        groups.put(group.id!!, "participant")
        Log.d(TAG, "Groups before SET $groups")
        docRef.update("groups",groups)
            .addOnCompleteListener { Log.d(TAG, "Groups updated $groups") }
            .addOnFailureListener { e -> Log.w(TAG, "Groups update failed", e) }
    }*/

    /**
     * Aggiunge un gruppo alla lista dei gruppi accessibili dall'utente specificato
     */
    private fun addGroupToGroupList(email:String, group: Group){
        var groups: Groups? = null
        val docRef = database.collection(MainActivity.USERS)
            .document(email)
        // recupero la lista dei gruppi a cui l'utente ha accesso
        docRef.get(Source.SERVER)
            .addOnSuccessListener { value ->
               groups = value.toObject(Groups::class.java)!!    // converto il documento in oggetto Groups
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Add group to list failed", e)
            }
            .addOnCompleteListener {
                // aggiorno la mappa con i nuovi dati
                groups?.put(group.id!!, "participant")      // aggiungo il gruppo alla collezione dei gruppi accessibili dall'utente
                Log.d(TAG, "Groups before SET $groups")
                docRef.set(groups!!)
                    .addOnCompleteListener { Log.d(TAG, "Groups updated $groups") }
                    .addOnFailureListener { e -> Log.w(TAG, "Groups update failed", e) }
            }


    }

    /**
     * Elimina un gruppo.
     * Rimuove anche il riferimento nella collezione dei gruppi accessibili dall'utente
     */
    fun deleteGroup(email: String, group: Group){
        executor.execute {
            var groups: Groups? = null
            val docRef = database.collection(MainActivity.USERS).document(email)

            // Recupero la lista dei gruppi a cui l'utente ha accesso
            docRef.get(Source.SERVER)
                .addOnSuccessListener { value ->
                    groups = value.toObject(Groups::class.java)!!
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Add group to list failed", e)
                }
                .addOnCompleteListener {
                    // aggiorno la mappa con i nuovi dati
                    groups?.remove(group.id!!)
                    Log.d(TAG, "Groups before SET $groups")
                    docRef.set(groups!!)
                        .addOnCompleteListener { Log.d(TAG, "Groups updated $groups") }
                        .addOnFailureListener { e -> Log.w(TAG, "Groups update failed", e) }
                        .addOnCompleteListener {
                            // rimuovo il gruppo dalla collezione dei gruppi
                            database.collection(MainActivity.GROUPS).document("${group.id}").delete()
                                .addOnSuccessListener {
                                    Log.d(ChatFragment.TAG, "DELETED GROUP ${group.name}")
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Deletion failed: " + e.toString())
                                }
                                .addOnCompleteListener {
                                    Log.d(TAG, "Group ${group.name} deleted")
                                }
                        }
                }
        }
    }

    /**
     * Aggiorna il gruppo con i nuovi campi
     */
    fun updateGroup(group: Group){
        executor.execute {
            /*database.collection(MainActivity.GROUPS)
                .document("${group.id}")
                .update("name", group.name, "description", group.description, "picture", group.picture)
                .addOnSuccessListener { Log.d(TAG, "Group ${group.name} updated") }
                .addOnFailureListener { e -> Log.w(TAG, "Failed update group ${group.name}") }
                .addOnCompleteListener { Log.d(TAG, "Update group ${group.name} completed")}*/
            database.collection(MainActivity.GROUPS)
                .document("${group.id}")
                .delete()
                .addOnSuccessListener { Log.d(TAG, "Group ${group.name} updated") }
                .addOnFailureListener { e -> Log.w(TAG, "Failed update group ${group.name}") }
                .addOnCompleteListener {
                    database.collection(MainActivity.GROUPS)
                        .document("${group.id}")
                        .set(group)
                }
        }
    }

    /**
     * Recupera i messaggi inviati nella chat di un gruppo specificato.
     */
    fun getMessages(group: Group, adapter: ChatMessageAdapter): ArrayList<ChatMessage>{
        executor.execute {
            // accedo al gruppo, alla collezione dei messaggi e li prelevo in ordine crescente di data
            // grazie a SnapshotListener posso prelevare i messaggi non appena vengo aggiunti al database
            database.collection(MainActivity.GROUPS)
                .document("${group.id}")
                .collection("messages")
                .orderBy("date")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.w(ChatFragment.TAG, "Error occurred", error)
                    } else {
                        group.messages?.clear()
                        for (doc in value!!) {
                            group.messages?.add(doc.toObject(ChatMessage::class.java))
                        }
                        adapter.notifyDataSetChanged()  // aggiorno la recycle view
                    }
                }
        }
        return group.messages!!
    }

    /** Funzione che estrapola la lista dei grupi dalla HashMap fornita come parametro.
     * Utile per l'Adapter della recycle view
     */
    fun toList(map: HashMap<String, Group>): ArrayList<Group> {
        val listOfGroups : ArrayList<Group> = ArrayList()
        map.forEach { entry ->
            listOfGroups.add(entry.value)
        }
        return listOfGroups
    }

    companion object{
        private var INSTANCE : GroupRepository? = null

        fun initialize(context: Context){
            if(INSTANCE == null){
                INSTANCE = GroupRepository()
            }
        }

        fun get(): GroupRepository{
            return INSTANCE ?:
            throw IllegalStateException("GroupRepository must be initialized")
        }

        const val TAG = "GroupRepository"
    }
}