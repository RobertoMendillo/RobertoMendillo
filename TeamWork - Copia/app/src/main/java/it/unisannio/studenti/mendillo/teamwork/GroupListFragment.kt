package it.unisannio.studenti.mendillo.teamwork

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import it.unisannio.studenti.mendillo.teamwork.databinding.FragmentGroupListBinding
import it.unisannio.studenti.mendillo.teamwork.model.Group


class GroupListFragment: Fragment(){

    /**
     * Required interface for hosting activities
     */
    interface Callbacks {
        fun onGroupSelected(group: Group)
    }
    private var callbacks: Callbacks? = null

    private lateinit var groupRecycleView: RecyclerView
    private lateinit var adapter: GroupAdapter
    private lateinit var manager: LinearLayoutManager

    private lateinit var database: FirebaseFirestore

    private lateinit var binding: FragmentGroupListBinding

    private var auth: FirebaseUser? = null

    private lateinit var groups: HashMap<String, Group>
    private val groupRepository = GroupRepository.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val authReference = FirebaseAuth.getInstance()
        auth = authReference.currentUser
        database = FirebaseFirestore.getInstance()
        groupRepository.getGroups(auth!!)
        adapter = GroupAdapter(groupRepository.toList())
        manager = WrapContentLinearLayoutManager(context)
        manager.stackFromEnd = true
        Log.d(TAG, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupListBinding.inflate(inflater, container, false)
        binding.groupListRecycleView.adapter = adapter
        binding.groupListRecycleView.layoutManager = manager
        groupRecycleView = binding.groupListRecycleView
        groupRecycleView.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var addGroupButton: FloatingActionButton = view.findViewById(R.id.add_group_button) as FloatingActionButton
        addGroupButton.setOnClickListener{
            val fragment = GroupCreationFragment.newInstance()
            parentFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
        }
        Log.d(TAG, "onViewCreated")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_group_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.logout -> {
                Firebase.auth.signOut()
                val fragment = LoginFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null).commit()
                true
            }
            R.id.sync -> {

                Log.d(TAG, "UTENTE:"+FirebaseAuth.getInstance().currentUser?.email.toString())

                groupRepository.getGroups(auth!!)
                adapter = GroupAdapter(groupRepository.toList())
                binding.groupListRecycleView.adapter = adapter
                adapter.notifyDataSetChanged()
                true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    /**
     * HOLDER
     */
    private inner class GroupHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{

        private lateinit var group: Group

        private val nameTextView: TextView = itemView.findViewById(R.id.group_name_label)
        private val descrTextView: TextView = itemView.findViewById(R.id.group_description)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(group: Group){
            this.group = group
            nameTextView.text = this.group.name
            descrTextView.text = this.group.description
        }

        override fun onClick(v: View?) {
            callbacks?.onGroupSelected(group)
        }
    }

    /**
     * ADAPTER
     */
    private inner class GroupAdapter(
        private val listOfGroups : ArrayList<Group>
    ): RecyclerView.Adapter<GroupHolder>(){



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupHolder {
            val view = layoutInflater.inflate(R.layout.list_group_item, parent, false)
            return GroupHolder(view)
        }

        override fun getItemCount(): Int {
            return listOfGroups.size
        }

        override fun onBindViewHolder(
            holder: GroupHolder,
            position: Int
        ) {
           val group = listOfGroups[position]
            holder.bind(group)
        }

    }


    companion object{
        fun newInstance(): GroupListFragment{
            return GroupListFragment()
        }

        const val TAG = "GroupListFragment"
        const val GROUP_NAME = "groupName"
    }














}