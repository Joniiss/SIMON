package com.app.simon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.app.simon.R
import com.app.simon.data.UserData
import com.app.simon.data.CommentData
import com.app.simon.data.ForumData
import com.google.firebase.firestore.FirebaseFirestore


class PostAdapter(private val mData: List<CommentData>, private val user: UserData, private val post: ForumData, private val context: Context) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mData[position]
        holder.bind(item, position, user, post)
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
        private val verificado: ImageView = itemView.findViewById(R.id.tvCheckModeradorComment)
        private val context: Context = context


        fun bind(item: CommentData, position: Int, user: UserData, post: ForumData) {
            textComment.text = item.texto
            dataComment.text = item.data
            autorComment.text = item.autor
            btnVotos.isChecked = false
            btnVotos.text = item.qtdVotos.toString()

            if(item.monitor == "true") {
                monComment.text = "Monitor"
                verificado.alpha = 1.0f
            }

            if(item.aprovado == "true") {
                verificado.alpha = 1.0f
            }

            btnVotos.setOnCheckedChangeListener{ buttonView, isChecked ->
                if(isChecked) {
                    item.qtdVotos += 1
                    btnVotos.textOn = item.qtdVotos.toString()


                    println(user.monitor)
                    if(user.monitor != "") {
                        db.collection("Comentarios").document(item.id)
                            .update("qtdVotos", item.qtdVotos, "aprovado", "true")
                        verificado.alpha = 1.0f
                        db.collection("Forum").document(post.id)
                            .update("aprovado", "true")
                    } else {
                        db.collection("Comentarios").document(item.id)
                            .update("qtdVotos", item.qtdVotos)
                    }
                } else {
                    item.qtdVotos -= 1
                    btnVotos.textOff = item.qtdVotos.toString()

                    if(user.monitor != "") {
                        db.collection("Comentarios").document(item.id)
                            .update("qtdVotos", item.qtdVotos, "aprovado", "false")
                        verificado.alpha = 0.0f
                        db.collection("Forum").document(post.id)
                            .update("aprovado", "false")
                    } else {
                        db.collection("Comentarios").document(item.id)
                            .update("qtdVotos", item.qtdVotos)
                    }
                }
            }
        }
    }
}