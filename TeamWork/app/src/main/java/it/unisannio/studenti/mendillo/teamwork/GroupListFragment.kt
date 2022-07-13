package it.unisannio.studenti.mendillo.teamwork

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
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

    private lateinit var database: FirebaseDatabase

    private lateinit var binding: FragmentGroupListBinding




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        database = FirebaseDatabase.getInstance("https://teamwork-2110e-default-rtdb.europe-west1.firebasedatabase.app")
        var groupsRef = database.reference.child("groups")
        val options = FirebaseRecyclerOptions.Builder<Group>()
            .setQuery(groupsRef, Group::class.java)
            .build()
        adapter = GroupAdapter(options)
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
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
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
            }else -> {
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
        private val options: FirebaseRecyclerOptions<Group>
    ): FirebaseRecyclerAdapter<Group, RecyclerView.ViewHolder>(options){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupHolder {
            val view = layoutInflater.inflate(R.layout.list_group_item, parent, false)
            return GroupHolder(view)
        }

        override fun getItemViewType(position: Int): Int {
            return 1
        }

        override fun onBindViewHolder(
            holder: RecyclerView.ViewHolder,
            position: Int,
            model: Group
        ) {
            if(options.snapshots[position].owner != null){
                Log.d(TAG, "OnBindViewHolder")
                (holder as GroupHolder).bind(model)
                Log.d(TAG, "OnBindViewHolder")
            }


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