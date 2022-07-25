package it.unisannio.studenti.mendillo.teamwork

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import it.unisannio.studenti.mendillo.teamwork.databinding.FragmentGroupBinding
import it.unisannio.studenti.mendillo.teamwork.model.Group

class GroupFragment: Fragment() {

    private lateinit var binding: FragmentGroupBinding
    private lateinit var membersRecycleView: RecyclerView
    private lateinit var adapter: MemberAdapter

    private lateinit var group: Group

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        this.group = arguments?.get("group") as Group

        var listOfParticipant : ArrayList<String?> = ArrayList()
        group.members?.forEach { entry ->
            listOfParticipant.add(entry.key)
        }
        adapter = MemberAdapter(listOfParticipant, layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentGroupBinding.inflate(inflater, container, false)
        binding.groupName.text = group.name.toString()
        membersRecycleView = binding.groupMembersRecycle
        membersRecycleView.adapter = adapter

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_group, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.quit_group -> {
                GroupRepository.get().removeMemberToGroup(FirebaseAuth.getInstance().currentUser?.email!!, group)
                parentFragmentManager.beginTransaction().replace(R.id.fragment_container, GroupListFragment()).commit()
                true
            }else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }
}