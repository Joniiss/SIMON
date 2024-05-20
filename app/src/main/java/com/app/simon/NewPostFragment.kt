package com.app.simon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.app.simon.databinding.FragmentNewPostBinding
import com.app.simon.data.ForumData
import com.app.simon.data.UserData
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate

class NewPostFragment : Fragment() {

    private var db = FirebaseFirestore.getInstance()

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

        val user = arguments?.getSerializable("user") as UserData
        val materia = arguments?.getString("materiaSub")

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
            val data = LocalDate.now()
            val dataPost = "${data.dayOfMonth}/${data.monthValue}/${data.year}"

            db.collection("Forum")
                .add(
                    ForumData(
                        user.nome,
                        binding.etAddTittle.text.toString(),
                        binding.etAddComment.text.toString(),
                        materia!!,
                        0 as Integer,
                        "false",
                        dataPost,
                    )
                )
            binding.root.postDelayed({
                findNavController().popBackStack()
            }, 1000)
        }
    }
}