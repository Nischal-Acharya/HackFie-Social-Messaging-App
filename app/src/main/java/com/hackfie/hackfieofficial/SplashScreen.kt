package com.hackfie.hackfieofficial

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.hackfie.hackfieofficial.databinding.ActivitySplashScreenBinding
import com.google.firebase.FirebaseApp




class SplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var firebaseAuth: FirebaseAuth



    class MyApp : Application() {
        override fun onCreate() {
            super.onCreate()
            FirebaseApp.initializeApp(this)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {


        // Hide the navigation bar
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        super.onCreate(savedInstanceState)

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()

        enableEdgeToEdge()
        setContentView(binding.root)


        Handler(Looper.getMainLooper()).postDelayed({
            if (firebaseAuth.currentUser != null) {
                val mainActivity = Intent(this, MainActivity::class.java)
                startActivity(mainActivity)
                finish()
            }else{
                val passActivity = Intent(this, GetStarted::class.java)
                startActivity(passActivity)
                finish()
            }

        }, 3000)

    }
}