package com.guy.class26a_and_4

import android.content.Context
import android.content.Context.MODE_PRIVATE

object MSPV1 {

    private const val SP_FILE: String = "MyDBFile"

    fun readInt(context: Context, key: String, def: Int = 0): Int {
        val prefs = context.getSharedPreferences(SP_FILE, MODE_PRIVATE)
        val value = prefs.getInt(key, def)

        return value
    }

    fun writeInt(context: Context, key: String, value: Int) {
        val editor = context.getSharedPreferences(SP_FILE, MODE_PRIVATE).edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun readString(context: Context, key: String, def: String? = ""): String? {
        val prefs = context.getSharedPreferences(SP_FILE, MODE_PRIVATE)
        val value = prefs.getString(key, def)

        return value
    }

    fun writeString(context: Context, key: String, value: String?) {
        val editor = context.getSharedPreferences(SP_FILE, MODE_PRIVATE).edit()
        editor.putString(key, value)
        editor.apply()
    }
}