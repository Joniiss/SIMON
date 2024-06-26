package com.app.simon

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.simon.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
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
                        Toast.makeText(
                            baseContext, "Login realizado para usuário " + user!!.email,
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            baseContext, "Email ou senha incorreta, tente novamente!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}