package it.unisannio.studenti.mendillo.teamwork

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * ADAPTER
 */
class MemberAdapter(
    private val participants: ArrayList<String?>,
    private val layoutInflater: LayoutInflater
):  RecyclerView.Adapter<MemberHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberHolder {
        val view = layoutInflater.inflate(R.layout.fragment_member_item, parent, false)
        return MemberHolder(view)
    }

    override fun getItemCount(): Int {
        return participants.size
    }

    override fun onBindViewHolder(
        holder: MemberHolder,
        position: Int
    ) {
        var participant = participants[position]
        holder.bind(participant!!)

    }
}

/**
 * HOLDER
 */
class MemberHolder(view: View): RecyclerView.ViewHolder(view){

    private lateinit var attende: String
    private val memberEmail: TextView = view.findViewById(R.id.member_email)

    fun bind(participant: String){
        this.attende = attende
        memberEmail.text = this.attende
    }

}