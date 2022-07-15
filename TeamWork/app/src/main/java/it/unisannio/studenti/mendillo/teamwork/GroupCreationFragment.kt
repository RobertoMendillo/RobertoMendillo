package it.unisannio.studenti.mendillo.teamwork

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import it.unisannio.studenti.mendillo.teamwork.databinding.FragmentGroupCreationBinding
import it.unisannio.studenti.mendillo.teamwork.model.Group

private const val TAG = "GroupCreationFragment"

class GroupCreationFragment: Fragment() {

    private val db: FirebaseDatabase = FirebaseDatabase.getInstance("https://teamwork-2110e-default-rtdb.europe-west1.firebasedatabase.app")

    private var group: Group = Group()

    private lateinit var binding: FragmentGroupCreationBinding
    private lateinit var membersRecycleView: RecyclerView
    private lateinit var adapter: MemberAdapter
    private lateinit var membersRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if(arguments?.get("group") != null){
            group = Group()
            group = arguments?.get("group") as Group
        }
        membersRef = db.reference.child(GROUPS).child("${group.id}").child("members")
        Log.d(TAG, "${group.id}")
        val options = group.members
        adapter = MemberAdapter(options!!)
        Log.d(TAG, options.toString())
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
        val user = FirebaseAuth.getInstance().currentUser?.email.toString()
        group.owner = user
        if (groupName != null){
            binding.editTextGroupName.setText(groupName)
            binding.editTextGroupDescription.setText(groupDescription)
            membersRecycleView = binding.groupMembersRecyclerView
            membersRecycleView.layoutManager = LinearLayoutManager(context)
            membersRecycleView.adapter = adapter
        }

        if (!FirebaseAuth.getInstance().currentUser?.email?.equals(group.owner)!!){
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
                group.name = binding.editTextGroupName.text.toString()
                group.description = binding.editTextGroupDescription.text.toString()
                var pushRef = db.reference.child(GROUPS).push()
                group.id = pushRef.key
                pushRef.setValue(group)
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
                group.members?.add(email)
                var postValues: Map<String, Any?> = group.toMap()
                db.reference.child(GROUPS)
                    .child("${group.id}")
                    .updateChildren(postValues)
            }
        }

        binding.removeMemberButton.setOnClickListener{
            val email = binding.addMemberEditText.text.toString()
            if(TextUtils.isEmpty(email)){
                binding.addMemberEditText.error = "Required"
            }else{
                group.members?.remove(email)
                var postValues: Map<String, Any?> = group.toMap()
                db.reference.child(GROUPS)
                    .child("${group.id}")
                    .updateChildren(postValues)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_group_creation, menu)
    }

    /**
     * ADAPTER
     */
    private inner class MemberAdapter(
        private val options: ArrayList<String>
    ):  RecyclerView.Adapter<MemberHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberHolder {
            val view = layoutInflater.inflate(R.layout.fragment_member_item, parent, false)
            return MemberHolder(view)
        }

        override fun getItemCount(): Int {
            return options.size
        }

        override fun onBindViewHolder(
            holder: MemberHolder,
            position: Int
        ) {
            val member = options[position]
            holder.bind(member)

        }
    }

    /**
     * HOLDER
     */
    private inner class MemberHolder(view: View): RecyclerView.ViewHolder(view){

        private lateinit var member: String
        private val memberEmail: TextView = view.findViewById(R.id.member_email)

        fun bind(member: String){
            this.member = member
            memberEmail.text = this.member
        }

    }

    companion object{
        fun newInstance(): GroupCreationFragment{
            return GroupCreationFragment()
        }

        const val GROUPS = "groups"
        const val USERS = "users"
        const val GROUP_DESCR = "groupDescription"
    }
}