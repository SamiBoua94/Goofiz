package com.example.goofiz

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.telephony.TelephonyManager
import java.util.UUID

object DeviceInfoHelper {

    @SuppressLint("MissingPermission", "HardwareIds")
    fun getDeviceId(context: Context): String {
        try {
            val androidId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            if (!androidId.isNullOrBlank()) {
                return androidId
            }
        } catch (e: Exception) {
            // Fallback
        }

        // UUID persistant
        val prefs = context.getSharedPreferences("device_prefs", Context.MODE_PRIVATE)
        var uuid = prefs.getString("persistent_device_id", null)
        if (uuid == null) {
            uuid = UUID.randomUUID().toString()
            prefs.edit().putString("persistent_device_id", uuid).apply()
        }
        return uuid
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    fun getIMEI(context: Context): String {
        return try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                telephonyManager.imei ?: "Non disponible"
            } else {
                @Suppress("DEPRECATION")
                telephonyManager.deviceId ?: "Non disponible"
            }
        } catch (e: SecurityException) {
            "Permission refusée"
        } catch (e: Exception) {
            "Erreur: ${e.message}"
        }
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    fun getIMSI(context: Context): String {
        return try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            telephonyManager.subscriberId ?: "Non disponible"
        } catch (e: SecurityException) {
            "Permission refusée"
        } catch (e: Exception) {
            "Erreur: ${e.message}"
        }
    }
}