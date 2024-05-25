package com.app.simon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.app.simon.databinding.FragmentForumBinding
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.simon.adapter.ForumAdapter
import com.app.simon.data.ForumData
import com.app.simon.data.UserData
import com.beust.klaxon.Klaxon
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder

class ForumFragment : Fragment() {

    private var _binding: FragmentForumBinding? = null
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: ForumAdapter
    private lateinit var functions: FirebaseFunctions
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        functions = Firebase.functions("southamerica-east1")

        val user = arguments?.getSerializable("user") as UserData
        val materia = arguments?.getString("materiaSub")

        (activity as? AppCompatActivity)?.supportActionBar?.title = materia

        mRecyclerView = binding.rvListaForum
        mRecyclerView.setLayoutManager(LinearLayoutManager(this.context));


        val postsList: MutableList<String> = emptyList<String>().toMutableList()
        val ids: MutableList<String> = emptyList<String>().toMutableList()

        getPosts(materia!!)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    val genericResp = gson.fromJson(
                        task.result,
                        FunctionsGenericResponse::class.java
                    )

                    if(genericResp.payload.toString() != "{}") {
                        val posts = Klaxon()
                            .parseArray<ArrayList<String>>(genericResp.payload.toString())

                        posts!!.forEach{doc ->
                            ids.add(doc[0])
                            postsList.add(doc[1])
                        }

                        val postsRec = Klaxon()
                            .parseArray<ForumData>(postsList.toString())

                        var i = 0
                        postsRec!!.forEach { post ->
                            post.id = ids[i]
                            i++
                        }

                        if(postsRec.isEmpty()){
                            binding.tvListaVazia.text = "O forúm desta matéria está vazio!";
                        }

                        mAdapter = ForumAdapter(postsRec!!, this.requireContext(), this)
                        mRecyclerView.adapter = mAdapter
                    } else {
                        binding.tvListaVazia.text = "Essa matéria não tem posts!"
                    }
                }

            }

        binding.btnMonitores.setOnClickListener {
            findNavController().navigate(R.id.action_forumFragment_to_subjectFragment)
        }

        binding.fbtnAddPost.setOnClickListener {
            val bundle = bundleOf(
                "materiaSub" to materia,
                "user" to user)
            findNavController().navigate(R.id.action_forumFragment_to_newPostFragment, bundle)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getPosts(materia: String): Task<String> {

        val data = hashMapOf(
            "materia" to materia
        )
        return functions
            .getHttpsCallable("getPosts")
            .call(data)
            .continueWith { task ->
                val res = gson.toJson(task.result?.data)
                res
            }
    }
}