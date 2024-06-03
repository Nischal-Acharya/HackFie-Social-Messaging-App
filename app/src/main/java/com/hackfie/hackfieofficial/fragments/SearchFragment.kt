package com.hackfie.hackfieofficial.fragments

import UserAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hackfie.hackfieofficial.R
//import com.hackfie.hackfieofficial.adapters.UserAdapter
import com.hackfie.hackfieofficial.model.User

class SearchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderText: TextView
    private lateinit var searchView: SearchView
    private lateinit var userAdapter: UserAdapter
    private val userList = mutableListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_developers, container, false)

        searchView = view.findViewById(R.id.search_view)
        placeholderText = view.findViewById(R.id.placeholder_text)
        recyclerView = view.findViewById(R.id.recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        userAdapter = UserAdapter(userList, requireContext()) // Pass context to UserAdapter constructor
        recyclerView.adapter = userAdapter

        setupSearchView()

        return view









    }

    private fun setupSearchView() {
        searchView.setIconifiedByDefault(false)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    performSearch(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    performSearch(it)
                }
                return true
            }
        })
    }

    private fun performSearch(query: String) {
        val db = FirebaseFirestore.getInstance()
        val usersRef = db.collection("Users")
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        var matchCount = 0  // Keep track of the number of matches found

        usersRef.get()
            .addOnSuccessListener { documents ->
                userList.clear()
                for (document in documents) {
                    if (matchCount >= 20) break  // Stop searching once 20 matches are found

                    val user = document.toObject(User::class.java)
                    val userName = user.name.toLowerCase()
                    val searchQuery = query.toLowerCase()

                    // Exclude current user from search results
                    if (userName.contains(searchQuery) && user.uid != currentUserUid) {
                        userList.add(user)
                        matchCount++  // Increment match count
                    }
                }
                if (userList.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    placeholderText.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    placeholderText.visibility = View.GONE
                    userAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "Error getting documents: ", exception)
                recyclerView.visibility = View.GONE
                placeholderText.visibility = View.VISIBLE
            }
    }
}
