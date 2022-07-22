package it.unisannio.studenti.mendillo.teamwork

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import it.unisannio.studenti.mendillo.teamwork.model.Group
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

private const val DATABASE_NAME = "Firebase-Firestore"

class GroupRepository {

    private val database : FirebaseFirestore = FirebaseFirestore.getInstance()

    private val executor = Executors.newSingleThreadExecutor()

    private val groups: HashMap<String, Group> = HashMap()

    fun getGroups(user: FirebaseUser, adapter: GroupAdapter): HashMap<String, Group>{
       executor.execute{
           fetchGroups(user, adapter)
       }
       return this.groups
    }

    private fun fetchGroups(user: FirebaseUser, adapter: GroupAdapter){
        this.groups.clear()
        var groupsRef = database.collection(MainActivity.GROUPS)
        Log.d(TAG, "UTENTE:"+ user.email)
        database.collection("users")
            .document(user.email!!)
            .get(Source.SERVER)
            .addOnSuccessListener { value ->
                Log.d(TAG, ""+value.toString())
                val data = value.get("groups") as HashMap<String, String>
                data.forEach {entry ->
                    groupsRef.document(entry.key)
                        .addSnapshotListener{ value, e ->
                            if(e != null){
                                Log.w(TAG, "Error Listening groups", e)
                            }else{
                                if (value != null) {
                                    this.groups.put(
                                        value?.id!!,
                                        value.toObject(Group::class.java)!!
                                    )
                                    Log.d(GroupListFragment.TAG, "GROUP:" + value)
                                }
                            }
                            adapter.setContents(toList(groups))
                            adapter.notifyDataSetChanged()
                        }
                }
            }
    }

    fun createGroup(email: String, group: Group){
        executor.execute {
            database.collection(MainActivity.GROUPS)
                .document("${group.id}")
                .set(group)
                .addOnSuccessListener {
                    addGroupToGroupList(email, group)
                    Log.d(TAG, "Group added with success "+ group.name)
                }

        }
    }

    fun addMemberToGroup(email: String, group: Group){
        executor.execute {
            database.collection(MainActivity.GROUPS)
                .document("${group.id}")
                .update("members", group.members)
                .addOnSuccessListener { Log.d(TAG, "Document successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

            addGroupToGroupList(email, group)
        }
    }

    fun removeMemberToGroup(email: String, group: Group){
        executor.execute {
            group.members?.remove(email)
            database.collection(MainActivity.GROUPS)
                .document("${group.id}")
                .delete()
            database.collection(MainActivity.GROUPS)
                .document("${group.id}")
                .set(group)
                .addOnSuccessListener {
                }

            /* Rimuovo il gruppo dalla mappa dei gruppi accessibili dall'utente */
            // recupero la mappa (group id: ruolo)
            var participantGroups: HashMap<String, String> = HashMap()
            database.collection(MainActivity.USERS)
                .document("$email")
                .get()
                .addOnSuccessListener { value ->
                    participantGroups = value.get("groups") as HashMap<String, String>
                    // rimuovo il gruppo corrente dalla mappa
                    participantGroups.remove(group.id)
                }

            // aggiorno la mappa nel database
            database.collection(MainActivity.USERS)
                .document(email)
                .update("groups", participantGroups)
                .addOnSuccessListener { Log.d(TAG, "group.id removed") }
        }
    }

    private fun addGroupToGroupList(email:String, group: Group){
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

        // aggiorno la mappa con i nuovi dati
        groups.put(group.id!!, "participant")
        Log.d(TAG, "Groups before SET $groups")
        docRef.update("groups",groups)
            .addOnCompleteListener { Log.d(TAG, "Groups updated $groups") }
            .addOnFailureListener { e -> Log.w(TAG, "Groups update failed", e) }
    }

    fun deleteGroup(email: String, group: Group){
        executor.execute {
            database.collection(MainActivity.GROUPS).document("${group.id}").delete()
                .addOnSuccessListener {
                    Log.d(ChatFragment.TAG, "DELETED GROUP ${group.name}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Deletion failed: " + e.toString())
                }

            database.collection(MainActivity.USERS)
                .document(email)
                .get().addOnSuccessListener { value ->
                    (value.get("groups") as HashMap<String, String>).remove(group.id)
                }
            /*.addSnapshotListener{ value, e ->
                if(e != null){
                    Log.w(TAG, "Error ", e)
                }else{
                    var data = value?.get("groups") as HashMap<String, String>
                    data.remove(group.id)
                }
            }*/
        }
    }

    fun getMessages(group: Group, adapter: ChatMessageAdapter): ArrayList<ChatMessage>{
        executor.execute {
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
                        adapter.notifyDataSetChanged()
                    }
                }
        }
        return group.messages!!
    }

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