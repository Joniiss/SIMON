package com.app.simon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.simon.adapter.MyAdapter
import com.app.simon.data.SubjectData
import com.app.simon.data.UserData
import com.app.simon.databinding.FragmentMainBinding


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MyAdapter
    private lateinit var mItems: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = activity?.getIntent()?.getExtras()?.getSerializable("user") as UserData


        mRecyclerView = binding.rvLista
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        var mItems: MutableList<SubjectData> = emptyList<SubjectData>().toMutableList()



        for(item : String in user.materias) {
            val itemSubject = SubjectData(user, item)
            mItems.add(itemSubject)
        }

        mAdapter = MyAdapter(mItems, this)
        mRecyclerView.adapter = mAdapter

        binding.btnMateriasMonitoradas.setOnClickListener {
            val bundle = bundleOf("post" to mItems)
            findNavController().navigate(R.id.action_mainFragment_to_materiasMonitoradasFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}