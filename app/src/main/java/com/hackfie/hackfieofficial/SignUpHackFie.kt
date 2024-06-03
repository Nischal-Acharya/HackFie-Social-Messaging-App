package com.hackfie.hackfieofficial

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hackfie.hackfieofficial.databinding.ActivitySignUpHackFieBinding
import java.text.SimpleDateFormat
import java.util.Date

class SignUpHackFie : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpHackFieBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpHackFieBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        dialog = ProgressDialog(this)
        dialog!!.setMessage("Registering you into our server...")
        dialog!!.setCancelable(false)

        binding.registerHackFieButton.setOnClickListener {
            val email = binding.emailField.text.toString()
            val fullName = binding.fullNameField.text.toString()
            val password = binding.passwordField.text.toString()
            val confirmPassword = binding.confrimPasswordField.text.toString()

            if (email.isNotEmpty() && fullName.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    dialog!!.show()
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val sdf = SimpleDateFormat("dd/M/yyyy")
                            val currentDate = sdf.format(Date())

                            Log.d("SignUpHackFie", "User created successfully, storing data...")

                            val currentUser = FirebaseAuth.getInstance().currentUser
                            val userId = currentUser?.uid.toString()

                            storeData(userId, fullName, email, password)


                        } else {
                            dialog!!.dismiss()
                            Log.e("SignUpHackFie", "Registration failed: ${task.exception?.message}")
                            binding.errorText.text = task.exception?.message
                        }
                    }
                } else {
                    binding.errorText.text = "Your Confirm Password is not Matched"
                }
            } else {
                binding.errorText.text = "Please fill all the fields"
            }
        }


        val clearErrorListener = View.OnClickListener {
            binding.errorText.text = ""
        }
        binding.emailField.setOnClickListener(clearErrorListener)
        binding.fullNameField.setOnClickListener(clearErrorListener)
        binding.passwordField.setOnClickListener(clearErrorListener)
        binding.confrimPasswordField.setOnClickListener(clearErrorListener)

        binding.LoginHackFieButton.setOnClickListener {
            val intent = Intent(this, LoginHackFie::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun storeData(userId: String, fullName: String, emailAddress: String, accountPassword: String) {

        val todayDate = SimpleDateFormat("dd/MM/yyyy").format(Date())

        val data = hashMapOf(
            "name" to fullName,
            "email" to emailAddress,
            "password" to accountPassword,
            "bio" to "Hey, I'm new here",
            "profile" to "",
            "dob" to "",
            "gender" to "",
            "followers" to listOf<String>(),
            "following" to listOf<String>(),
            "posts" to 0,
            "status" to "",
            "uid" to userId,
            "registeredOn" to todayDate
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("Users")
            .document(userId)
            .set(data)
            .addOnSuccessListener {
                firebaseAuth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
                    Log.d("SignUpHackFie", "Email verification sent.")
                    firebaseAuth.signOut()
                    val intent = Intent(this, EmailSentRegistration::class.java)
                    startActivity(intent)
                    dialog!!.dismiss()
                    finish()
                }?.addOnFailureListener { e ->
                    dialog!!.dismiss()
                    Log.e("SignUpHackFie", "Email verification failed: ${e.message}")
                }
                Log.d("Firestore", "Document added with ID: $userId")
            }
            .addOnFailureListener { e ->
                dialog!!.dismiss()
                Log.w("Firestore", "Error adding document", e)
            }
    }


}
