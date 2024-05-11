package com.app.simon

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView

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

        fun bind(item: ForumData, fragment: Fragment) {
            tituloPost.text = item.nome

            itemView.setOnClickListener{
                findNavController(fragment).navigate(R.id.action_forumFragment_to_postFragment)
            }
        }
    }
}