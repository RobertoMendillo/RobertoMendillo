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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import it.unisannio.studenti.mendillo.teamwork.databinding.FragmentGroupCreationBinding
import org.w3c.dom.Text

private const val TAG = "GroupCreationFragment"

class GroupCreationFragment: Fragment() {

    private val db: FirebaseDatabase = FirebaseDatabase.getInstance("https://teamwork-2110e-default-rtdb.europe-west1.firebasedatabase.app")
    private lateinit var docRef: DocumentReference

    private var group: Group = Group()

    private lateinit var binding: FragmentGroupCreationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupCreationBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createGroupButton.setOnClickListener{

            val name = binding.editTextGroupName.text.toString()
            if(TextUtils.isEmpty(name)){
                binding.editTextGroupName.error = "Required"
            }
            else {
                val user = FirebaseAuth.getInstance().currentUser?.email.toString()
                group.name = binding.editTextGroupName.text.toString()
                group.description = binding.editTextGroupDescription.text.toString()
                group.owner = user
                db.reference.child(GROUPS).push().setValue(group)
                    .addOnSuccessListener {
                        binding.editTextGroupName.error = null
                        Log.d(TAG, "Group added with success "+ group.owner)
                        Toast.makeText(context, "Group added", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, GroupListFragment.newInstance()).commit()
                }
            }
        }

        binding.addMemberButton.setOnClickListener{
            val email = binding.addMemberEditText.text.toString()
            if(TextUtils.isEmpty(email)){
                binding.addMemberEditText.error = "Required"
            }else{
                db.reference.child(USERS).get().addOnSuccessListener { it ->
                    if(it.toString().equals(email)){
                        group.members!!.add(email)
                    }
                }

            }
        }
    }


    companion object{
        fun newInstance(): GroupCreationFragment{
            return GroupCreationFragment()
        }

        const val GROUPS = "groups"
        const val USERS = "users"
    }
}