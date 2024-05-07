package com.app.simon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.simon.databinding.FragmentForumBinding
import androidx.navigation.fragment.findNavController

class ForumFragment : Fragment() {

    private var _binding: FragmentForumBinding? = null

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

        binding.btnMonitores.setOnClickListener {
            findNavController().navigate(R.id.action_forumFragment_to_subjectFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}