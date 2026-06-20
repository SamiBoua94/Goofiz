package com.example.goofiz

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionManager(
    private val activity: Activity,
    private val onAllGranted: () -> Unit,
    private val onDenied: ((List<String>) -> Unit)? = null
) {
    companion object {
        const val REQUEST_CODE = 100

        // ========== PERMISSIONS SANS AUTORISATION (accordées automatiquement) ==========

        // INTERNET ET RÉSEAU
        const val INTERNET = Manifest.permission.INTERNET
        const val ACCESS_NETWORK_STATE = Manifest.permission.ACCESS_NETWORK_STATE
        const val ACCESS_WIFI_STATE = Manifest.permission.ACCESS_WIFI_STATE
        const val CHANGE_WIFI_STATE = Manifest.permission.CHANGE_WIFI_STATE
        const val CHANGE_NETWORK_STATE = Manifest.permission.CHANGE_NETWORK_STATE
        const val NFC = Manifest.permission.NFC
        const val BLUETOOTH = Manifest.permission.BLUETOOTH
        const val BLUETOOTH_ADMIN = Manifest.permission.BLUETOOTH_ADMIN
        const val BLUETOOTH_CONNECT = Manifest.permission.BLUETOOTH_CONNECT
        const val BLUETOOTH_SCAN = Manifest.permission.BLUETOOTH_SCAN

        // VIBRATION
        const val VIBRATE = Manifest.permission.VIBRATE

        // ALARMES ET RÉVEIL
        const val WAKE_LOCK = Manifest.permission.WAKE_LOCK
        const val SET_ALARM = Manifest.permission.SET_ALARM
        const val RECEIVE_BOOT_COMPLETED = Manifest.permission.RECEIVE_BOOT_COMPLETED
        const val FOREGROUND_SERVICE = Manifest.permission.FOREGROUND_SERVICE

        // APPLICATIONS
        const val REQUEST_INSTALL_PACKAGES = Manifest.permission.REQUEST_INSTALL_PACKAGES
        const val INSTALL_SHORTCUT = Manifest.permission.INSTALL_SHORTCUT
        const val UNINSTALL_SHORTCUT = Manifest.permission.UNINSTALL_SHORTCUT
        const val KILL_BACKGROUND_PROCESSES = Manifest.permission.KILL_BACKGROUND_PROCESSES

        // AFFICHAGE
        const val SYSTEM_ALERT_WINDOW = Manifest.permission.SYSTEM_ALERT_WINDOW
        const val EXPAND_STATUS_BAR = Manifest.permission.EXPAND_STATUS_BAR

        // AUDIO
        const val MODIFY_AUDIO_SETTINGS = Manifest.permission.MODIFY_AUDIO_SETTINGS

        // SYSTÈME
        const val SET_WALLPAPER = Manifest.permission.SET_WALLPAPER
        const val SET_WALLPAPER_HINTS = Manifest.permission.SET_WALLPAPER_HINTS
        const val GET_TASKS = Manifest.permission.GET_TASKS
        const val REORDER_TASKS = Manifest.permission.REORDER_TASKS
        const val GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS
        const val MANAGE_OWN_CALLS = Manifest.permission.MANAGE_OWN_CALLS
        const val USE_BIOMETRIC = Manifest.permission.USE_BIOMETRIC
        const val USE_FINGERPRINT = Manifest.permission.USE_FINGERPRINT

        // ========== PERMISSIONS AVEC AUTORISATION EXPLICITE ==========

        // CAMÉRA
        const val CAMERA = Manifest.permission.CAMERA

        // MICROPHONE (Enregistrement audio)
        const val RECORD_AUDIO = Manifest.permission.RECORD_AUDIO

        // LOCALISATION
        const val ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        const val ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
        const val ACCESS_BACKGROUND_LOCATION = Manifest.permission.ACCESS_BACKGROUND_LOCATION
        const val ACCESS_MEDIA_LOCATION = Manifest.permission.ACCESS_MEDIA_LOCATION

        // CONTACTS
        const val READ_CONTACTS = Manifest.permission.READ_CONTACTS
        const val WRITE_CONTACTS = Manifest.permission.WRITE_CONTACTS

        // TÉLÉPHONE
        const val READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE
        const val READ_PHONE_NUMBERS = Manifest.permission.READ_PHONE_NUMBERS
        const val CALL_PHONE = Manifest.permission.CALL_PHONE
        const val ANSWER_PHONE_CALLS = Manifest.permission.ANSWER_PHONE_CALLS
        const val READ_CALL_LOG = Manifest.permission.READ_CALL_LOG
        const val WRITE_CALL_LOG = Manifest.permission.WRITE_CALL_LOG
        const val ADD_VOICEMAIL = Manifest.permission.ADD_VOICEMAIL
        const val USE_SIP = Manifest.permission.USE_SIP

        // SMS
        const val SEND_SMS = Manifest.permission.SEND_SMS
        const val READ_SMS = Manifest.permission.READ_SMS
        const val RECEIVE_SMS = Manifest.permission.RECEIVE_SMS
        const val RECEIVE_MMS = Manifest.permission.RECEIVE_MMS
        const val RECEIVE_WAP_PUSH = Manifest.permission.RECEIVE_WAP_PUSH

        // STOCKAGE
        const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
        const val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
        const val MANAGE_EXTERNAL_STORAGE = Manifest.permission.MANAGE_EXTERNAL_STORAGE

        // NOTIFICATIONS
        const val POST_NOTIFICATIONS = Manifest.permission.POST_NOTIFICATIONS

        // CALENDRIER
        const val READ_CALENDAR = Manifest.permission.READ_CALENDAR
        const val WRITE_CALENDAR = Manifest.permission.WRITE_CALENDAR

        // CAPTEURS CORPORELS
        const val BODY_SENSORS = Manifest.permission.BODY_SENSORS
        const val BODY_SENSORS_BACKGROUND = Manifest.permission.BODY_SENSORS_BACKGROUND

        // ACTIVITÉ PHYSIQUE
        const val ACTIVITY_RECOGNITION = Manifest.permission.ACTIVITY_RECOGNITION

        // SANTÉ (Android 14+)
        const val HIGH_SAMPLING_RATE_SENSORS = Manifest.permission.HIGH_SAMPLING_RATE_SENSORS

        // FICHIERS MÉDIA (Android 13+)
        const val READ_MEDIA_IMAGES = Manifest.permission.READ_MEDIA_IMAGES
        const val READ_MEDIA_VIDEO = Manifest.permission.READ_MEDIA_VIDEO
        const val READ_MEDIA_AUDIO = Manifest.permission.READ_MEDIA_AUDIO

        // APPAREILS PROCHES
        const val NEARBY_WIFI_DEVICES = Manifest.permission.NEARBY_WIFI_DEVICES
        const val UWB_RANGING = Manifest.permission.UWB_RANGING
    }

    private var permissions: Array<String> = emptyArray()

    fun request(vararg permissions: String) {
        this.permissions = arrayOf(*permissions)

        val deniedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }

        if (deniedPermissions.isEmpty()) {
            onAllGranted()
        } else {
            ActivityCompat.requestPermissions(
                activity,
                deniedPermissions.toTypedArray(),
                REQUEST_CODE
            )
        }
    }

    fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE) {
            val deniedPermissions = mutableListOf<String>()

            permissions.forEachIndexed { index, permission ->
                if (index < grantResults.size && grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permission)
                }
            }

            if (deniedPermissions.isEmpty()) {
                onAllGranted()
            } else {
                onDenied?.invoke(deniedPermissions)
            }
        }
    }
}
