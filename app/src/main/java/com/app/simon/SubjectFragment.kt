package com.app.simon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.simon.databinding.FragmentSubjectBinding
import com.app.simon.adapter.MonitorAdapter
import com.app.simon.data.ForumData
import com.app.simon.data.MonitorData
import com.app.simon.data.UserData
import com.beust.klaxon.Klaxon
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder

class SubjectFragment : Fragment() {

    private var _binding: FragmentSubjectBinding? = null

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MonitorAdapter
    private lateinit var functions: FirebaseFunctions
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        functions = Firebase.functions("southamerica-east1")

        val materia = arguments?.getString("materiaSub")
        val user = arguments?.getSerializable("user") as UserData

        binding.tvTituloMateria.text = materia

        mRecyclerView = binding.rvListaMonitores
        mRecyclerView.setLayoutManager(LinearLayoutManager(this.context))

        var mItems: MutableList<MonitorData> = emptyList<MonitorData>().toMutableList()

        var exemplo = MonitorData("Alfredo","Local: S02 - H15","Horario: 16:20 as 16:30", "H15", "S02", "", "", "", "")
        var exemplo2 = MonitorData("qualquer coisa","Local: S02 - H15","Horario: 16:20 as 16:30", "H15", "S02", "", "", "", "")

        Toast.makeText(context, materia!!, Toast.LENGTH_SHORT).show()

        getMonitores(materia)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    val genericResp = gson.fromJson(
                        task.result,
                        FunctionsGenericResponse::class.java
                    )

                    if(genericResp.payload.toString() != "{}") {
                        val monitor = Klaxon()
                            .parseArray<MonitorData>(genericResp.payload.toString())
                        
                        mAdapter = MonitorAdapter(monitor!!, this.requireContext(), this)
                        mRecyclerView.adapter = mAdapter
                    } else {
                        Toast.makeText(context, "Essa matéria n tem monitores!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

//        mItems.add(exemplo)
//        mItems.add(exemplo2)

        binding.btnForum.setOnClickListener {
            findNavController().navigate(R.id.action_subjectFragment_to_forumFragment)
        }

    }

    private fun getMonitores(materia: String): Task<String> {

        val data = hashMapOf(
            "materia" to materia
        )
        return functions
            .getHttpsCallable("getMonitores")
            .call(data)
            .continueWith { task ->
                val res = gson.toJson(task.result?.data)
                res
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}