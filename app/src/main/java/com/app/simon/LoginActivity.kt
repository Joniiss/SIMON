package com.app.simon

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.simon.databinding.ActivityLoginBinding
import com.beust.klaxon.Klaxon
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import java.io.Serializable
import java.util.Objects

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var functions: FirebaseFunctions
    private var db = FirebaseFirestore.getInstance()
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.btnEntrar.setOnClickListener {

            auth.signInWithEmailAndPassword(
                binding.etRa.text.toString(),
                binding.etSenha.text.toString()
            )
                .addOnCompleteListener(this) { task ->
                    if(task.isSuccessful){
                        val user = auth.currentUser

                        functions = Firebase.functions("southamerica-east1")

                        login(user!!.uid)
                            .addOnCompleteListener(OnCompleteListener { task ->
                                if(task.isSuccessful) {
                                    val genericResp = gson.fromJson(task.result, FunctionsGenericResponse::class.java)

                                    val result = Klaxon()
                                        .parse<User>(genericResp.payload.toString())

                                    db.collection("Alunos").whereEqualTo("uid", "w3qRRACjXpO9z8SNDDnHSkAcQyi2")
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            for(document in documents) {
                                                result!!.celular = document["celular"].toString()
                                                result.email = document["email"].toString()
                                                result.horario = document["horario"].toString()
                                                result.predio = document["predio"].toString()
                                                result.sala = document["sala"].toString()
                                                result.status = document["status"] as Boolean
                                                result.uid = user.uid
                                            }
                                            val i = Intent(this, MainActivity::class.java)
                                            i.putExtra("user", result as Serializable)
                                            this.startActivity(i)
                                        }
                                }
                                else {
                                    Toast.makeText(this, "Erro", Toast.LENGTH_SHORT).show()
                                }
                            })
                    } else {
                        Toast.makeText(
                            baseContext, "Email ou senha incorreta, tente novamente!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    private fun login(uid: String): Task<String> {

        val data = hashMapOf(
            "uid" to uid
        )
        return functions
            .getHttpsCallable("login")
            .call(data)
            .continueWith { task ->
                val res = gson.toJson(task.result?.data)
                res
            }
    }
}