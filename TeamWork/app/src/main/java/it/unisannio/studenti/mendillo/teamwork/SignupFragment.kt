package it.unisannio.studenti.mendillo.teamwork

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.unisannio.studenti.mendillo.teamwork.databinding.FragmentSignupBinding

class SignupFragment: Fragment(){

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentSignupBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signupButton.setOnClickListener{
            val email = binding.signupEmailEdittext.text.toString()
            val password = binding.signupPasswordEdittext.text.toString()
            createAccount(email, password)
        }

        auth = Firebase.auth
    }

    fun createAccount(email: String, password: String){
        Log.d(TAG, "createAccount:$email")
        if(!validateForm()){
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()){ task ->
                if(task.isSuccessful){
                    // Registrazione completata con successo
                    Log.d(TAG, "createUserWithEmailPassword:success")
                    val user = auth.currentUser
                    //TODO aggiungere redirezione verso lista dei gruppi
                }else{
                    Log.w(TAG, "createUserWithEmailPassword:failed", task.exception)
                    Toast.makeText(context, "Sign up failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validateForm(): Boolean{

        var valid = true
        // verifichiamo che il campo email sia stato inserito
        val email = binding.signupEmailEdittext.text.toString()
        if(TextUtils.isEmpty(email)){
            binding.signupEmailEdittext.error = "Required"
            valid = false
        }else{
            binding.signupEmailEdittext.error = null
        }

        // verifichiamo che il campo password sia stato inserito
        val password = binding.signupPasswordEdittext.text.toString()
        if(TextUtils.isEmpty(password)){
            binding.signupPasswordEdittext.error = "Required"
            valid = false
        }else{
            binding.signupPasswordEdittext.error = null
        }

        return valid
    }

    companion object{
        fun newInstance(): SignupFragment{
            return SignupFragment()
        }

        val TAG = "SignUpFragment"
    }
}