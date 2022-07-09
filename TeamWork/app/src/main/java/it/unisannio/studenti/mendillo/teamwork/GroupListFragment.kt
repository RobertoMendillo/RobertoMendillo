package it.unisannio.studenti.mendillo.teamwork

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import it.unisannio.studenti.mendillo.teamwork.databinding.FragmentGroupListBinding
import it.unisannio.studenti.mendillo.teamwork.databinding.ListGroupItemBinding
import java.io.Serializable

private const val ARG_USER_ID = "user_id"

class GroupListFragment : Fragment() {

    private lateinit var user: String
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var groups: ArrayList<Group>

    private var adapter: GroupAdapter? = null
    private lateinit var groupRecyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        var user: Serializable? = savedInstanceState?.getSerializable("user")
        user = user.toString()
        collectGroups()
    }

    private fun collectGroups() {
        val collection = firestore.collection("groups")
        groups = ArrayList()
        collection.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
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
        val view = inflater.inflate(R.layout.fragment_group_list, container, false)
        groupRecyclerView = view.findViewById(R.id.groupListRecycleView) as RecyclerView
        groupRecyclerView.layoutManager = LinearLayoutManager(context)
        groupRecyclerView.adapter = adapter
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // callbacks = context as Callbacks?
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectGroups()
        updateUI()
        var addGroupButton: FloatingActionButton = view.findViewById(R.id.add_group_button) as FloatingActionButton
        addGroupButton.setOnClickListener {
            val fragment = GroupCreationFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack("GroupCrationFragment")
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "OnResume")
    }

    private fun updateUI() {
        adapter = GroupAdapter(groups)
        groupRecyclerView.adapter = adapter
    }



    /**
     * ViewHolder for List Group Item
     */
    private inner class GroupHolder(view: View) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var group: Group

        val groupNameView: TextView = itemView.findViewById(R.id.group_name_label)
        val groupDescriptionView: TextView = itemView.findViewById(R.id.group_description)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(group: Group) {
            this.group = group
            groupNameView.text = group.name
            groupDescriptionView.text = group.description
        }

        override fun onClick(v: View?) {
            Toast.makeText(context, "${group.name} pressed", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Adapter for Group List Recycle View
     */
    private inner class GroupAdapter(var groups: ArrayList<Group>) :
        RecyclerView.Adapter<GroupHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupHolder {
            val view = layoutInflater.inflate(R.layout.list_group_item, parent, false)
            return GroupHolder(view)
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
        fun newInstance(): GroupListFragment {
            return GroupListFragment()
        }

        const val TAG: String = "GroupListFragment"
    }
}