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
        group.messages?.clear()
        messagesRef.orderBy("date")/*.get().addOnSuccessListener {
            it.forEach{ doc ->
               var message = doc.toObject(ChatMessage::class.java)
                group.messages?.add(message)
                Log.d(TAG, group.messages.toString())
            }
            Log.d(TAG, "CURRENT MESSGES:"+ group.messages)
        }.addOnCompleteListener {
            this.onResume()
        }*/

        /*messagesRef*/
            .addSnapshotListener { value, error ->
            if(error != null){
                Log.w(TAG, "Error occurred", error)
            }else{
                group.messages?.clear()
               for (doc in value!!){
                   group.messages?.add(doc.toObject(ChatMessage::class.java))
               }
            }
        }

        adapter = ChatMessageAdapter(group.messages!!)
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

        adapter.registerAdapterDataObserver(
            ScrollToBottomObserver(binding.recyclerViewChat)
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
        private val messages: List<ChatMessage>
    ): RecyclerView.Adapter<ChatMessageHodler>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageHodler {
            val view = layoutInflater.inflate(R.layout.chat_message_item, parent, false)
            return ChatMessageHodler(view)
        }

        override fun getItemCount(): Int {
            return messages.size
        }

        override fun onBindViewHolder(
            holder: ChatMessageHodler,
            position: Int
        ) {
            val message = messages[position]
            holder.bind(message)
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
        const val PERMISSION_DENIED = "PERMISSION DENIED"
    }
}