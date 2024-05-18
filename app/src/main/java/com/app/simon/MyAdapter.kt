package com.app.simon
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView


class MyAdapter(private val mData: MutableList<SubjectData>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subject, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mData[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.tvNomeMateria)

        fun bind(item: SubjectData) {
            textView.text = item.materia

            itemView.setOnClickListener{

                val iSubject = Intent(itemView.context, SubjectActivity::class.java)
                iSubject.putExtra("user", item.user)
                iSubject.putExtra("materiaSub", item.materia)
                itemView.context.startActivity(iSubject)
            }
        }
    }
}
