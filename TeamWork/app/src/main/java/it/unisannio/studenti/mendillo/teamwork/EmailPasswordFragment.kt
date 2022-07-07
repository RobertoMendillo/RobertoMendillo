package it.unisannio.studenti.mendillo.teamwork

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import it.unisannio.studenti.mendillo.teamwork.databinding.FragmentEmailPasswordBinding

class EmailPasswordFragment: Fragment() {

    // Firebase Auth necessaria per l'autenticazione tramite Firebase
    private lateinit var auth: FirebaseAuth

    // binding al layout del login
    private var _binding: FragmentEmailPasswordBinding? = null
    // costante che preleva e conserva il riferimento al binding
    private val binding: FragmentEmailPasswordBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEmailPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object{
        fun newInstance(): EmailPasswordFragment{
            return EmailPasswordFragment()
        }
    }
}