package com.thanhdv.workchecker.network

import com.thanhdv.workchecker.data.UserConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object WebhookClient {

    private val client = OkHttpClient()

    suspend fun send(config: UserConfig): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            validate(config)?.let { return@withContext Result.failure(Exception(it)) }

            val time = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date())
            val isoTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ROOT).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.format(Date())

            val resolvedMessage = resolveMessage(config)

            val payload = config.payload
                .replace("{message}", resolvedMessage)
                .replace("{name}", config.name)
                .replace("{empId}", config.employeeId)
                .replace("{email}", config.email)
                .replace("{time}", time)
                .replace("{isoTime}", isoTime)

            val body = payload.toRequestBody("application/json".toMediaType())
            val request = Request.Builder().url(config.webhookURL).post(body).build()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("HTTP ${response.code} — ${response.message}"))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun resolveMessage(config: UserConfig): String {
        val time = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date())
        return config.message
            .replace("{name}", config.name)
            .replace("{empId}", config.employeeId)
            .replace("{time}", time)
    }

    private fun validate(config: UserConfig): String? = when {
        config.webhookURL.isBlank() -> "Webhook URL is not configured"
        config.name.isBlank() -> "Name cannot be empty"
        config.payload.isBlank() -> "Payload template is empty"

        else -> null
    }
}