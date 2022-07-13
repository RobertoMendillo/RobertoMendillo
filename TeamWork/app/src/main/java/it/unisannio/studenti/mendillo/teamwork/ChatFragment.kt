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
import it.unisannio.studenti.mendillo.teamwork.databinding.FragmentChatBinding
import it.unisannio.studenti.mendillo.teamwork.model.Group

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
    private lateinit var firestore: FirebaseDatabase
    //private lateinit var collection: CollectionReference
    private lateinit var messagesRef: DatabaseReference

    private lateinit var manager: LinearLayoutManager

    private lateinit var binding: FragmentChatBinding
    private lateinit var chatMessageRecyclerView: RecyclerView
    private lateinit var adapter: ChatMessageAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        group = arguments?.getSerializable("group") as Group
        Log.d(TAG, "args bundle group name: ${group.name}")

        user = arguments?.getSerializable("User") as String

        firestore= FirebaseDatabase.getInstance("https://teamwork-2110e-default-rtdb.europe-west1.firebasedatabase.app")
        messagesRef = firestore.reference.child(GROUPS).child("${group.id}").child("messages")

        val options = FirebaseRecyclerOptions.Builder<ChatMessage>()
            .setQuery(messagesRef, ChatMessage::class.java)
            .build()
        adapter = ChatMessageAdapter(options, user)
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
                messagesRef.push()
                    .setValue(chatMessage)
                    .addOnSuccessListener {
                        Log.d(TAG, "Message send with success")
                        Toast.makeText(context, "Sent!", Toast.LENGTH_SHORT).show()
                    }
                binding.editTextChat.setText("")
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
                val fragment = GroupCreationFragment()
                fragment.arguments = bundle
                parentFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
                return true
            }
            R.id.delete_group ->{
                var auth = FirebaseAuth.getInstance().currentUser?.email
                if (auth.equals(group.owner) && group.members?.size == 1){
                    firestore.reference.child(GROUPS).child("${group.id}").removeValue()
                    Log.d(TAG, "DELETE GROUP")
                    parentFragmentManager.beginTransaction().remove(this).commit()
                    parentFragmentManager.beginTransaction().add(R.id.fragment_container, GroupListFragment()).commit()
                }
                else if(!auth.equals(group.owner)){
                    Toast.makeText(context, "PERMISSION DENIED: you are not the owner!", Toast.LENGTH_SHORT ).show()
                }else{
                    Toast.makeText(context, "${PERMISSION_DENIED}: The group is not empty", Toast.LENGTH_SHORT).show()
                }

                //TODO DELETE GROUP
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    /**
     * HOLDER
     */
    private inner class ChatMessageHodler(view: View): RecyclerView.ViewHolder(view){

        private lateinit var message: ChatMessage

        private val userNameTextView: TextView = itemView.findViewById(R.id.user_name)
        private val messageTextView: TextView = itemView.findViewById(R.id.message)

        fun bind(chatMessage: ChatMessage){
            this.message = chatMessage
            userNameTextView.text = this.message.userName
            messageTextView.text = this.message.message
        }

    }

    /**
     * ADAPTER
     */
    private inner class ChatMessageAdapter(
        private val options: FirebaseRecyclerOptions<ChatMessage>,
        private val currentUserName: String?
    ): FirebaseRecyclerAdapter<ChatMessage, RecyclerView.ViewHolder>(options){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageHodler {
            val view = layoutInflater.inflate(R.layout.chat_message_item, parent, false)
            return ChatMessageHodler(view)
        }

        override fun getItemViewType(position: Int): Int {
            return VIEW_TYPE_TEXT
        }

        override fun onBindViewHolder(
            holder: RecyclerView.ViewHolder,
            position: Int,
            model: ChatMessage
        ) {
            if(options.snapshots[position].message != null){
                (holder as ChatMessageHodler).bind(model)
            }

        }

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
        const val VIEW_TYPE_TEXT = 1
        const val MESSAGES = "messages"
        const val GROUPS = "groups"
        const val GROUP_NAME = "groupName"
        const val PERMISSION_DENIED = "PERMISSION DENIED"
    }
}