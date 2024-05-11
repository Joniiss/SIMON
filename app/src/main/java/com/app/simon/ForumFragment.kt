package com.app.simon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.simon.databinding.FragmentForumBinding
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ForumFragment : Fragment() {

    private var _binding: FragmentForumBinding? = null
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: ForumAdapter
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForumBinding.inflate(inflater, container, false)
        return binding.root

        //não alterar onCreateView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //fazer tudo aqui

        mRecyclerView = binding.rvListaForum
        mRecyclerView.setLayoutManager(LinearLayoutManager(this.context));

        val teste = ForumData("Teste Post")

        val teste2 = ForumData("Teste Post Lista matei minha familia e comecei um culto  sem querer familia tmj ")

        val lista: MutableList<ForumData> = emptyList<ForumData>().toMutableList()

        lista.add(teste)
        lista.add(teste2)

        mAdapter = ForumAdapter(lista, this.requireContext())
        mRecyclerView.adapter = mAdapter

        binding.btnMonitores.setOnClickListener {
            findNavController().navigate(R.id.action_forumFragment_to_subjectFragment)
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}