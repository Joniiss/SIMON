package com.app.simon

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.simon.databinding.FragmentMateriasMonitoradasBinding
import com.app.simon.databinding.FragmentNewCommentBinding
import com.app.simon.databinding.FragmentNewPostBinding

class MateriasMonitoradasFragment : Fragment() {

    private var _binding: FragmentMateriasMonitoradasBinding? = null
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MateriaMonitoradaAdapter

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMateriasMonitoradasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = activity?.getIntent()?.getExtras()?.getSerializable("user") as User

        mRecyclerView = binding.rvMateriasMonitoradas
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        var mItems: MutableList<SubjectData> = emptyList<SubjectData>().toMutableList()

        for(item : String in user.materias) {
            val itemSubject = SubjectData(user, item)
            mItems.add(itemSubject)
        }

        val testeItem = SubjectData(user, "Teste Materia Monitorada")

        mItems.add(testeItem)

        mAdapter = MateriaMonitoradaAdapter(mItems)
        mRecyclerView.adapter = mAdapter

        binding.btnMaterias.setOnClickListener {
            findNavController().navigate(R.id.action_materiasMonitoradasFragment_to_mainFragment)
        }
    }

}