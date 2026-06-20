package com.example.goofiz

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Build
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var btnNext: Button
    private var permissionsValidees = false
    private val permissionsAReclamer = mutableListOf<String>().apply {
        add(Manifest.permission.CAMERA)
        add(Manifest.permission.RECORD_AUDIO)
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_COARSE_LOCATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.READ_MEDIA_IMAGES)
            add(Manifest.permission.READ_MEDIA_VIDEO)
            add(Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionsValidees = true

        if (permissions.values.all { it }) {
            Toast.makeText(
                this,
                "Merci de votre confiance. Vous pouvez maintenant utiliser l'application.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                this,
                "Vous pouvez continuer, même si certaines permissions ont été refusées.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnNext = findViewById(R.id.btnNext)
        btnNext.isEnabled = true
        btnNext.alpha = 1.0f

        btnNext.setOnClickListener {
            val permissionsManquantes = permissionsAReclamer.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }

            if (!permissionsValidees && permissionsManquantes.isNotEmpty()) {
                demanderPermissions(permissionsManquantes)
            } else {
                allerPageSuivante()
            }
        }

        permissionsValidees = permissionsAReclamer.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun demanderPermissions(permissions: List<String>) {
        val permissionsManquantes = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsManquantes.isNotEmpty()) {
            permissionLauncher.launch(permissionsManquantes.toTypedArray())
        }
    }

    private fun allerPageSuivante() {
        startActivity(Intent(this, SecondActivity::class.java))
    }
}