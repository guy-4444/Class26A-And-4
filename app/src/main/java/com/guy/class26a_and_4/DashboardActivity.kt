package com.guy.class26a_and_4

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.guy.class26a_and_4.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        updateUI()


    }

    private fun updateUI() {
        binding.prgActivity.progress = 70
        binding.lblPrgActivity.text = "70%"
        binding.lblDailyActivity.text = "You've reached 70% of today's goal. keep the momentum!"
        binding.lblHeartRate.text = "${76}"
        binding.lblHeartRateDesc.text = "Steady"

        // Sample data
        val data = floatArrayOf(10f, 25f, 18f, 35f, 28f, 15f, 38f, 52f, 80f, 60f)

        // Set data with auto min/max
        binding.spcGraph.setData(data)

        // Or set data with custom min/max range
        binding.spcGraph.setData(data, minVal = 0f, maxVal = 100f)

        binding.spcGraph.graphPaddingHorizontal = 0f
    }
}