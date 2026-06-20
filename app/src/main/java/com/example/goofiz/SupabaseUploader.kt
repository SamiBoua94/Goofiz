package com.example.goofiz

import android.content.Context
import org.json.JSONObject
import java.io.BufferedOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

object SupabaseUploader {

    fun insertDeviceInfo(
        context: Context,
        payload: JSONObject,
        onResult: (Boolean, String?) -> Unit
    ) {
        thread(name = "supabase-insert-thread") {
            var connection: HttpURLConnection? = null

            try {
                val baseUrl = context.getString(R.string.supabase_url)
                val anonKey = context.getString(R.string.supabase_anon_key)
                val tableName = context.getString(R.string.supabase_table_name)

                val url = URL("$baseUrl/rest/v1/$tableName")
                connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    connectTimeout = 15000
                    readTimeout = 15000
                    setRequestProperty("apikey", anonKey)
                    setRequestProperty("Authorization", "Bearer $anonKey")
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Prefer", "return=minimal")
                }

                BufferedOutputStream(connection.outputStream).use { outputStream ->
                    outputStream.write(payload.toString().toByteArray(Charsets.UTF_8))
                    outputStream.flush()
                }

                val responseCode = connection.responseCode
                val success = responseCode in 200..299
                val message = if (success) {
                    null
                } else {
                    "Supabase a répondu avec le code $responseCode"
                }

                onResult(success, message)
            } catch (exception: Exception) {
                onResult(false, exception.message ?: "Erreur lors de l'envoi Supabase")
            } finally {
                connection?.disconnect()
            }
        }
    }
}