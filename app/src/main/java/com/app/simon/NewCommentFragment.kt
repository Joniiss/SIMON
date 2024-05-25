package com.app.simon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.app.simon.databinding.FragmentNewCommentBinding
import com.app.simon.data.CommentData
import com.app.simon.data.ForumData
import com.app.simon.data.UserData
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate

class NewCommentFragment : Fragment() {

    private var db = FirebaseFirestore.getInstance()

    private var _binding: FragmentNewCommentBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewCommentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val post = arguments?.getSerializable("post") as ForumData
        val user = arguments?.getSerializable("user") as UserData

        binding.tvTituloPostComment.text = post.titulo

        binding.etAddComment.addTextChangedListener {
            if(binding.etAddComment.text.isNotEmpty()){
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

            var monitor = ""
            if(user.monitor == "") {
                monitor = "false"
            } else {
                monitor = "true"
            }

            db.collection("Comentarios")
                .add(
                    CommentData(
                        user.nome,
                        dataPost,
                        monitor,
                        post.id,
                        0,
                        binding.etAddComment.text.toString(),
                        "false"
                    )
                )
            if(monitor == "true") {
                db.collection("Forum")
                    .document(post.id)
                    .update(
                        "aprovado", "true",
                        "qtdComent", (post.qtdComent as Int)+1
                    )
                    .addOnCompleteListener {
                        binding.root.postDelayed({
                            findNavController().popBackStack()
                        }, 1000)
                    }
            }
        }
    }
}