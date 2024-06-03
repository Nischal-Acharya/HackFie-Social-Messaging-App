package com.hackfie.hackfieofficial.fragments

import UpdateUserProfileHackFie
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hackfie.hackfieofficial.LoginHackFie
import com.hackfie.hackfieofficial.R
import com.hackfie.hackfieofficial.databinding.FragmentDevProfileBinding
import java.lang.Integer.parseInt
import java.time.LocalDate
import java.time.format.DateTimeParseException

//import com.hackfie.hackfieofficial.UpdateUserProfileHackFie

class DevProfileFragment : Fragment() {

    private var _binding: FragmentDevProfileBinding? = null
    private val binding get() = _binding!!

    private var param1: String = ""
    private var param2: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDevProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // User ko data fetch garna lai user ko id fetch gareko
        val aahileLoginVakoUser = FirebaseAuth.getInstance().currentUser
        val userkoID = aahileLoginVakoUser?.uid.toString()
        fetchData(userkoID)




        binding.editMyProfileBtn.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.main_frame_layout, UpdateUserProfileHackFie())
                addToBackStack(null) // Optional: add to back stack to enable back navigation
                commit()
            }
        }

        binding.userLogout.setOnClickListener {
            showLogoutDialog()
        }














    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Check if fragment is attached to activity before performing transactions
        if (!isAdded) return
        // Your code here
    }



    // Yeha bata user ko profile update gareko hai
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchData(userId: String) {
        val collectionReference = FirebaseFirestore.getInstance().collection("Users")

        collectionReference.document(userId).get()
            .addOnSuccessListener { document ->
                if (isAdded) { // Check if fragment is attached
                    if (document.exists()) {
                        val fullName = document.getString("name")
                        val dob = document.getString("dob")
                        val gender = document.getString("gender")
                        val status = document.getString("status")
                        val bio = document.getString("bio")
                        val profileUrl = document.getString("profile")
                        val email = document.getString("email")
                        val userRegistered = document.getString("registeredOn")





                        // Update UI only if fragment is still attached


                        if(fullName.isNullOrBlank()){
                            binding.userFullName.setText("Not set")
                        }else {
                            binding.userFullName.setText(fullName)
                            binding.fullNameUserDetails.setText(fullName)
                            binding.logoutName.setText(fullName)
                        }

                        if(dob.isNullOrBlank()){
                            binding.dobUserDetails.setText("Not set")
                        }else{
                            binding.dobUserDetails.setText(dob)
                        }

                        if(gender.isNullOrBlank()){
                            binding.genderUserDetails.setText("Not set")
                        }else{
                            binding.genderUserDetails.setText(gender)
                        }

                        if(status.isNullOrBlank()){
                            binding.statusUserDetails.setText("Not set")
                        }else {
                            binding.statusUserDetails.setText(status)
                        }

                        if(bio.isNullOrBlank()){
                            binding.bio.setText("Not set")
                        }else{
                            binding.bio.setText(bio)
                        }

                        if(email.isNullOrBlank()){
                            binding.emailUserDetails.setText("Not set")
                        }else{
                            binding.emailUserDetails.setText(email)
                        }

                        if(userRegistered.isNullOrBlank()){
                            binding.userRegisteredOn.setText("Not set")
                        }else{
                            binding.userRegisteredOn.setText(userRegistered)
                        }

                        if(dob.isNullOrBlank()){
                            binding.ageUserDetails.setText("Not set")
                        }else{
                            val currentYear = LocalDate.now().year
                            var date = dob.toString();
                            val currentDate = LocalDate.now()

                            // Split the date string
                            val parts = dob.split("/")
                            if (parts.size != 3) {
                                throw DateTimeParseException("Invalid date format", dob, 0)
                            }

                            // Parse day, month, year from the parts
                            val day = parts[0].toInt()
                            val month = parts[1].toInt()
                            val year = parts[2].toInt()

                            // Create a LocalDate object from the parsed values
                            val birthDate = LocalDate.of(year, month, day)

                            // Calculate the age
                            var userAge = currentDate.year - birthDate.year
                            if (currentDate.dayOfYear < birthDate.dayOfYear) {
                                userAge -= 1
                            }
                            binding.ageUserDetails.setText(userAge.toString())
                        }



                        if(profileUrl.isNullOrBlank()){
                            binding.updateUserProfileImage.setImageResource(R.drawable.user_avatar)
                        }else{
                            Glide.with(this)
                                .load(profileUrl)
                                .into(binding.updateUserProfileImage)
                        }


                        binding.userVerification.setText("verified by email")


                    } else {
                        Log.d("Firestore", "No such document")
                    }
                }
            }
            .addOnFailureListener { exception ->
                if (isAdded) { // Check if fragment is attached
                    Log.w("Firestore", "Error getting document: ", exception)
                }
            }
    }



    // Yeha chai logout lo dialog banako
    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this.context)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")

        builder.setPositiveButton("Logout") { dialog, which ->
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this.context, LoginHackFie::class.java)
            startActivity(intent)
            activity?.finish()
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            // Just dismiss the dialog
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()

//            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
    }


}
