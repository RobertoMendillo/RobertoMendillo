package it.unisannio.studenti.mendillo.teamwork

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import it.unisannio.studenti.mendillo.teamwork.databinding.FragmentGroupListBinding
import it.unisannio.studenti.mendillo.teamwork.databinding.ListGroupItemBinding

private const val ARG_USER_ID = "user_id"

class GroupListFragment: Fragment() {

    private lateinit var user: FirebaseUser
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var groups: ArrayList<Group>

    private lateinit var binding: FragmentGroupListBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        collectGroups()
    }

    private fun collectGroups(){
        val collection = firestore.collection("groups")
        groups = ArrayList()
        collection.get()
            .addOnSuccessListener{documents ->
                for(document in documents){
                    groups.add(document.toObject(Group::class.java))
                    Log.d("GROUP ISTANCE", document.toObject(Group::class.java).toString())
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
       // callbacks = context as Callbacks?
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addGroupButton.setOnClickListener{
            val fragment = GroupCreationFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack("GroupCrationFragment")
                .commit()
        }
        updateUI()
    }

    private fun updateUI(){
        collectGroups()
        var adapter: GroupAdapter = GroupAdapter(groups)
        binding.groupListRecycleView.adapter = adapter
    }


    fun setUser(user: FirebaseUser){
        this.user = user
    }

    /**
     * ViewHolder for List Group Item
     */
    private inner class GroupHolder(private val binding: ListGroupItemBinding) :
     RecyclerView.ViewHolder(binding.root){

        private lateinit var group: Group

        val groupNameView: TextView = binding.groupNameLabel
        val groupDescriptionView: TextView = binding.groupDescription

        fun bind(group: Group){
            this.group = group
            binding.groupNameLabel.text = group.name
            binding.groupDescription.text = group.description
        }
     }

    /**
     * Adapter for Group List Recycle View
     */
    private inner class GroupAdapter(var groups: ArrayList<Group>):
    RecyclerView.Adapter<GroupHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupHolder {
            val binding = ListGroupItemBinding.inflate(layoutInflater, parent, false)
            return GroupHolder(binding)
        }

        override fun onBindViewHolder(holder: GroupHolder, position: Int) {
            val group = groups[position]
            holder.bind(group)
        }

        override fun getItemCount(): Int {
           return groups.size
        }
    }


    companion object {
        fun newInstance(): GroupListFragment{
            return GroupListFragment()
        }
    }
}