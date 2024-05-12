package com.app.simon

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class PostAdapter(private val mData: List<CommentData>,private val user: User, private val context: Context) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mData[position]
        holder.bind(item, position, user)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    class ViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
        private var db = FirebaseFirestore.getInstance()

        private val textComment: TextView = itemView.findViewById(R.id.tvDescComentario)
        private val dataComment: TextView = itemView.findViewById(R.id.tvDataComentario)
        private val autorComment: TextView = itemView.findViewById(R.id.tvNomeComentario)
        private val btnVotos: AppCompatToggleButton = itemView.findViewById(R.id.btnUpvote)
        private val monComment: TextView = itemView.findViewById(R.id.tvOP)
        private val context: Context = context


        fun bind(item: CommentData, position: Int, user: User) {
            textComment.text = item.texto
            dataComment.text = item.data
            autorComment.text = item.autor
            btnVotos.isChecked = false
            btnVotos.text = item.qtdVotos.toString()

            if(item.monitor == "true") {
                monComment.text = "Monitor"
            }

            btnVotos.setOnCheckedChangeListener{ buttonView, isChecked ->
                if(isChecked) {
                    item.qtdVotos += 1
                    btnVotos.textOn = item.qtdVotos.toString()


                    println(user.monitor)
                    if(user.monitor == "true") {
                        db.collection("Comentarios").document(item.id)
                            .update("qtdVotos", item.qtdVotos, "aprovado", "true")
                    } else {
                        db.collection("Comentarios").document(item.id)
                            .update("qtdVotos", item.qtdVotos)
                    }
                } else {
                    item.qtdVotos -= 1
                    btnVotos.textOff = item.qtdVotos.toString()

                    if(user.monitor == "true") {
                        db.collection("Comentarios").document(item.id)
                            .update("qtdVotos", item.qtdVotos, "aprovado", "false")
                    } else {
                        db.collection("Comentarios").document(item.id)
                            .update("qtdVotos", item.qtdVotos)
                    }
                }
            }
        }
    }
}