package it.unisannio.studenti.mendillo.teamwork

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import it.unisannio.studenti.mendillo.teamwork.databinding.FragmentGroupCreationBinding

private const val TAG = "GroupCreationFragment"

class GroupCreationFragment: Fragment() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var docRef: DocumentReference

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
                binding.editTextGroupName.error = null

                val description = binding.editTextGroupDescription.text.toString()

                var groupsToSave: HashMap<String, Any> = HashMap()
                groupsToSave.put("name", name)
                groupsToSave.put("description", description)
                //groupsToSave.put("messages", null)
                //groupsToSave.put("owner", owner)
                docRef = db.collection("groups").document(name)
                docRef.set(groupsToSave).addOnSuccessListener {
                    Log.d(TAG, "Group added with success")
                    Toast.makeText(context, "Group added", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.beginTransaction().remove(this).commit()
                }
            }
        }
    }


    companion object{
        fun newInstance(): GroupCreationFragment{
            return GroupCreationFragment()
        }
    }
}