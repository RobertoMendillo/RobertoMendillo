package it.unisannio.studenti.mendillo.teamwork

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import it.unisannio.studenti.mendillo.teamwork.model.Group

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity(), GroupListFragment.Callbacks {

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Inizializza Firebase auth e verifica se l'utente ha effettuato il login
        auth = FirebaseAuth.getInstance()
        if(auth.currentUser == null){
            Log.d("MAINACTIVITY", "START AUTHENTICATION ACTIVITY")
            // L'utente non è loggato, viene lanciata l'activity per effettuare il login
            var fragment = LoginFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit()
            //startActivity(Intent(this, AuthenticationActivity::class.java))
            return
        }
        else{
            // L'utente è loggato e si avvia il fragment che mostra la lista dei gruppi
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if(currentFragment == null){
                val fragment = GroupListFragment.newInstance()
                supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit()
            }
        }
    }

    override fun onGroupSelected(group: Group) {
        var fragment = ChatFragment.newInstance(group)
        var bundle = Bundle()
        bundle.putSerializable("group", group)
        bundle.putSerializable(USER, auth.currentUser?.email.toString())
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
    }




    companion object{
        const val GROUPS = "groups"
        const val USERS = "users"
        const val MEMBERS = "members"
        const val USER = "user"

    }

}