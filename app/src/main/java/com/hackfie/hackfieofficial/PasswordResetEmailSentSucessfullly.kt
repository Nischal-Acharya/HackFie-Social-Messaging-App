package com.hackfie.hackfieofficial

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PasswordResetEmailSentSucessfullly : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()

        setContentView(R.layout.activity_password_reset_email_sent_sucessfullly)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backToLogin = findViewById<Button>(R.id.backToLogin)

        backToLogin.setOnClickListener {
            val intent = Intent(this, LoginHackFie::class.java)
            startActivity(intent)
            finish()
        }

    }
}