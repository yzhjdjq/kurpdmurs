package com.report.kurs.settings

import android.content.Context
import android.content.Context.MODE_PRIVATE

object Settings {
    object SettingsPreferences {
        const val StorageName = "MineSweeperSettings"
        const val SizeOfArenaRowName = "sizeOfArena"
        const val CountOfMinesRowName = "countOfMines"
        const val FlaggingModeRowName = "flaggingMode"
    }

    fun GetSizeOfArena(context: Context, defaultSize: Int = 6): Int {
        return GetPreferences(context, SettingsPreferences.SizeOfArenaRowName, defaultSize)
    }

    fun SetSizeOfArena(context: Context, size: Int) {
        SetPreferences(context, SettingsPreferences.SizeOfArenaRowName, size)
    }

    fun GetCountOfMines(context: Context, defaultCount: Int = 6): Int {
        return GetPreferences(context, SettingsPreferences.CountOfMinesRowName, defaultCount)
    }

    fun SetCountOfMines(context: Context, count: Int) {
        SetPreferences(context, SettingsPreferences.CountOfMinesRowName, count)
    }

    fun GetFlaggingMode(context: Context, defaultMode: Boolean = false):Boolean {
        val pref = context.getSharedPreferences(SettingsPreferences.StorageName, MODE_PRIVATE)
        return pref.getBoolean(SettingsPreferences.FlaggingModeRowName, defaultMode)
    }

    fun SetFlaggingMode(context: Context, mode: Boolean) {
        val pref = context.getSharedPreferences(SettingsPreferences.StorageName, MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean(SettingsPreferences.FlaggingModeRowName, mode)
        editor.apply()
    }

    private fun GetPreferences(context: Context, key: String, defaultValue: Int): Int {
        val pref = context.getSharedPreferences(SettingsPreferences.StorageName, MODE_PRIVATE)
        return pref.getInt(key, defaultValue)
    }

    private fun SetPreferences(context: Context, key: String, value: Int) {
        val pref = context.getSharedPreferences(SettingsPreferences.StorageName, MODE_PRIVATE)
        val editor = pref.edit()
        editor.putInt(key, value)
        editor.apply()
    }
}

