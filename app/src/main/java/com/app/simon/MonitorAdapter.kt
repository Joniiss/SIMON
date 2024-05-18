package com.app.simon

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File


class MonitorAdapter(private val mData: List<MonitorData>, private val context: Context, private val myFragment: Fragment) : RecyclerView.Adapter<MonitorAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_monitor, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mData[position]
        holder.bind(item, myFragment)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    class ViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
        private val monitor: TextView = itemView.findViewById(R.id.tvNomeMonitor)
        private val sala: TextView = itemView.findViewById(R.id.tvLocal)
        private val horario: TextView = itemView.findViewById(R.id.tvHorario)
        private val imagem: CircleImageView = itemView.findViewById(R.id.ivFotoMonitor)
        private val status: CardView = itemView.findViewById(R.id.ivStatusItemMonitor)
        private val context: Context = context

        fun bind(item: MonitorData, fragment: Fragment) {
            monitor.text = item.nome
            sala.text = "Local: ${item.sala} - ${item.predio}"
            horario.text = item.horario


            if(item.status == "true") {
                status.setCardBackgroundColor(Color.parseColor("#48d41e"))
            }

            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.getReferenceFromUrl(item.foto)

            storageRef.downloadUrl.addOnSuccessListener {
                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
                Picasso.with(context).load(it).fit().centerInside().into(imagem)

            }
/*
            val storage = FirebaseStorage.getInstance()
            val storageRef1 = storage.getReferenceFromUrl(item.foto)
            val localFile1 = File.createTempFile("images", "jpg")

            storageRef1.getFile(localFile1).addOnSuccessListener {
                Picasso.with(context).load("file:" + localFile1.absolutePath).fit().centerInside().into(imagem)
            }.addOnFailureListener {
                Toast.makeText(context, "Erro ao baixar imagem: ${it.message}", Toast.LENGTH_LONG).show()
            }
*/
            itemView.setOnClickListener{
                val bundle = bundleOf("monitor" to item)
                NavHostFragment.findNavController(fragment)
                    .navigate(R.id.action_subjectFragment_to_perfilMonitorFragment, bundle)
            }
            //Picasso.with(context).load(R.drawable.track).into(Status);
        }
    }
}