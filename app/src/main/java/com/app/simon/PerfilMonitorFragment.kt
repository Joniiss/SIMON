package com.app.simon

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.simon.databinding.FragmentPerfilMonitorBinding
import com.app.simon.databinding.FragmentSubjectBinding
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

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


        if(monitor.status == "true"){
            binding.ivSmallerCam.setCardBackgroundColor(Color.parseColor("#48d41e"))
        }

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl(monitor.foto)

        storageRef.downloadUrl.addOnSuccessListener {
            Picasso.with(context).load(it).fit().centerInside().into(binding.ivPerfil)
        }



        binding.etCel.hint = monitor.celular;
        binding.etEmail.hint = monitor.email;
        binding.etSala.hint = monitor.sala;
        binding.etPredio.hint = monitor.predio;


    }

}