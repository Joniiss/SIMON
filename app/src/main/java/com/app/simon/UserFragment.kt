package com.app.simon

import android.Manifest
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.simon.MyAdapter
import com.app.simon.R
import com.app.simon.databinding.ActivityLoginBinding
import com.app.simon.databinding.FragmentUserBinding
import com.app.simon.ui.main.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.File


class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null

    private var db = FirebaseFirestore.getInstance()
    private val user = Firebase.auth.currentUser

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MyAdapter
    private lateinit var mItems: MutableList<String>
    private lateinit var tvNome: TextView

    private val binding get() = _binding!!
//    companion object {
//        fun newInstance() = UserFragment()
//    }

    //private val viewModel: MainViewModel by viewModels()

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentUserBinding.inflate(layoutInflater)
        binding.tvNome.text = "teste";
        // TODO: Use the ViewModel
    }*/

    private val cameraProviderResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if(it) {
                cameraScreen()
            } else {
                Snackbar.make(binding.root, "Não foi possível iniciar a câmera", Snackbar.LENGTH_LONG).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = activity?.getIntent()?.getExtras()?.getSerializable("user") as User

        loadPic(user.foto)
        binding.tvNome.text = user.nome
        binding.etCel.hint = user.celular
        binding.etEmail.hint = user.email
        binding.etPredio.hint = user.predio
        binding.etSala.hint = user.sala
        binding.etHorario.hint = user.horario

        if(user.status == "true") {
            binding.tbtnStatus.isChecked = true
            binding.tvToggle.text = "Disponível"
        } else {
            binding.tbtnStatus.isChecked = false
            binding.tvToggle.text = "Indisponível"
        }

        binding.tbtnStatus.setOnCheckedChangeListener{ buttonview, isChecked ->
            attStatus(isChecked.toString())
            if(isChecked) {
                binding.tvToggle.text = "Disponível"
            } else {
                binding.tvToggle.text = "Indisponível"
            }
        }

        binding.btnChangePhoto.setOnClickListener {
            cameraProviderResult.launch(Manifest.permission.CAMERA)
        }
    }

    private fun cameraScreen() {
        val intentCameraPreview = Intent(requireContext(), CameraActivity::class.java)
        startActivity(intentCameraPreview)
    }

    private fun loadPic(foto: String) {
        val storage = FirebaseStorage.getInstance()
        //val storageRef1 = storage.getReferenceFromUrl("gs://simon-12985.appspot.com/perfis/default.jpeg")
        val storageRef1 = storage.getReferenceFromUrl(foto)
        val localFile1 = File.createTempFile("images", "jpg")

        storageRef1.getFile(localFile1).addOnSuccessListener {
            // Local temp file has been created
            //val bitmap = BitmapFactory.decodeFile(localFile1.absolutePath)
            Picasso.with(context).load("file:" + localFile1.absolutePath).fit().centerInside().into(binding.ivPerfil)


            /*
            val bitmap = BitmapFactory.decodeFile(localFile1.absolutePath)
            Picasso.with(context).load("file:" + localFile1.absolutePath).fit().centerInside().into(binding.ivPerfil)
            */

        }.addOnFailureListener {
            // Handle any errors
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }
    }


    private fun attFirestore(campo: String, dado: String) {
        db.collection("Alunos").whereEqualTo("uid", user!!.uid)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    db.collection("Alunos").document(document.id)
                        .update(campo, dado)
                }
            }
    }
    private fun attCel(cel: String) {
        attFirestore("celular", cel)
    }

    private fun attEmail(email: String) {
        attFirestore("email", email)
    }

    private fun attPredio(predio: String) {
        attFirestore("predio", predio)
    }

    private fun attSala(sala: String) {
        attFirestore("sala", sala)
    }
    private fun attStatus(status: String) {
        attFirestore("status", status)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}