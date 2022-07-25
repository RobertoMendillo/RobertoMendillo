package it.unisannio.studenti.mendillo.teamwork

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import it.unisannio.studenti.mendillo.teamwork.model.ChatMessage

class ScrollToBottomObserver(
    private val recycler: RecyclerView,
    private var messages: ArrayList<ChatMessage>
    ) : AdapterDataObserver(){

    override fun onChanged() {
        super.onChanged()
        recycler.scrollToPosition(messages.size-1)
    }
}