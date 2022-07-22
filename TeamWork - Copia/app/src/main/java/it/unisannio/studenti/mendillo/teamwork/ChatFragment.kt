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
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import it.unisannio.studenti.mendillo.teamwork.databinding.FragmentChatBinding
import it.unisannio.studenti.mendillo.teamwork.model.Group
import java.lang.reflect.Type

internal class WrapContentLinearLayoutManager(c: Context?) : LinearLayoutManager(c) {

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            Log.e("TAG", "meet a IOOBE in RecyclerView")
        }
    }
}

class ChatFragment: Fragment() {

    private lateinit var group: Group
    private lateinit var user: String
    private lateinit var firestore: FirebaseFirestore
    //private lateinit var collection: CollectionReference
    private lateinit var messagesRef: CollectionReference

    private lateinit var manager: LinearLayoutManager

    private lateinit var binding: FragmentChatBinding
    private lateinit var chatMessageRecyclerView: RecyclerView
    private lateinit var adapter: ChatMessageAdapter
    private lateinit var uid: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        group = arguments?.getSerializable("group") as Group
        arguments?.remove("group")
        Log.d(TAG, "args bundle group name: ${group.name}")
        uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        user = arguments?.getSerializable(MainActivity.USER) as String

        firestore= FirebaseFirestore.getInstance()
        messagesRef = firestore.collection(MainActivity.GROUPS).document("${group.id}").collection("messages")
        // pulisco la cache
        group.messages?.clear()

        adapter = ChatMessageAdapter(group.messages!!, layoutInflater)

        // recupero i messaggi dal database
        group.messages = GroupRepository.get().getMessages(group, adapter)
        manager = WrapContentLinearLayoutManager(context)
        manager.stackFromEnd = true

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        binding.recyclerViewChat.layoutManager = manager
        binding.recyclerViewChat.adapter = adapter
        chatMessageRecyclerView = binding.recyclerViewChat
        chatMessageRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.editTextChat.addTextChangedListener(SendButtonObserver(binding.sendMessageButton))

        // Scorri verso il basso quando arriva un nuovo messaggio
        // Vedere MyScrollToBottomObserver per i dettagli
        adapter.registerAdapterDataObserver(
            ScrollToBottomObserver(binding.recyclerViewChat, group.messages!!)
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var sendButton: Button = binding.sendMessageButton
        sendButton.setOnClickListener{
            val message = binding.editTextChat.text.toString()
            if(message != "") {
                val chatMessage = ChatMessage(
                    user,
                    message
                )
                group.messages?.add(chatMessage)
                firestore.collection(MainActivity.GROUPS)
                    .document("${group.id}")
                    .collection("messages")
                    .add(chatMessage)
                    .addOnSuccessListener {
                        Log.d(TAG, "Message send with success")
                        Toast.makeText(context, "Sent!", Toast.LENGTH_SHORT).show()
                    }
                binding.editTextChat.setText("")
                adapter.notifyDataSetChanged()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_chat, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId){
            R.id.modify_group ->{
                val bundle = Bundle()
                bundle.putSerializable("group", group)
                var fragment: Fragment? = null
                if(uid == group.owner){
                    fragment = GroupCreationFragment()
                    fragment.arguments = bundle
                }else{
                    fragment = GroupFragment()
                    fragment.arguments = bundle
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()

                return true
            }
            R.id.delete_group ->{
                var auth = FirebaseAuth.getInstance().currentUser?.uid
                var email = FirebaseAuth.getInstance().currentUser?.email
                if (auth.equals("${group.owner}") && group.members?.size == 1){
                    GroupRepository.get().deleteGroup(email!!, group)
                    parentFragmentManager.beginTransaction().remove(this).commit()
                    parentFragmentManager.beginTransaction().add(R.id.fragment_container, GroupListFragment()).commit()
                }
                else if(!auth.equals("${group.owner}")){
                    Toast.makeText(context, "PERMISSION DENIED: you are not the owner!", Toast.LENGTH_SHORT ).show()
                }else{
                    Toast.makeText(context, "${PERMISSION_DENIED}: The group is not empty", Toast.LENGTH_SHORT).show()
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        adapter.notifyDataSetChanged()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    companion object{
        fun newInstance(group: Group): ChatFragment{
            val args = Bundle().apply {
                putSerializable(GroupListFragment.GROUP_NAME, group.name)
            }
            return ChatFragment().apply {
                arguments = args
            }
        }

        const val TAG = "ChatFragment"
        const val PERMISSION_DENIED = "PERMISSION DENIED"
    }
}