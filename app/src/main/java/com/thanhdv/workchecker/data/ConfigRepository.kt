package com.thanhdv.workchecker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore< Preferences> by preferencesDataStore(name = "user_config")

class ConfigRepository(private  val context: Context) {
    companion object{
        val KEY_NAME = stringPreferencesKey("name")
        val KEY_EMAIL = stringPreferencesKey("email")
        val KEY_EMPLOYEE_ID = stringPreferencesKey("employee_id")
        val KEY_WEBHOOK_URL = stringPreferencesKey("webhook_url")
        val KEY_PAYLOAD = stringPreferencesKey("payload")
        val KEY_MESSAGE = stringPreferencesKey("message")
    }

    val configFlow: Flow<UserConfig> = context.dataStore.data.map { prefs ->
        val default = UserConfig()
        UserConfig(
            name = prefs[KEY_NAME] ?: default.name,
            email = prefs[KEY_EMAIL] ?: default.email,
            employeeId = prefs[KEY_EMPLOYEE_ID] ?: default.employeeId,
            webhookURL = prefs[KEY_WEBHOOK_URL] ?: default.webhookURL,
            payload = prefs[KEY_PAYLOAD] ?: default.payload,
            message = prefs[KEY_MESSAGE] ?: default.message,
        )
    }

    suspend fun saveConfig(config: UserConfig) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NAME] = config.name
            prefs[KEY_EMAIL] = config.email
            prefs[KEY_EMPLOYEE_ID] = config.employeeId
            prefs[KEY_WEBHOOK_URL] = config.webhookURL
            prefs[KEY_PAYLOAD] = config.payload
            prefs[KEY_MESSAGE] = config.message
        }
    }
}