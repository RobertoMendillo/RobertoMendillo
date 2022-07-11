package it.unisannio.studenti.mendillo.teamwork

import android.content.Context
import android.os.Bundle
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import it.unisannio.studenti.mendillo.teamwork.databinding.FragmentChatBinding
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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

    private lateinit var group: String
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

        group = arguments?.getSerializable(GroupListFragment.GROUP_NAME) as String
        Log.d(TAG, "args bundle group name: ${group}")
        user = arguments?.getSerializable("User") as String

        firestore= FirebaseDatabase.getInstance("https://teamwork-2110e-default-rtdb.europe-west1.firebasedatabase.app")
        messagesRef = firestore.reference.child(GROUPS).child("${group}").child("messages")

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
        fun newInstance(groupName: String?): ChatFragment{
            val args = Bundle().apply {
                putSerializable(GroupListFragment.GROUP_NAME, groupName)
            }
            return ChatFragment().apply {
                arguments = args
            }
        }

        const val TAG = "ChatFragment"
        const val VIEW_TYPE_TEXT = 1
        const val MESSAGES = "messages"
        const val GROUPS = "groups"
    }
}