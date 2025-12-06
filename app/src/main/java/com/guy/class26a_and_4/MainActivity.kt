package com.guy.class26a_and_4

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.guy.class26a_and_4.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

//        MSPV1.writeInt(this, "TOP_SCORE", 100);

//        val score = MSPV1.readInt(this, "TOP_SCORE")

//        var mspv2 = MSPV2(this)
//        val score = mspv2.readInt("TOP_SCORE")

//        val score = MSPV3.getInstance().readInt("TOP_SCORE")

//        Log.d("pttt", "score: $score")

        binding.btnVibrate.setOnClickListener {
            MySignal.getInstance().vibrate()
        }

        binding.btnToast.setOnClickListener {
            MySignal.getInstance().toast("Hello World")
        }



        val imageUrl = "https://upload.wikimedia.org/wikipedia/commons/2/2f/1_Noa_Kirel_%28cropped%29.jpg"
//        Glide.with(this).load(imageUrl).into(binding.imgBack);

        Glide
            .with(this)
            .load(R.drawable.img_bibi)
            .into(binding.imgBack);



        gameEnded(100)


        val json = MSPV3.getInstance().readString("DB")
        var myDataAfter: GamaData = Gson().fromJson(json, GamaData::class.java)

    }

    fun gameEnded(score: Int) {
        var myData: GamaData = GamaData()
        myData.records.add(Record().setScore(100).setTimestamp(System.currentTimeMillis()).setLat(10.0).setLon(20.0))
        myData.records.add(Record().setScore(130).setTimestamp(System.currentTimeMillis()).setLat(10.0).setLon(20.0))
        myData.records.add(Record().setScore(100).setTimestamp(System.currentTimeMillis()).setLat(10.0).setLon(20.0))
        myData.records.add(Record().setScore(200).setTimestamp(System.currentTimeMillis()).setLat(10.0).setLon(20.0))
        myData.records.add(Record().setScore(400).setTimestamp(System.currentTimeMillis()).setLat(10.0).setLon(20.0))
        myData.records.add(Record().setScore(100).setTimestamp(System.currentTimeMillis()).setLat(10.0).setLon(20.0))
        myData.records.add(Record().setScore(100).setTimestamp(System.currentTimeMillis()).setLat(10.0).setLon(20.0))

        val json = Gson().toJson(myData)

        MSPV3.getInstance().writeString("DB", json)







    }



}


