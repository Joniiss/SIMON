package com.app.simon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.simon.databinding.FragmentForumBinding
import com.app.simon.databinding.FragmentPostBinding


class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: PostAdapter
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //fazer tudo aqui

        mRecyclerView = binding.rvListaComentario
        mRecyclerView.setLayoutManager(LinearLayoutManager(this.context));

        val teste = CommentData("Teste Post")

        val teste2 = CommentData("Teste Post Lista matei minha familia e comecei um culto  sem querer familia tmj ")

        val lista: MutableList<CommentData> = emptyList<CommentData>().toMutableList()

        lista.add(teste)
        lista.add(teste2)

        mAdapter = PostAdapter(lista, this.requireContext())
        mRecyclerView.adapter = mAdapter



    }
}