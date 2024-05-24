package com.app.simon

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.simon.databinding.FragmentPerfilMonitorBinding
import com.app.simon.data.MonitorData
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.File

class PerfilMonitorFragment : Fragment() {

    private var _binding: FragmentPerfilMonitorBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilMonitorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val monitor = arguments?.getSerializable("monitor") as MonitorData

        (activity as? AppCompatActivity)?.supportActionBar?.title = monitor.nome

        if(monitor.status == "true"){
            binding.ivSmallerCam.setCardBackgroundColor(Color.parseColor("#48d41e"))
        }
/*
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl(monitor.foto)

        storageRef.downloadUrl.addOnSuccessListener {
            Picasso.with(context).load(it).fit().centerInside().into(binding.ivPerfil)
        }
*/
        val storage = FirebaseStorage.getInstance()
        val storageRef1 = storage.getReferenceFromUrl(monitor.foto)
        val localFile1 = File.createTempFile("images", "jpg")

        storageRef1.getFile(localFile1).addOnSuccessListener {
            Picasso.with(context).load("file:" + localFile1.absolutePath).fit().centerInside().into(binding.ivPerfil)
        }.addOnFailureListener {
            Toast.makeText(context, "Erro ao baixar imagem: ${it.message}", Toast.LENGTH_LONG).show()
        }

        val horario = monitor.horario.split("-")
        binding.etHorarioDe.hint = horario[0]
        binding.etHorarioAte.hint = horario[1]
        binding.tvNome.text = monitor.nome
        binding.etCel.hint = monitor.celular;
        binding.etEmail.hint = monitor.email;
        binding.etSala.hint = monitor.sala;
        binding.etPredio.hint = monitor.predio;
    }
}