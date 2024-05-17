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
import com.app.simon.databinding.FragmentNewPostBinding

class NewPostFragment : Fragment() {

    private var _binding: FragmentNewPostBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etAddTittle.addTextChangedListener {
            if(binding.etAddComment.text.isNotEmpty() && binding.etAddTittle.text.isNotEmpty()){
                binding.btnAddPost.setBackgroundResource(R.drawable.rounded_publish)
                binding.btnAddPost.alpha = 1.0f
                binding.btnAddPost.isClickable = true
            }else{
                binding.btnAddPost.setBackgroundResource(R.drawable.rounded_disabled_public)
                binding.btnAddPost.alpha = 0.69f
            }
        }

        binding.etAddComment.addTextChangedListener {
            if(binding.etAddComment.text.isNotEmpty() && binding.etAddTittle.text.isNotEmpty()){
                binding.btnAddPost.setBackgroundResource(R.drawable.rounded_publish)
                binding.btnAddPost.alpha = 1.0f
                binding.btnAddPost.isClickable = true
            }else{
                binding.btnAddPost.setBackgroundResource(R.drawable.rounded_disabled_public)
                binding.btnAddPost.alpha = 0.69f
            }
        }

        binding.btnAddPost.setOnClickListener {
            findNavController().popBackStack()
        }


    }

}