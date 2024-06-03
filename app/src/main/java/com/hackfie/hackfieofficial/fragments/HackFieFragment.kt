package com.hackfie.hackfieofficial.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hackfie.hackfieofficial.R
import com.hackfie.hackfieofficial.adapter.FriendsAdapter
import com.hackfie.hackfieofficial.databinding.FragmentHackFieBinding
import com.hackfie.hackfieofficial.model.AboutDevelopers
import com.hackfie.hackfieofficial.model.User

class HackFieFragment : Fragment() {

    private lateinit var binding: FragmentHackFieBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var friendsAdapter: FriendsAdapter
    private lateinit var userList: MutableList<User>

    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using View Binding
        binding = FragmentHackFieBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.materialToolbar)

        // Set up the toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.materialToolbar)

        // Progressbar initialized
        progressBar = binding.progressBar

        // Enable back navigation
        binding.materialToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        userList = mutableListOf()
        friendsAdapter = FriendsAdapter(userList)

        binding.userList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = friendsAdapter
        }

        fetchMutualFollowers()

        // Returning the root view
        return binding.root
    }

    private fun fetchMutualFollowers() {
        progressBar.visibility = View.VISIBLE

        val currentUserId = mAuth.currentUser?.uid
        if (currentUserId != null) {
            db.collection("Users").document(currentUserId).get().addOnSuccessListener { document ->
                progressBar.visibility = View.GONE

                if (document != null) {
                    val followers = document["followers"] as? List<String> ?: emptyList()
                    val following = document["following"] as? List<String> ?: emptyList()

                    if (followers.isEmpty() || following.isEmpty()) {
                        binding.userList.visibility = View.GONE
                        binding.noUserFound.visibility = View.VISIBLE
                        return@addOnSuccessListener
                    }

                    val mutualFollowers = followers.intersect(following).toList()
                    if (mutualFollowers.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "No mutual followers found.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        fetchUsers(mutualFollowers)
                    }
                } else {
                    progressBar.visibility = View.GONE
                    Log.d("HackFieFragment", "No such document")
                    Toast.makeText(requireContext(), "No user data found.", Toast.LENGTH_SHORT)
                        .show()
                }
            }.addOnFailureListener { exception ->
                progressBar.visibility = View.GONE
                Log.d("HackFieFragment", "get failed with ", exception)
                Toast.makeText(requireContext(), "Failed to fetch user data.", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), "User not authenticated.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchUsers(mutualFollowers: List<String>) {
        progressBar.visibility = View.VISIBLE

        if (mutualFollowers.isEmpty()) {
            progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), "No mutual followers to display.", Toast.LENGTH_SHORT)
                .show()
            return
        }

        db.collection("Users").whereIn("uid", mutualFollowers).get()
            .addOnSuccessListener { documents ->
                progressBar.visibility = View.GONE

                if (documents.isEmpty) {
                    Toast.makeText(requireContext(), "No users found.", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                for (document in documents) {
                    val user = document.toObject(User::class.java)
                    userList.add(user)
                }
                friendsAdapter.notifyDataSetChanged()
            }.addOnFailureListener { exception ->
            progressBar.visibility = View.GONE
            Log.d("HackFieFragment", "Error getting documents: ", exception)
            Toast.makeText(requireContext(), "Error fetching users.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_friends_top_bar_activity, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.addDevelopers -> {
                showLogoutDialog()
                true
            }
            R.id.developers -> {
                val intent = Intent(requireContext(), AboutDevelopers::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Followers")
        builder.setMessage(
            "In this activity, only individuals who have followed you will be visible, and you can only chat with those who have followed you back. \n\n\nTo view users on this list:\n" +
                    "\n" +
                    "Navigate to HackFie > Search > Follow Users > Await mutual follow-back."
        )

        builder.setNegativeButton("Okay") { dialog, which ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
