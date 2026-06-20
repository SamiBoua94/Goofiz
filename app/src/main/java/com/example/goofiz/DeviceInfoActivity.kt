package com.example.goofiz

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

class DeviceInfoActivity : AppCompatActivity() {

    private lateinit var deviceIdValue: TextView
    private lateinit var imeiValue: TextView
    private lateinit var imsiValue: TextView
    private lateinit var locationValue: TextView
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.device_infos)

        // Initialiser les vues
        deviceIdValue = findViewById(R.id.deviceIdValue)
        imeiValue = findViewById(R.id.imeiValue)
        imsiValue = findViewById(R.id.imsiValue)
        locationValue = findViewById(R.id.locationValue)
        btnBack = findViewById(R.id.btnBack)

        // Récupérer et afficher les informations
        afficherInformationsAppareil()
        afficherLocalisationActuelle()

        // Bouton retour
        btnBack.setOnClickListener {
            finish() // Retourne à la page précédente
        }
    }

    private fun afficherInformationsAppareil() {
        // Récupérer les infos
        val deviceId = DeviceInfoHelper.getDeviceId(this)
        val imei = DeviceInfoHelper.getIMEI(this)
        val imsi = DeviceInfoHelper.getIMSI(this)

        // Afficher les infos
        deviceIdValue.text = deviceId
        imeiValue.text = imei
        imsiValue.text = imsi
    }

    @SuppressLint("MissingPermission")
    private fun afficherLocalisationActuelle() {
        val fineGranted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            locationValue.text = getString(R.string.current_location_unavailable)
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    locationValue.text = "Lat: ${location.latitude}, Lon: ${location.longitude}"
                } else {
                    val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                    val provider = when {
                        fineGranted && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
                        coarseGranted && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
                        else -> null
                    }

                    if (provider != null) {
                        val fallbackLocation = locationManager.getLastKnownLocation(provider)
                        locationValue.text = fallbackLocation?.let {
                            "Lat: ${it.latitude}, Lon: ${it.longitude}"
                        } ?: getString(R.string.current_location_unavailable)
                    } else {
                        locationValue.text = getString(R.string.current_location_unavailable)
                    }
                }
            }
            .addOnFailureListener {
                locationValue.text = getString(R.string.current_location_unavailable)
            }
    }
}