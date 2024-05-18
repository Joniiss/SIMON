package com.app.simon

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.beust.klaxon.Klaxon
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

class LoadingActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var db = FirebaseFirestore.getInstance()
    private lateinit var functions: FirebaseFunctions
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loading)

        auth = Firebase.auth

        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
    }

    override fun onStart() {
        super.onStart()
        //auth.signOut()
        val currentUser = auth.currentUser
        functions = Firebase.functions("southamerica-east1")

        if(currentUser != null){

            login(currentUser!!.uid)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        val genericResp = gson.fromJson(
                            task.result,
                            FunctionsGenericResponse::class.java
                        )

                        val user = Klaxon()
                            .parse<User>(genericResp.payload.toString())

                        db.collection("Alunos")
                            .whereEqualTo("uid", "w3qRRACjXpO9z8SNDDnHSkAcQyi2")
                            .get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {
                                    user!!.celular =
                                        document["celular"].toString()
                                    user.email =
                                        document["email"].toString()
                                    user.horario =
                                        document["horario"].toString()
                                    user.predio =
                                        document["predio"].toString()
                                    user.sala =
                                        document["sala"].toString()
                                    user.status =
                                        document["status"].toString()
                                    user.uid =
                                        currentUser.uid
                                    user.pNome =
                                        user.nome.split(" ")[0]
                                    user.foto =
                                        document["foto"].toString()
                                    user.monitor =
                                        document["monitor"].toString()
                                }
                                val i = Intent(this, MainActivity::class.java)
                                i.putExtra("user", user as Serializable)
                                this.startActivity(i)
                                finish()
                            }
                    }
                }
        } else{
            val intentLogin = Intent(this,LoginActivity::class.java)

            this.startActivity(intentLogin)
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