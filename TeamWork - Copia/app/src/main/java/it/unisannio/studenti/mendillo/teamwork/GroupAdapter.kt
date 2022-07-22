package it.unisannio.studenti.mendillo.teamwork

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.unisannio.studenti.mendillo.teamwork.model.Group

/**
 * HOLDER
 */
class GroupHolder(view: View, private val callbacks: GroupListFragment.Callbacks): RecyclerView.ViewHolder(view), View.OnClickListener{

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
        this.callbacks.onGroupSelected(group)
    }
}

/**
 * ADAPTER
 */
class GroupAdapter(
    private var listOfGroups : ArrayList<Group>,
    private val layoutInflater: LayoutInflater,
    private val callbacks: GroupListFragment.Callbacks
): RecyclerView.Adapter<GroupHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupHolder {
        val view = layoutInflater.inflate(R.layout.list_group_item, parent, false)
        return GroupHolder(view, callbacks)
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

    fun setContents(data: ArrayList<Group>){
        this.listOfGroups = data
    }

}