package com.thanhdv.workchecker.ui.theme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.thanhdv.workchecker.data.ConfigRepository
import com.thanhdv.workchecker.data.UserConfig
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ConfigViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = ConfigRepository(application)

    val  config = repo.configFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserConfig()
    )

    fun saveConfig(config: UserConfig) {
        viewModelScope.launch { repo.saveConfig(config) }
    }
}