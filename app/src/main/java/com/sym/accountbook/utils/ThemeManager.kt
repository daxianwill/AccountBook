package com.sym.accountbook.utils

import android.content.Context
import androidx.compose.runtime.mutableStateOf

object ThemeManager {
    private val _isDynamicColorEnabled = mutableStateOf(false)
    private val _isDarkThemeEnabled = mutableStateOf(false)
    
    var isDynamicColorEnabled: Boolean
        get() = _isDynamicColorEnabled.value
        set(value) {
            _isDynamicColorEnabled.value = value
        }
    
    var isDarkThemeEnabled: Boolean
        get() = _isDarkThemeEnabled.value
        set(value) {
            _isDarkThemeEnabled.value = value
        }
    
    fun initialize(context: Context) {
        val settingsManager = SettingsManager.getInstance(context)
        isDynamicColorEnabled = settingsManager.isDynamicColorEnabled
        isDarkThemeEnabled = settingsManager.isDarkThemeEnabled
    }
    
    fun saveDynamicColorSetting(context: Context, enabled: Boolean) {
        val settingsManager = SettingsManager.getInstance(context)
        settingsManager.isDynamicColorEnabled = enabled
        isDynamicColorEnabled = enabled
    }
    
    fun saveDarkThemeSetting(context: Context, enabled: Boolean) {
        val settingsManager = SettingsManager.getInstance(context)
        settingsManager.isDarkThemeEnabled = enabled
        isDarkThemeEnabled = enabled
    }
}
