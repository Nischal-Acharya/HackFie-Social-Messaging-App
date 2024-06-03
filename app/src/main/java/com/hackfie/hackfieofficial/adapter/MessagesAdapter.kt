package com.hackfie.hackfieofficial.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hackfie.hackfieofficial.ChatActivity
import com.hackfie.hackfieofficial.R
import com.hackfie.hackfieofficial.UsersProfileHackFIe
import com.hackfie.hackfieofficial.databinding.DeleteLayoutBinding
import com.hackfie.hackfieofficial.databinding.SendMsgBinding
import com.hackfie.hackfieofficial.databinding.ReceiveMsgBinding
import com.hackfie.hackfieofficial.model.Message
import com.hackfie.hackfieofficial.model.User

class MessagesAdapter(
    var context: Context,
    var messages: ArrayList<Message>?,
    var senderRoom: String,
    var receiverRoom: String,
    var profileImage: String,
    var full_user: User
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_SENT = 1
    val ITEM_RECEIVE = 2

    init {
        this.messages = messages ?: ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_SENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.send_msg, parent, false)
            SentMsgHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.receive_msg, parent, false)
            // receive msg left user image update
            val userImageView = view.findViewById<ImageView>(R.id.user_image)
            if (profileImage.isNotEmpty()) {
                Glide.with(parent.context)
                    .load(profileImage)
                    .placeholder(R.drawable.image_loading_placeholder)
                    .into(userImageView)
            }

            // Set OnClickListener for the profile image
            userImageView.setOnClickListener {
                val context = parent.context
                val intent = Intent(context, UsersProfileHackFIe::class.java).apply {
                    putExtra("user", full_user) // Pass the user object to the profile activity
//                    Toast.makeText(context, "Profile Clicked", Toast.LENGTH_SHORT).show()
                }
                context.startActivity(intent)
            }

            ReceiverMsgHolder(view)
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (FirebaseAuth.getInstance().uid == messages!![position].senderId) {
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int {
        return messages!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages!![position]

        if (holder is SentMsgHolder) {
            if (message.message == "photo") {
                holder.binding.image.visibility = View.VISIBLE
                holder.binding.message.visibility = View.GONE
                Glide.with(context).load(message.imageUrl).placeholder(R.drawable.image_loading_placeholder).into(holder.binding.image)
            } else {
                holder.binding.image.visibility = View.GONE
                holder.binding.message.visibility = View.VISIBLE
                holder.binding.message.text = message.message
            }

            holder.itemView.setOnLongClickListener {
                val view = LayoutInflater.from(context).inflate(R.layout.delete_layout, null)
                val binding: DeleteLayoutBinding = DeleteLayoutBinding.bind(view)
                val dialog = AlertDialog.Builder(context)
                    .setTitle("Delete Message")
                    .setView(binding.root)
                    .create()

                binding.everyone.setOnClickListener {
                    message.message = "This message is removed."
                    message.messageId?.let { id ->
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(senderRoom).child("messages").child(id).setValue(message)
                    }
                    message.messageId?.let { id ->
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(receiverRoom).child("messages").child(id).setValue(message)
                    }
                    dialog.dismiss()
                }

                binding.delete.setOnClickListener {
                    message.messageId?.let { id ->
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(senderRoom).child("messages").child(id).setValue(null)
                    }
                    dialog.dismiss()
                }

                binding.cancel.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
                true
            }
        } else if (holder is ReceiverMsgHolder) {
            if (message.message == "photo") {
                holder.binding.image.visibility = View.VISIBLE
                holder.binding.message.visibility = View.GONE
                Glide.with(context).load(message.imageUrl).placeholder(R.drawable.image_loading_placeholder).into(holder.binding.image)
            } else {
                holder.binding.image.visibility = View.GONE
                holder.binding.message.visibility = View.VISIBLE
                holder.binding.message.text = message.message
            }

            // Remove the long click listener for received messages
            holder.itemView.setOnLongClickListener(null)
        }
    }


    inner class SentMsgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: SendMsgBinding = SendMsgBinding.bind(itemView)
    }

    inner class ReceiverMsgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: ReceiveMsgBinding = ReceiveMsgBinding.bind(itemView)
    }
}
