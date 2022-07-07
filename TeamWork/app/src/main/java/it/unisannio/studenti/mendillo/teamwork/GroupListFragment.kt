package it.unisannio.studenti.mendillo.teamwork

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class GroupListFragment: Fragment() {

    // Interfaccia richiesta dall'activity ospitante il fragment
    interface Callbacks{
        fun onGroupSelected(groupId: UUID)
    }
    private var callbacks: Callbacks? = null

    private lateinit var groupListRecycleView: RecyclerView
   // private var adapter : GroupAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // costruisce l'albero dei controlli della vista contenente la recycle view dei gruppi
        val view = inflater.inflate(R.layout.fragment_group_list, container, false)

        // recupera la recycle view
        groupListRecycleView = view.findViewById(R.id.groupListRecycleView) as RecyclerView
        // recupera il layout manager associato al contesto dell'activity
        groupListRecycleView.layoutManager = LinearLayoutManager(context)
        //groupListRecycleView.adapter = adapter
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
       // callbacks = context as Callbacks?
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {
        fun newInstance(): GroupListFragment{
            return GroupListFragment()
        }
    }
}