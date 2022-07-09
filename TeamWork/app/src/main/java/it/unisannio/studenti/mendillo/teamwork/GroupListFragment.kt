package it.unisannio.studenti.mendillo.teamwork

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class GroupListFragment: Fragment(){

    private lateinit var groupRecycleView: RecyclerView
    private var adapter: GroupAdapter? = null

    private val groupListViewModel: GroupListViewModel by lazy {
        ViewModelProvider(this).get(GroupListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Total groups: ${groupListViewModel.groups.size}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_group_list, container, false)
        groupRecycleView = view.findViewById(R.id.groupListRecycleView) as RecyclerView
        groupRecycleView.layoutManager = LinearLayoutManager(context)

        updateUI()

        return view
    }

    

    private fun updateUI(){
        val groups = groupListViewModel.groups
        adapter = GroupAdapter(groups)
        groupRecycleView.adapter = adapter
    }

    /**
     * HOLDER
     */
    private inner class GroupHolder(view: View): RecyclerView.ViewHolder(view){

        val nameTextView: TextView = itemView.findViewById(R.id.group_name_label)
        val descrTextView: TextView = itemView.findViewById(R.id.group_description)

    }

    /**
     * ADAPTER
     */
    private inner class GroupAdapter(var groups: ArrayList<Group>): RecyclerView.Adapter<GroupHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupHolder {
            val view = layoutInflater.inflate(R.layout.list_group_item, parent, false)
            return GroupHolder(view)
        }

        override fun getItemCount(): Int {
            return groups.size
        }

        override fun onBindViewHolder(holder: GroupHolder, position: Int) {
            val group = groups[position]
            holder.apply {
                nameTextView.text = group.name
                descrTextView.text = group.description
            }
        }

    }


    companion object{
        fun newInstance(): GroupListFragment{
            return GroupListFragment()
        }

        const val TAG = "GroupListFragment"
    }














}