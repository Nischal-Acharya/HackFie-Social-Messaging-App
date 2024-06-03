package com.hackfie.hackfieofficial

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.hackfie.hackfieofficial.databinding.ActivityLoginHackFieBinding

class LoginHackFie : AppCompatActivity() {

    private lateinit var binding: ActivityLoginHackFieBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var dialog: ProgressDialog? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = ActivityLoginHackFieBinding.inflate(layoutInflater)

        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()


        dialog = ProgressDialog(this)
        dialog!!.setMessage("Login into HackFie")
        dialog!!.setCancelable(false)

        // login user to hackfie

        binding.loginHackFieButton.setOnClickListener {
            var email = binding.emailField.text.toString()
            var password = binding.passwordField.text.toString()

            if (email.isNotEmpty() &&  password.isNotEmpty()){
                dialog!!.show()

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful){

                        val userVerified = firebaseAuth.currentUser?.isEmailVerified

                        if (userVerified == true) {

                            val intent = Intent(this, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            dialog!!.dismiss()
                            startActivity(intent)
                            finish()
                        }else{
                            firebaseAuth.currentUser?.sendEmailVerification()?.addOnSuccessListener {

                                firebaseAuth.signOut()

                                val intent = Intent(this, EmailSentRegistration::class.java)
                                dialog!!.dismiss()
                                startActivity(intent)
                                finish()
                            }
                        }
                    }else{
                        binding.passwordField.text.clear()
                        binding.errorText.text = "HackFie does't allow to login with invalid credentials"
                        dialog!!.dismiss()
                    }
                }

            }else{
                binding.errorText.text = "Please enter your credentials"
            }
        }



        // redirect to signup
        binding.SignUpHackFieButton.setOnClickListener{
            var intent = Intent(this, SignUpHackFie::class.java)
            startActivity(intent)
            finish()
        }

        // redirect to forgot password
        binding.recoverIdBtn.setOnClickListener{
            var intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
            finish()
        }

        // hide error text

        binding.emailField.setOnClickListener {
            binding.errorText.text = ""
        }

        binding.passwordField.setOnClickListener {
            binding.errorText.text = ""
        }


    }
}