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
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.simon.MyAdapter
import com.app.simon.R
import com.app.simon.databinding.ActivityLoginBinding
import com.app.simon.databinding.FragmentUserBinding
import com.app.simon.ui.main.MainViewModel
import com.beust.klaxon.Klaxon
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import java.io.File
import java.io.Serializable
import java.lang.ClassCastException
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit


class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null

    private var db = FirebaseFirestore.getInstance()
    private val fireUser = Firebase.auth.currentUser
    private lateinit var functions: FirebaseFunctions
    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()
/*
    val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Do if the permission is granted
        }
        else {
            // Do otherwise
        }
    }
*/



    private val binding get() = _binding!!

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



        functions = Firebase.functions("southamerica-east1")
        val user = activity?.getIntent()?.getExtras()?.getSerializable("user") as User

        loadPic(user.foto)
        binding.tvNome.text = user.nome
        binding.etCel.hint = user.celular
        binding.etEmail.hint = user.email
        binding.etPredio.hint = user.predio
        binding.etSala.hint = user.sala

        val horario = user.horario.split("-")
        binding.etHorarioDe.hint = horario[0]
        binding.etHorarioAte.hint = horario[1]

        db.collection("Monitores").document(user.monitor)
            .get()
            .addOnCompleteListener { doc ->
                if(doc.result.data!!["horas"].toString() == "0") {
                    binding.tvHorasContabInput.text = "0.00"
                } else {
                    binding.tvHorasContabInput.text = "%.2f".format(doc.result.data!!["horas"])
                }

            }

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
                attFirestore("status", user.status)


                db.collection("Monitores").document(user.monitor)
                    .get()
                    .addOnCompleteListener { doc ->
                        val month = doc.result.data!!["monthValue"]

                        if(month.toString() != LocalDateTime.now().monthValue.toString()) {
                            db.collection("Monitores").document(user.monitor)
                                .update("monthValue", LocalDateTime.now().monthValue,
                                    "horas", 0.0f)

                            db.collection("LogHoras").add(logHorasData(uid= user.uid))
                        } else {
                            db.collection("LogHoras").add(logHorasData(uid= user.uid))
                        }
                    }



            } else {
                binding.tvToggle.text = "Indisponível"
                user.status = "false"
                attFirestore("status", user.status)

                getLogHoras(user.uid)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            val genericResp = gson.fromJson(
                                task.result,
                                FunctionsGenericResponse::class.java
                            )

                            if(genericResp.payload.toString() != "{}") {
                                val log = Klaxon()
                                    .parse<logHorasData>(genericResp.payload.toString())

                                val intervalo = System.currentTimeMillis() - log!!.millis
                                val tempo = intervalo.milliseconds
                                Toast.makeText(context, "${tempo.toString(DurationUnit.HOURS,
                                    2)} horas adicionadas",
                                    Toast.LENGTH_SHORT).show()

                                val horasN = tempo.toDouble(DurationUnit.HOURS)

                                db.collection("Monitores").document(user.monitor)
                                    .get()
                                    .addOnCompleteListener { doc ->
                                        var horas = 0.0
                                        try {
                                            horas = (doc.result.data!!["horas"] as Long).toDouble()
                                        } catch (e: ClassCastException) {
                                            horas = doc.result.data!!["horas"] as Double
                                        }

                                        val calcHoras = horas + horasN

                                        db.collection("Monitores").document(user.monitor)
                                            .update("horas", calcHoras)

                                        binding.tvHorasContabInput.text = "%.2f".format(calcHoras)
                                    }
                            } else {
                                Toast.makeText(context, "Erro ao obter horas", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
        }

        binding.btnChangePhoto.setOnClickListener {
            val bundle = bundleOf("user" to user)
            cameraProviderResult.launch(Manifest.permission.CAMERA)
            //cameraProviderResult.launch(Manifest.permission.CAMERA)
            //findNavController().popBackStack()
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
        val user = activity?.getIntent()?.getExtras()?.getSerializable("user") as User
        val bundle = bundleOf("user" to user)
        findNavController().navigate(R.id.action_userFragment_to_cameraFragment, bundle)
    }

    private fun loadPic(foto: String) {
        val storage = FirebaseStorage.getInstance()
        val storageRef1 = storage.getReferenceFromUrl(foto)
        val localFile1 = File.createTempFile("images", "jpg")

        storageRef1.getFile(localFile1).addOnSuccessListener {
            Picasso.with(context).load("file:" + localFile1.absolutePath).fit().centerInside().into(binding.ivPerfil)
        }.addOnFailureListener {
            Toast.makeText(context, "Erro ao baixar imagem: ${it.message}",Toast.LENGTH_LONG).show()
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

    private fun getLogHoras(uid: String): Task<String> {

        val data = hashMapOf(
            "uid" to uid
        )
        return functions
            .getHttpsCallable("getLogHoras")
            .call(data)
            .continueWith { task ->
                val res = gson.toJson(task.result?.data)
                res
            }
    }
/*
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    */
}