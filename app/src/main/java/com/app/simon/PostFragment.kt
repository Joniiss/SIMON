package com.app.simon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.simon.databinding.FragmentForumBinding
import com.app.simon.databinding.FragmentPostBinding
import com.beust.klaxon.Klaxon
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder


class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: PostAdapter
    private lateinit var functions: FirebaseFunctions
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()
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
        val user: User = requireActivity().intent.getSerializableExtra("user") as User
        val post = arguments?.getSerializable("post") as ForumData
        functions = Firebase.functions("southamerica-east1")

        binding.tvTituloDentroPost.text = post.titulo
        binding.tvDescPost.text = post.texto
        binding.tvDataPost.text = post.data
        binding.tvUsuario.text = post.autor





        mRecyclerView = binding.rvListaComentario
        mRecyclerView.setLayoutManager(LinearLayoutManager(this.context));

        //val teste = CommentData("Teste Post")

        //val teste2 = CommentData("Teste Post Lista matei minha familia e comecei um culto  sem querer familia tmj ")

        val lista: MutableList<CommentData> = emptyList<CommentData>().toMutableList()



        //lista.add(teste)
        //lista.add(teste2)
        println(post.id)
        getComentarios(post.id)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    val genericResp = gson.fromJson(
                        task.result,
                        FunctionsGenericResponse::class.java
                    )

                    if(genericResp.payload.toString() != "{}") {
                        val comentarios = Klaxon()
                            .parseArray<ArrayList<String>>(genericResp.payload.toString())

                        val comsList: MutableList<String> = emptyList<String>().toMutableList()
                        val ids: MutableList<String> = emptyList<String>().toMutableList()

                        comentarios!!.forEach{doc ->
                            ids.add(doc[0])
                            comsList.add(doc[1])
                        }

                        val comRec = Klaxon()
                            .parseArray<CommentData>(comsList.toString())

                        var i = 0
                        comRec!!.forEach { com ->
                            com.id = ids[i]
                            i++
                        }

                        mAdapter = PostAdapter(comRec, user, this.requireContext())
                        mRecyclerView.adapter = mAdapter
                    } else {
                        Toast.makeText(context, "Essa matéria n tem comentarios!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        //mAdapter = PostAdapter(lista, this.requireContext())
        //mRecyclerView.adapter = mAdapter
    }

    private fun getComentarios(post: String): Task<String> {

        val data = hashMapOf(
            "post" to post
        )
        return functions
            .getHttpsCallable("getComentarios")
            .call(data)
            .continueWith { task ->
                val res = gson.toJson(task.result?.data)
                res
            }
    }
}