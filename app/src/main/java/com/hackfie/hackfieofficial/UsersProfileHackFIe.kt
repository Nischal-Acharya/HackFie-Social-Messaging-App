package com.hackfie.hackfieofficial

import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.hackfie.hackfieofficial.databinding.ActivityUsersProfileHackFieBinding
import com.hackfie.hackfieofficial.model.User
import de.hdodenhof.circleimageview.CircleImageView
import java.time.LocalDate
import java.time.format.DateTimeParseException

class UsersProfileHackFIe : AppCompatActivity() {

    private lateinit var binding: ActivityUsersProfileHackFieBinding
    private lateinit var user: User
    private var isFollowing: Boolean = false
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersProfileHackFieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        user = intent.getSerializableExtra("user") as User

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setupUserProfile()
        setupFollowButton()

        binding.followButton.setOnClickListener {
            toggleFollowStatus()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupUserProfile() {
        val userImage: CircleImageView = findViewById(R.id.updateUserProfileImage)
        val userFullName: TextView = findViewById(R.id.userFullName)
        val fullNameUserDetails: TextView = findViewById(R.id.fullNameUserDetails)
        val genderUserDetails: TextView = findViewById(R.id.genderUserDetails)
        val ageUserDetails: TextView = findViewById(R.id.ageUserDetails)
        val dobUserDetails: TextView = findViewById(R.id.dobUserDetails)
        val statusUserDetails: TextView = findViewById(R.id.statusUserDetails)
        val emailUserDetails: TextView = findViewById(R.id.emailUserDetails)
        val userRegisteredOn: TextView = findViewById(R.id.userRegisteredOn)
        val userVerification: TextView = findViewById(R.id.userVerification)
        val bio: TextView = findViewById(R.id.bio)
        val followingTextView: TextView = findViewById(R.id.following_text_view)
        val followersTextView: TextView = findViewById(R.id.followers_text_view)

        userFullName.text = user.name.ifEmpty { "Not set" }
        fullNameUserDetails.text = user.name.ifEmpty { "Not set" }
        genderUserDetails.text = user.gender.ifEmpty { "Not set" }
        dobUserDetails.text = user.dob.ifEmpty { "Not set" }
        ageUserDetails.text = if (user.dob.isNotEmpty()) calculateAge(user.dob) else "Not set"
        statusUserDetails.text = user.status.ifEmpty { "Not set" }
        emailUserDetails.text = user.email.ifEmpty { "Not set" }
        userRegisteredOn.text = user.registeredOn.ifEmpty { "Not set" }
        bio.text = user.bio.ifEmpty { "Not set" }
        userVerification.text = if (user.status == "verified") "Verified" else "verified by email"
        followingTextView.text = "Following: ${user.following.size}"
        followersTextView.text = "Followers: ${user.followers.size}"

        if (user.profile.isEmpty()) {
            userImage.setImageResource(R.drawable.user_avatar)
        } else {
            Glide.with(this)
                .load(user.profile)
                .placeholder(R.drawable.user_avatar)
                .into(userImage)
        }
    }

    private fun setupFollowButton() {
        checkIfFollowing(user.uid)
    }

    private fun toggleFollowStatus() {
        val currentUser = auth.currentUser?.uid ?: return

        if (isFollowing) {
            // Unfollow the user
            firestore.collection("Users").document(user.uid)
                .update("followers", FieldValue.arrayRemove(currentUser))
                .addOnSuccessListener {
                    firestore.collection("Users").document(currentUser)
                        .update("following", FieldValue.arrayRemove(user.uid))
                        .addOnSuccessListener {
                            isFollowing = false
                            updateFollowButton()
                        }
                }
        } else {
            // Follow the user
            firestore.collection("Users").document(user.uid)
                .update("followers", FieldValue.arrayUnion(currentUser))
                .addOnSuccessListener {
                    firestore.collection("Users").document(currentUser)
                        .update("following", FieldValue.arrayUnion(user.uid))
                        .addOnSuccessListener {
                            isFollowing = true
                            updateFollowButton()
                        }
                }
        }
    }

    private fun updateFollowButton() {
        if (isFollowing) {
            binding.followButton.text = "Unfollow"
            binding.followButton.setBackgroundColor(resources.getColor(R.color.gray))
        } else {
            binding.followButton.text = "Follow"
            binding.followButton.setBackgroundColor(resources.getColor(R.color.follow_button))
        }
        updateFollowerCount()
//        updateFollowingCount()
    }

    private fun updateFollowerCount() {
        firestore.collection("Users").document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                val followersList = document["followers"] as? List<String> ?: listOf()
                binding.followersTextView.text = "Followers: ${followersList.size}"
            }
    }

//    private fun updateFollowingCount() {
//        val currentUser = auth.currentUser?.uid ?: return
//        firestore.collection("Users").document(currentUser)
//            .get()
//            .addOnSuccessListener { document ->
//                val followingList = document["following"] as? List<String> ?: listOf()
//                binding.followingTextView.text = "Following: ${followingList.size-1}"
//            }
//    }

    private fun checkIfFollowing(userId: String) {
        val currentUser = auth.currentUser?.uid ?: return

        firestore.collection("Users").document(currentUser)
            .get()
            .addOnSuccessListener { document ->
                val followingList = document["following"] as? List<String> ?: listOf()
                isFollowing = followingList.contains(userId)
                updateFollowButton()
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateAge(dob: String): String {
        return try {
            val currentDate = LocalDate.now()
            val parts = dob.split("/")
            if (parts.size != 3) {
                throw DateTimeParseException("Invalid date format", dob, 0)
            }
            val day = parts[0].toInt()
            val month = parts[1].toInt()
            val year = parts[2].toInt()
            val birthDate = LocalDate.of(year, month, day)
            var userAge = currentDate.year - birthDate.year
            if (currentDate.dayOfYear < birthDate.dayOfYear) {
                userAge -= 1
            }
            userAge.toString()
        } catch (e: Exception) {
            "Invalid date format"
        }
    }
}
