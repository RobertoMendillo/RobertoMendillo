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
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import it.unisannio.studenti.mendillo.teamwork.databinding.FragmentGroupCreationBinding
import it.unisannio.studenti.mendillo.teamwork.model.Group
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "GroupCreationFragment"

class GroupCreationFragment: Fragment() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private var group: Group = Group()

    private lateinit var binding: FragmentGroupCreationBinding
    private lateinit var membersRecycleView: RecyclerView
    private lateinit var adapter: MemberAdapter
    private lateinit var membersRef: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if(arguments?.get("group") != null){
            group = Group()
            group = arguments?.get("group") as Group
        }
        membersRef = db.collection(MainActivity.GROUPS).document("${group.id}").collection("members").document("list")
        membersRef.get()
            .addOnSuccessListener {
                if(it.exists()){
                    group.members = it["members"] as ArrayList<String> /* = java.util.ArrayList<kotlin.String> */
                }
                Log.d(TAG, "MEMBERS"+group.members.toString())
                }
        Log.d(TAG, "${group.id}")
        adapter = MemberAdapter(group.members!!)
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
                group.name = binding.editTextGroupName.text.toString()
                group.description = binding.editTextGroupDescription.text.toString()
                group.id = UUID.randomUUID().toString()
                db.collection(MainActivity.GROUPS).document("${group.id}").set(group)
                    .addOnSuccessListener {
                        binding.editTextGroupName.error = null
                        Log.d(TAG, "Group added with success "+ group.owner)
                        Toast.makeText(context, "Group added", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, GroupListFragment.newInstance()).commit()
                }
                adapter.notifyDataSetChanged()
            }
        }

        binding.addMemberButton.setOnClickListener{
            val email = binding.addMemberEditText.text.toString()
            if(TextUtils.isEmpty(email)){
                binding.addMemberEditText.error = "Required"
            }else{
                group.members?.add(email)
                db.collection(MainActivity.GROUPS)
                    .document("${group.id}")
                    .collection(MainActivity.MEMBERS)
                    .document("list")
                    .delete()

                val data = group.membersToMap()
                Toast.makeText(context, data.toString(), Toast.LENGTH_LONG).show()
                db.collection(MainActivity.GROUPS)
                    .document("${group.id}")
                    .collection(MainActivity.MEMBERS)
                    .document("list")
                    .set(data)
            }
            adapter.notifyDataSetChanged()
        }

        binding.removeMemberButton.setOnClickListener{
            val email = binding.addMemberEditText.text.toString()
            if(TextUtils.isEmpty(email)){
                binding.addMemberEditText.error = "Required"
            }else{
                group.members?.remove(email)
                db.collection(MainActivity.GROUPS)
                    .document("${group.id}")
                    .collection(MainActivity.MEMBERS)
                    .document("list")
                    .delete()
                val data = group.membersToMap()
                db.collection(MainActivity.GROUPS)
                    .document("${group.id}")
                    .collection(MainActivity.MEMBERS)
                    .document("list")
                    .set(data)
                adapter.notifyDataSetChanged()
            }
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
            var member = options[position]
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
    }
}