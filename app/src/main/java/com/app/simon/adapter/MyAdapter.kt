package com.app.simon.adapter
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.app.simon.R
import com.app.simon.SubjectActivity
import com.app.simon.data.SubjectData


class MyAdapter(private val mData: MutableList<SubjectData>, private val myFragment: Fragment) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subject, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mData[position]
        holder.bind(item, myFragment)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.tvNomeMateria)

        fun bind(item: SubjectData, fragment: Fragment) {
            textView.text = item.materia

            itemView.setOnClickListener{

//                val iSubject = Intent(itemView.context, SubjectActivity::class.java)
//                iSubject.putExtra("user", item.user)
//                iSubject.putExtra("materiaSub", item.materia)
//                itemView.context.startActivity(iSubject)
                val bundle = bundleOf(
                    "user" to item.user,
                    "materiaSub" to item.materia
                )
                NavHostFragment.findNavController(fragment)
                    .navigate(R.id.action_mainFragment_to_subjectFragment2, bundle)
            }
        }
    }
}