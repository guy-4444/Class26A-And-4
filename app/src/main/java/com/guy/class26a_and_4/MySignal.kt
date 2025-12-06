package com.guy.class26a_and_4

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Context.VIBRATOR_SERVICE
import android.content.SharedPreferences
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import java.lang.ref.WeakReference

class MySignal(context: Context) {


    companion object{

        @Volatile
        private var instance: MySignal? = null

        fun init(context: Context) : MySignal {
            return instance ?: synchronized(this){
                instance ?: MySignal(context).also { instance = it }
            }
        }

        fun getInstance(): MySignal {
            return instance ?: throw IllegalStateException(
                "MySignal must be initialized by calling init(context) before use."
            )
        }
    }



    private val contextRef = WeakReference(context)


    fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // API 31+  → VibratorManager
            val vibratorManager = contextRef.get()?.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator

            val effect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(effect)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val v = contextRef.get()?.getSystemService(VIBRATOR_SERVICE) as Vibrator
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            val v = contextRef.get()?.getSystemService(VIBRATOR_SERVICE) as Vibrator
            v.vibrate(500)
        }
    }

    fun toast(msg: String) {
        Toast.makeText(contextRef.get(), msg, Toast.LENGTH_SHORT).show()
    }


}