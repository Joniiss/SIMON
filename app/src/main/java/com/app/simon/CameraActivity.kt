package com.app.simon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.app.simon.data.UserData
import com.app.simon.databinding.ActivityCameraBinding
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : ComponentActivity() {

    private lateinit var binding: ActivityCameraBinding

    private var db = FirebaseFirestore.getInstance()

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    //selecionar qual camera usar
    private lateinit var cameraSelector: CameraSelector

    private var imageCapture: ImageCapture? = null

    //executor de thread separado
    private lateinit var imgCaptureExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        imgCaptureExecutor = Executors.newSingleThreadExecutor()

        startCamera()

        binding.btnTakePhoto.setOnClickListener {
            val foto = takePhoto()
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                blinkPreview()
            }
            binding.root.postDelayed({
                salvarFoto(foto)
            }, 3000)

            Log.d("Camera","Foto tirada")
        }
    }

    private fun startCamera() {
        cameraProviderFuture.addListener({

            imageCapture = ImageCapture.Builder().build()

            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                Log.e("CameraPreview", "Falha ao abrir a câmera")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto():String {
        lateinit var file: File
        imageCapture?.let{
            val fileName = "JPEG_${System.currentTimeMillis()}.jpeg"
            file = File(externalMediaDirs[0], fileName)

            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()

            it.takePicture(
                outputFileOptions,
                imgCaptureExecutor,
                object : ImageCapture.OnImageSavedCallback{
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        Log.i("CameraPreview", "A imagem foi salva no diretório: ${file.toUri()}")
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(binding.root.context, "Erro ao salvar foto.", Toast.LENGTH_SHORT).show()
                        Log.e("CameraPreview", "Exceção ao gravar arquivo da foto: $exception")
                    }
                })
            return file.absolutePath
        }
        return file.absolutePath
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun blinkPreview(){
        binding.root.postDelayed({
            binding.root.foreground = ColorDrawable(Color.WHITE)
            binding.root.postDelayed({
                binding.root.foreground = null
            }, 50)
        }, 100)
    }

    private fun salvarFoto(foto: String) {
        val user = intent.getSerializableExtra("user") as UserData

        val millis = System.currentTimeMillis()
        Firebase.storage.getReference().child("perfis/img-${millis}.jpeg")
            .putFile(File(foto).toUri())
        val fotopath = "gs://simon-12985.appspot.com/perfis/img-${millis}.jpeg"
        db.collection("Alunos").whereEqualTo("uid",Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    db.collection("Alunos").document(document.id)
                        .update("foto", fotopath)
                }
            }
        user.foto = fotopath
    }
}