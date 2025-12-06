package com.guy.class26a_and_4

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class MSPV3(context: Context) {


    companion object{

        @Volatile
        private var instance: MSPV3? = null

        fun init(context: Context) : MSPV3 {
            return instance ?: synchronized(this){
                instance ?: MSPV3(context).also { instance = it }
            }
        }

        fun getInstance(): MSPV3 {
            return instance ?: throw IllegalStateException(
                "MSPV3 must be initialized by calling init(context) before use."
            )
        }

    }




    private val SP_FILE: String = "MyDBFile"

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences(SP_FILE, MODE_PRIVATE)









    fun readBool(key: String, def: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, def)
    }

    fun writeBool(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun readInt(key: String, def: Int = 0): Int {
        return sharedPreferences.getInt(key, def)
    }

    fun writeInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun readString(key: String, def: String? = ""): String? {
        return sharedPreferences.getString(key, def)
    }

    fun writeString(key: String, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
    }
}