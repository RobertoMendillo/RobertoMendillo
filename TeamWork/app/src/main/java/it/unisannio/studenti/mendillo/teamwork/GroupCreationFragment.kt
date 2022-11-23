package it.unisannio.studenti.mendillo.teamwork

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import it.unisannio.studenti.mendillo.teamwork.databinding.FragmentGroupCreationBinding
import it.unisannio.studenti.mendillo.teamwork.model.Group
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

private const val TAG = "GroupCreationFragment"

class GroupCreationFragment: Fragment() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private var group: Group = Group()

    private lateinit var binding: FragmentGroupCreationBinding
    private lateinit var membersRecycleView: RecyclerView
    private lateinit var adapter: MemberAdapter
    private lateinit var membersRef: DocumentReference

    private val groupRepository = GroupRepository.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if(arguments?.get("group") != null){
            group = Group()
            group = arguments?.get("group") as Group
        }

        Log.d(TAG, "${group.id}")
        var listOfParticipant : ArrayList<String?> = ArrayList()
        group.members?.forEach { entry ->
            listOfParticipant.add(entry.key)
        }
        adapter = MemberAdapter(listOfParticipant, layoutInflater)
        //Log.d(TAG, options.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupCreationBinding.inflate(inflater, container, false)
        if(group == null) this.group = Group()
        var groupName = group.name
        var groupDescription = group.description
        val user = FirebaseAuth.getInstance().currentUser?.uid.toString()
        group.owner = user
        if (groupName != null){
            binding.editTextGroupName.setText(groupName)
            binding.editTextGroupDescription.setText(groupDescription)
            membersRecycleView = binding.groupMembersRecyclerView
            membersRecycleView.layoutManager = LinearLayoutManager(context)
            membersRecycleView.adapter = adapter
        }

        if (!FirebaseAuth.getInstance().currentUser?.uid?.toString().equals(group.owner)!!){
            binding.removeMemberButton.isEnabled = false
            binding.addMemberButton.isEnabled = false
            binding.addMemberEditText.isEnabled = false
        }

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
                var email = FirebaseAuth.getInstance().currentUser?.email.toString()
                /* inizializzo i campi del gruppo */
                group.name = name
                group.description = binding.editTextGroupDescription.text.toString()
                group.id = UUID.randomUUID().toString()
                group.members?.put(email, "owner")
                GroupRepository.get().groups.put(group.id!!, group)
                // aggiungo il gruppo nel database
                groupRepository.createGroup(email, group)
                binding.editTextGroupName.error = null

                val fragment = GroupListFragment.newInstance()
                Toast.makeText(context, "Group ${group.name} added", Toast.LENGTH_SHORT).show()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit()
            }

        }

        binding.addMemberButton.setOnClickListener{
            val email = binding.addMemberEditText.text.toString()
            if(TextUtils.isEmpty(email)){
                binding.addMemberEditText.error = "Required"
            }else {
                // aggiungo il membro al gruppo
                group.members?.put(email, "participant")
                // aggiungo il membro al gruppo nel database
                groupRepository.addMemberToGroup(email, group)
                binding.addMemberEditText.text.clear()
            }
        }

        binding.removeMemberButton.setOnClickListener{
            val email = binding.addMemberEditText.text.toString()
            if(TextUtils.isEmpty(email)){
                binding.addMemberEditText.error = "Required"
            }else{
                // Rimuovo il membro dalla lista dei membri nel gruppo specifico
                group.members?.remove(email)
                // Rimuovo il membro dal gruppo nel database
                groupRepository.removeMemberToGroup(email, group)
                Toast.makeText(context, "$email removed", Toast.LENGTH_SHORT).show()
            }
            binding.addMemberEditText.text.clear()
        }
    }



    override fun onStart() {
        super.onStart()
        adapter.notifyDataSetChanged()
    }

    override fun onPause() {
        adapter.notifyDataSetChanged()
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_group_creation, menu)
    }





    companion object{
        fun newInstance(): GroupCreationFragment{
            return GroupCreationFragment()
        }
    }
}