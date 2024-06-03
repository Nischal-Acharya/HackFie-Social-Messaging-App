package com.hackfie.hackfieofficial

import android.content.Intent
import android.os.Bundle
import android.view.TextureView
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.hackfie.hackfieofficial.databinding.ActivityGetStartedBinding

class GetStarted : AppCompatActivity() {

    private lateinit var binding: ActivityGetStartedBinding
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        // Hide the navigation bar like soft buttons recent apps, home and back
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )

        binding = ActivityGetStartedBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)


        val button = findViewById<TextView>(R.id.getStarted)

        button.setOnClickListener {
            val intent = Intent(this, LoginHackFie::class.java)
            startActivity(intent)
        }


    }


}