package it.unisannio.studenti.mendillo.teamwork

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.unisannio.studenti.mendillo.teamwork.databinding.FragmentLoginBinding

class LoginFragment: Fragment() {


    // Firebase Auth necessaria per l'autenticazione tramite Firebase
    private lateinit var auth: FirebaseAuth

    // binding al layout del login
    private var _binding: FragmentLoginBinding? = null
    // costante che preleva e conserva il riferimento al binding
    private val binding: FragmentLoginBinding
        get() = _binding!!

    private val signIn: ActivityResultLauncher<Intent> =
        registerForActivityResult(FirebaseAuthUIActivityResultContract(), this::onSignInResult)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // listener sul bottone di sign up che apre la schermata di registrazione
        binding.signupButton.setOnClickListener {
            var fragment = SignupFragment.newInstance()
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack("signup_fragment")
                .commit()
        }

        binding.loginButton.setOnClickListener{

        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "OnStart")
        //Se non ci sono utenti registrati avvia il signInIntent creato il con il builder di
        // FireBaseUI Auth
        // altrimenti si attiva la mainActivity
        if(Firebase.auth.currentUser == null){
            // Sign in con FirebaseUI
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(listOf(
                    AuthUI.IdpConfig.EmailBuilder().build()
                ))
                .build()

            signIn.launch(signInIntent)
        }else{
            goToGroupListFragment()
        }


    }


    // funzione invocata al termine della fase di login, si gestisce il risultato del login stesso
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult){
        if(result.resultCode == Activity.RESULT_OK){
            Log.d(TAG, "Login effettuato con successo")
            goToGroupListFragment()
        }else{
            Toast.makeText(
                activity,
                "Login non riuscito",
                Toast.LENGTH_LONG
            ).show()

            val response = result.idpResponse
            if(response == null){
                Log.w(TAG, "Sing in cancellato")
            }else{
                Log.w(TAG, "Sing in errato", response.error)
            }
        }
    }

    private fun goToGroupListFragment() {
        parentFragmentManager.beginTransaction().replace(R.id.fragment_container, GroupListFragment.newInstance())
            .addToBackStack("GroupListFragment")
            .commit()
        activity?.finish()
    }

    companion object{
        fun newInstance(): LoginFragment{
            return LoginFragment()
        }

        private const val TAG = "LoginFramgent"
    }
}