package com.app.simon

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class MonitorAdapter(private val mData: List<MonitorData>, private val context: Context) : RecyclerView.Adapter<MonitorAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_monitor, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mData[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    class ViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
        private val monitor: TextView = itemView.findViewById(R.id.tvNomeMonitor)
        private val sala: TextView = itemView.findViewById(R.id.tvLocal)
        private val horario: TextView = itemView.findViewById(R.id.tvHorario)
        private val imagem: CircleImageView = itemView.findViewById(R.id.ivFotoMonitor)
        private val status: CardView = itemView.findViewById(R.id.ivStatusMonitor)
        private val context: Context = context

        fun bind(item: MonitorData) {
            monitor.text = item.nome
            sala.text = "Local: ${item.sala} - ${item.predio}"
            horario.text = item.horario

            if(item.status == "true") {
                status.setCardBackgroundColor(Color.parseColor("#48d41e"))
            }

            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.getReferenceFromUrl(item.foto)

            storageRef.downloadUrl.addOnSuccessListener {
                Picasso.with(context).load(it).fit().centerInside().into(imagem)
            }
            //Picasso.with(context).load(R.drawable.track).into(Status);
        }
    }
}