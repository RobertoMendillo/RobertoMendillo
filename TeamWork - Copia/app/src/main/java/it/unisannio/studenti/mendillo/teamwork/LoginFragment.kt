package it.unisannio.studenti.mendillo.teamwork

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
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
import com.google.firebase.auth.FirebaseUser
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

        binding.signinButton.setOnClickListener{
            if(Firebase.auth.currentUser == null){
                val email = binding.signinEmailEdittext.text.toString()
                val password = binding.signinPasswordEdittext.text.toString()
                signIn(email, password)

            }
        }

        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "OnStart")
    }


   private fun signIn(email: String, password: String){
       Log.d("LoginFragment - Sign In", email + password)
       if(!validateForm()){
           return
       }

       auth.signInWithEmailAndPassword(email, password)
           .addOnCompleteListener(requireActivity()){ task ->
               if(task.isSuccessful){
                   // Login avvenuto con successo
                   Log.d(TAG, "Signin successful")
                   val user = auth.currentUser
                   goToGroupListFragment(email)
               }
           }
   }

    private fun validateForm(): Boolean{

        var valid = true
        // verifichiamo che il campo email sia stato inserito
        val email = binding.signinEmailEdittext.text.toString()
        if(TextUtils.isEmpty(email)){
            binding.signinEmailEdittext.error = "Required"
            valid = false
        }else{
            binding.signinEmailEdittext.error = null
        }

        // verifichiamo che il campo password sia stato inserito
        val password = binding.signinPasswordEdittext.text.toString()
        if(TextUtils.isEmpty(password)){
            binding.signinPasswordEdittext.error = "Required"
            valid = false
        }else{
            binding.signinPasswordEdittext.error = null
        }

        return valid
    }

    private fun goToGroupListFragment(email: String) {
        var bundle: Bundle = Bundle()
        bundle.putSerializable("user",email)
        val fragment: Fragment = GroupListFragment()
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, GroupListFragment.newInstance())
            .addToBackStack("GroupListFragment")
            .commit()
    }

    companion object{
        fun newInstance(): LoginFragment{
            return LoginFragment()
        }

        private const val TAG = "LoginFramgent"
    }
}