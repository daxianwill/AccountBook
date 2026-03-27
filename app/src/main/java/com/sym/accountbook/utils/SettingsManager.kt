package com.sym.accountbook.utils

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("accountbook_settings", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_DYNAMIC_COLOR = "dynamic_color"
        private const val KEY_DARK_THEME = "dark_theme"
        
        @Volatile
        private var INSTANCE: SettingsManager? = null
        
        fun getInstance(context: Context): SettingsManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingsManager(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }
    
    var isDynamicColorEnabled: Boolean
        get() = sharedPreferences.getBoolean(KEY_DYNAMIC_COLOR, false)
        set(value) {
            sharedPreferences.edit().putBoolean(KEY_DYNAMIC_COLOR, value).apply()
        }
    
    var isDarkThemeEnabled: Boolean
        get() = sharedPreferences.getBoolean(KEY_DARK_THEME, false)
        set(value) {
            sharedPreferences.edit().putBoolean(KEY_DARK_THEME, value).apply()
        }
}
