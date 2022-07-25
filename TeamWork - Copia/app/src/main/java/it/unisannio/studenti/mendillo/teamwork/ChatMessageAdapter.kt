package it.unisannio.studenti.mendillo.teamwork

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.unisannio.studenti.mendillo.teamwork.model.ChatMessage


/**
 * HOLDER
 */
class ChatMessageHodler(view: View): RecyclerView.ViewHolder(view){

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
class ChatMessageAdapter(
    private val messages: List<ChatMessage>,
    private val layoutInflater: LayoutInflater
): RecyclerView.Adapter<ChatMessageHodler>() {

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
