package com.hackfie.hackfieofficial

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.hackfie.hackfieofficial.databinding.ActivityForgotPasswordBinding
private var dialog: ProgressDialog? = null



class ForgotPassword : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
//        enableEdgeToEdge()
        setContentView(binding.root)

        dialog = ProgressDialog(this)
        dialog!!.setMessage("Sending Email to recover your account")
        dialog!!.setCancelable(false)



        firebaseAuth = FirebaseAuth.getInstance()

        binding.resetPasswordBtn.setOnClickListener {
            val email = binding.emailPasswordResetField.text.toString()
            if (email.isEmpty()) {
                binding.errorText.text = "Please enter your email"
            }else{
                dialog!!.show()
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, PasswordResetEmailSentSucessfullly::class.java)
                        dialog!!.dismiss()
                        startActivity(intent)
                        finish()
                    }else{
                        dialog!!.dismiss()
                        binding.emailPasswordResetField.text.clear()
                        binding.errorText.text = it.exception?.message
                    }
                }
            }
        }


        binding.backToLogin.setOnClickListener {
            val intent = Intent(this, LoginHackFie::class.java)
            startActivity(intent)
            finish()
        }
        binding.emailPasswordResetField.setOnClickListener {
            binding.errorText.text = ""
        }



    }
}