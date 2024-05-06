package com.app.simon

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class MonitorAdapter(private val mData: MutableList<MonitorData>) : RecyclerView.Adapter<MonitorAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_monitor, parent, false)
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
        private val Monitor: TextView = itemView.findViewById(R.id.tvNomeMonitor)
        private val Sala: TextView = itemView.findViewById(R.id.tvLocal)
        private val Horario: TextView = itemView.findViewById(R.id.tvHorario)

        fun bind(item: MonitorData) {
            Monitor.text = item.nome
            Sala.text = item.local
            Horario.text = item.horario
        }
    }
}