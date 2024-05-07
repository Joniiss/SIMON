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
import java.io.Serializable


class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null

    private var db = FirebaseFirestore.getInstance()
    private val fireUser = Firebase.auth.currentUser

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
                user.status = "true"
            } else {
                binding.tvToggle.text = "Indisponível"
                user.status = "false"
            }
        }

        binding.btnChangePhoto.setOnClickListener {
            cameraProviderResult.launch(Manifest.permission.CAMERA)
        }

        binding.btnLogout.setOnClickListener {
            Firebase.auth.signOut()
            val i = Intent(context, LoginActivity::class.java)
            this.startActivity(i)
        }

        binding.btnEditarCel.setOnCheckedChangeListener{ buttonview, isChecked ->
            if(isChecked) {
                binding.etCel.hint = ""
                binding.etCel.text.clear()
                binding.etCel.isEnabled = true
            }
            else{
                // TODO: PESQUISAR SOBRE REGEX
                if(binding.etCel.text.toString() == ""){
                    binding.etCel.hint = user.celular
                }else{
                    user.celular = binding.etCel.text.toString()
                    binding.etCel.isEnabled = false
                    attCel(binding.etCel.text.toString())
                    Toast.makeText(context, "Celular atualizado!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnEditarEmail.setOnCheckedChangeListener{ buttonview, isChecked ->
            if(isChecked) {
                binding.etEmail.hint = ""
                binding.etEmail.text.clear()
                binding.etEmail.isEnabled = true
            }
            else{
                if(binding.etEmail.text.toString() == ""){
                    binding.etEmail.hint = user.email
                }else{
                    user.email = binding.etEmail.text.toString()
                    binding.etEmail.isEnabled = false
                    attEmail(binding.etEmail.text.toString())
                    Toast.makeText(context, "Email atualizado!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnEditarPredio.setOnCheckedChangeListener{ buttonview, isChecked ->
            if(isChecked) {
                binding.etPredio.hint = ""
                binding.etPredio.text.clear()
                binding.etPredio.isEnabled = true
            }
            else{
                if(binding.etPredio.text.toString() == ""){
                    binding.etPredio.hint = user.predio
                }else{
                    user.predio = binding.etPredio.text.toString()
                    binding.etPredio.isEnabled = false
                    attPredio(binding.etPredio.text.toString())
                    Toast.makeText(context, "Prédio atualizado!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnEditarSala.setOnCheckedChangeListener{ buttonview, isChecked ->
            if(isChecked) {
                binding.etSala.hint = ""
                binding.etSala.text.clear()
                binding.etSala.isEnabled = true
            }
            else{
                if(binding.etSala.text.toString() == ""){
                    binding.etSala.hint = user.sala
                }else{
                    user.sala = binding.etSala.text.toString()
                    binding.etSala.isEnabled = false
                    attSala(binding.etSala.text.toString())
                    Toast.makeText(context, "Sala atualizada!", Toast.LENGTH_SHORT).show()
                }
            }
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
        Toast.makeText(context, storageRef1.toString(), Toast.LENGTH_SHORT).show()
        val localFile1 = File.createTempFile("images", "jpg")

        storageRef1.downloadUrl.addOnSuccessListener { it ->

            Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()

        }

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
        db.collection("Alunos").whereEqualTo("uid", fireUser!!.uid)
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