package com.app.simon

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.simon.databinding.ActivityMainBinding
import com.app.simon.databinding.ActivitySubjectBinding
import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.Klaxon
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder

class SubjectActivity : AppCompatActivity() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MonitorAdapter
    private lateinit var binding: ActivitySubjectBinding
    private lateinit var functions: FirebaseFunctions
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySubjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        functions = Firebase.functions("southamerica-east1")

        val user: User = intent.getSerializableExtra("user") as User
        val materia = intent.getStringExtra("materiaSub")

        binding.tvTituloMateria.text = materia

        mRecyclerView = binding.rvListaMonitores
        mRecyclerView.setLayoutManager(LinearLayoutManager(this));

        var mItems: MutableList<MonitorData> = emptyList<MonitorData>().toMutableList()

        var exemplo = MonitorData("Alfredo","Local: S02 - H15","Horario: 16:20 as 16:30", "H15", "S02", "", "", "", "")
        var exemplo2 = MonitorData("qualquer coisa","Local: S02 - H15","Horario: 16:20 as 16:30", "H15", "S02", "", "", "", "")


        getMonitores(materia!!)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    val genericResp = gson.fromJson(
                        task.result,
                        FunctionsGenericResponse::class.java
                    )

                    Toast.makeText(this, genericResp.payload.toString(), Toast.LENGTH_LONG).show()
                    println(genericResp.payload)

                    //var teste = genericResp.payload.toString() as ArrayList<String>
                    //println(teste[0])

                    val monitor = Klaxon()
                        .parseArray<MonitorData>(genericResp.payload.toString())
                    val teste: MutableList<MonitorData> = emptyList<MonitorData>().toMutableList()

                    monitor?.get(0)?.let { teste!!.add(it) }
                    teste.add(MonitorData("José Henrique", "", "13:30-19:00", "H12", "208", "gs://simon-12985.appspot.com/perfis/teste.jpeg", "2", "a", "true"))

                    mAdapter = MonitorAdapter(teste, this)
                    mRecyclerView.adapter = mAdapter

                    /*mItems.add(exemplo)
                    mItems.add(exemplo2)
                    monitor!!.forEach { mon ->
                        mItems.add(mon)
                    }
                    mAdapter = MonitorAdapter(mItems)
                    mRecyclerView.adapter = mAdapter*/
                }
            }



        mItems.add(exemplo)
        mItems.add(exemplo2)




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
}