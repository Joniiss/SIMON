package com.app.simon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.app.simon.R
import com.app.simon.data.ForumData

class ForumAdapter(private val mData: List<ForumData>, private val context: Context, private val myFragment: Fragment) : RecyclerView.Adapter<ForumAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_forum, parent, false)
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
        private val tituloPost: TextView = itemView.findViewById(R.id.tvTituloPost)
        private val qtdComent: TextView = itemView.findViewById(R.id.tvQuantComentario)
        private val data: TextView = itemView.findViewById(R.id.tvData)
        private val verificado: ImageView = itemView.findViewById(R.id.tvCheckModerador)

        fun bind(item: ForumData, fragment: Fragment) {
            tituloPost.text = item.titulo
            qtdComent.text = "${item.qtdComent} comentários"
            data.text = item.data

            if(item.aprovado == "false") {
                verificado.alpha = 0.0f
            }

            itemView.setOnClickListener{
                val bundle = bundleOf("post" to item)
                findNavController(fragment).navigate(R.id.action_forumFragment_to_postFragment, bundle)
            }
        }
    }
}