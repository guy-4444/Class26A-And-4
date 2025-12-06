package com.guy.class26a_and_4

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class MSPV2(context: Context) {

    private val SP_FILE: String = "MyDBFile"
//
    private var sharedPreferences: SharedPreferences = context.getSharedPreferences(SP_FILE, MODE_PRIVATE)



    fun readInt(key: String, def: Int = 0): Int {
        val value = sharedPreferences.getInt(key, def)

        return value
    }

    fun writeInt(key: String, value: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun readString(key: String, def: String? = ""): String? {
        val value = sharedPreferences.getString(key, def)

        return value
    }

    fun writeString(key: String, value: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }
}