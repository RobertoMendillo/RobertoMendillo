package it.unisannio.studenti.mendillo.teamwork

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList

class GroupListViewModel: ViewModel() {

    var groups = ArrayList<Group>()

    init {
        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val collection = firestore.collection("groups")
        groups = ArrayList()
        collection.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val group = Group()
                    group.name = document.get("name").toString()
                    group.description = document.get("description").toString()
                    groups.add(group)
                    Log.d("GROUP ISTANCE", document.toObject(Group::class.java).toString())
                }
            }

        /*for(i in 0 until 100){
            val group = Group()
            group.name = "Group ${i+1}"
            group.description = "Descr ${i+1}"
            groups.add(group)
        }*/
        Thread.sleep(1000)
    }






}