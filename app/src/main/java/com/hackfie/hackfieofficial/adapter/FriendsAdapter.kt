package com.hackfie.hackfieofficial.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hackfie.hackfieofficial.ChatActivity
import com.hackfie.hackfieofficial.R
import com.hackfie.hackfieofficial.UsersProfileHackFIe
import com.hackfie.hackfieofficial.databinding.SearchFriendsRecyclerLayoutBinding
import com.hackfie.hackfieofficial.model.User

class FriendsAdapter(private val userList: List<User>) : RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val binding = SearchFriendsRecyclerLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = userList.size

    class FriendsViewHolder(private val binding: SearchFriendsRecyclerLayoutBinding) : RecyclerView.ViewHolder(binding.root) {


        fun bind(user: User) {
            binding.userName.text = user.name
            binding.bio.text = user.bio
            // Load user profile image here if available



            if (user.profile.isNotEmpty()) {
                Glide.with(binding.userImage.context)
                    .load(user.profile)
                    .placeholder(R.drawable.user_avatar)
                    .into(binding.userImage)
            } else {
                binding.userImage.setImageDrawable(ContextCompat.getDrawable(binding.userImage.context, R.drawable.user_avatar))
            }

            itemView.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("fulluser", user) // Pass the user object to the profile activity
                intent.putExtra("user", user.name) // Pass the user object to the profile activity
                intent.putExtra("image", user.profile) // Pass the user object to the profile activity
                intent.putExtra("uid", user.uid) // Pass the user object to the profile activity
                context.startActivity(intent)
            }

        }
    }
}
