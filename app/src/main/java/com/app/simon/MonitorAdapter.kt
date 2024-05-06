package com.app.simon

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
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
        private val Monitor: TextView = itemView.findViewById(R.id.tvNomeMonitor)
        private val Sala: TextView = itemView.findViewById(R.id.tvLocal)
        private val Horario: TextView = itemView.findViewById(R.id.tvHorario)
        private val Imagem: CircleImageView = itemView.findViewById(R.id.ivFotoMonitor)
        private val Status: CardView = itemView.findViewById(R.id.ivStatusMonitor)
        private val context: Context = context

        fun bind(item: MonitorData) {
            Monitor.text = item.nome
            Sala.text = "Local: ${item.sala} - ${item.predio}"
            Horario.text = item.horario

            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.getReferenceFromUrl(item.foto)

            storageRef.downloadUrl.addOnSuccessListener {
                Picasso.with(context).load(it).fit().centerInside().into(Imagem)
            }
            //Picasso.with(context).load(R.drawable.track).into(Status);
        }
    }
}