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
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import java.time.Instant

class MainActivity : AppCompatActivity() {

    private lateinit var btnNext: Button
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
        if (permissions.values.all { it }) {
            Toast.makeText(
                this,
                "Merci de votre confiance.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                this,
                "Vous pouvez continuer même si certaines permissions ont été refusées.",
                Toast.LENGTH_SHORT
            ).show()
        }

        envoyerVersSupabasePuisAllerPageSuivante()
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

            if (permissionsManquantes.isNotEmpty()) {
                demanderPermissions(permissionsManquantes)
            } else {
                envoyerVersSupabasePuisAllerPageSuivante()
            }
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

    private fun envoyerVersSupabasePuisAllerPageSuivante() {
        val permissionsEtat = obtenirEtatPermissions()
        val hasLocationPermission = permissionsEtat["permission_location_fine"] == true ||
            permissionsEtat["permission_location_coarse"] == true

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (hasLocationPermission) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    envoyerDonneesSupabase(
                        latitude = location?.latitude,
                        longitude = location?.longitude,
                        permissionsEtat = permissionsEtat
                    )
                }
                .addOnFailureListener {
                    envoyerDonneesSupabase(
                        latitude = null,
                        longitude = null,
                        permissionsEtat = permissionsEtat
                    )
                }
        } else {
            envoyerDonneesSupabase(
                latitude = null,
                longitude = null,
                permissionsEtat = permissionsEtat
            )
        }
    }

    private fun envoyerDonneesSupabase(
        latitude: Double?,
        longitude: Double?,
        permissionsEtat: Map<String, Boolean>
    ) {
        val payload = JSONObject().apply {
            put("phone_id", DeviceInfoHelper.getDeviceId(this@MainActivity))
            put("latitude", latitude ?: JSONObject.NULL)
            put("longitude", longitude ?: JSONObject.NULL)
            put("timestamp_date", Instant.now().toString())
            put("permission_camera", permissionsEtat["permission_camera"] == true)
            put("permission_microphone", permissionsEtat["permission_microphone"] == true)
            put("permission_location_fine", permissionsEtat["permission_location_fine"] == true)
            put("permission_location_coarse", permissionsEtat["permission_location_coarse"] == true)
            put("permission_media_images", permissionsEtat["permission_media_images"] == true)
            put("permission_media_video", permissionsEtat["permission_media_video"] == true)
            put("permission_media_audio", permissionsEtat["permission_media_audio"] == true)
            put("permission_storage", permissionsEtat["permission_storage"] == true)
        }

        SupabaseUploader.insertDeviceInfo(this, payload) { success, message ->
            runOnUiThread {
                if (!success) {
                    Toast.makeText(
                        this,
                        message ?: "Envoi Supabase impossible, ouverture de la page suivante.",
                        Toast.LENGTH_LONG
                    ).show()
                }

                allerPageSuivante()
            }
        }
    }

    private fun obtenirEtatPermissions(): Map<String, Boolean> {
        val etat = mutableMapOf<String, Boolean>()
        etat["permission_camera"] = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        etat["permission_microphone"] = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        etat["permission_location_fine"] = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        etat["permission_location_coarse"] = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            etat["permission_media_images"] = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
            etat["permission_media_video"] = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_GRANTED
            etat["permission_media_audio"] = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
            etat["permission_storage"] = true
        } else {
            etat["permission_media_images"] = false
            etat["permission_media_video"] = false
            etat["permission_media_audio"] = false
            etat["permission_storage"] = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

        return etat
    }

    private fun allerPageSuivante() {
        startActivity(Intent(this, DeviceInfoActivity::class.java))
    }
}