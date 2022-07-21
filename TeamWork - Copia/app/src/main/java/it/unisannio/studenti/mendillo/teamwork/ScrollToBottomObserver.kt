package it.unisannio.studenti.mendillo.teamwork

import androidx.recyclerview.widget.RecyclerView

class ScrollToBottomObserver(
    private val recycler: RecyclerView) : RecyclerView.AdapterDataObserver(){
    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        super.onItemRangeChanged(positionStart, itemCount)
        recycler.scrollToPosition(positionStart)
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
        super.onItemRangeChanged(positionStart, itemCount, payload)
        recycler.scrollToPosition(positionStart)
    }
}