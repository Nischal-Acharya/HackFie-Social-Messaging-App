package com.hackfie.hackfieofficial

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.hackfie.hackfieofficial.adapter.MessagesAdapter
import com.hackfie.hackfieofficial.databinding.ActivityChatBinding
import com.hackfie.hackfieofficial.model.Message
import com.hackfie.hackfieofficial.model.User
import java.util.*

class ChatActivity : AppCompatActivity() {

    private var binding: ActivityChatBinding? = null
    private var adapter: MessagesAdapter? = null
    private var messages: ArrayList<Message>? = null
    private var senderRoom: String? = null
    private var receiverRoom: String? = null
    private var database: FirebaseDatabase? = null
    private var storage: FirebaseStorage? = null
    private var dialog: ProgressDialog? = null
    private var senderUid: String? = null
    private var receiverUid: String? = null


    // yo chai calling ra video calling ko lagi gareko
    private lateinit var userID: String
    private lateinit var userName: String
    private lateinit var userIdTextField: EditText
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setSupportActionBar(binding!!.toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        dialog = ProgressDialog(this)
        dialog!!.setMessage("Uploading image...")
        dialog!!.setCancelable(false)
        messages = ArrayList()

        val name = intent.getStringExtra("user")
        val profile = intent.getStringExtra("image")
        receiverUid = intent.getStringExtra("uid")
        senderUid = FirebaseAuth.getInstance().uid
        senderRoom = senderUid + receiverUid
        receiverRoom = receiverUid + senderUid
        val user = intent.getSerializableExtra("fulluser") as User


        // setting target user id target_user_id text


        userName = user.name
        userID = senderUid!!



        binding!!.name.text = name

        if (profile!!.isNotEmpty()) {
            Glide.with(binding!!.profile01.context).load(profile)
                .placeholder(R.drawable.user_avatar).into(binding!!.profile01)
        } else {
            binding!!.profile01.setImageDrawable(
                ContextCompat.getDrawable(
                    binding!!.profile01.context, R.drawable.user_avatar
                )
            )
        }

        binding!!.profile01.setOnClickListener {
            intent = Intent(this, UsersProfileHackFIe::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
        }


        // call and video call setup
        binding!!.imageViewCall.setOnClickListener {
            Toast.makeText(this, "Sorry la yo feature ma paxi halxu", Toast.LENGTH_SHORT).show()


        }

        binding!!.imageViewVideoCall.setOnClickListener {
            Toast.makeText(this, "Sorry la yo feature ma paxi halxu", Toast.LENGTH_SHORT).show()

        }


        database!!.reference.child("Presence").child(receiverUid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val status = snapshot.getValue(String::class.java)
                        if (status == "offline") {
                            binding!!.status.visibility = View.GONE
                        } else {
                            binding!!.status.text = status
                            binding!!.status.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        adapter = MessagesAdapter(
            this@ChatActivity, messages, senderRoom!!, receiverRoom!!, profile, user
        )

        binding!!.recyclerView.layoutManager = LinearLayoutManager(this@ChatActivity)
        binding!!.recyclerView.adapter = adapter

        database!!.reference.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messages!!.clear()
                    for (snapshot1 in snapshot.children) {
                        val message = snapshot1.getValue(Message::class.java)
                        message!!.messageId = snapshot1.key
                        messages!!.add(message)
                    }
                    adapter!!.notifyDataSetChanged()
                    binding!!.recyclerView.scrollToPosition(messages!!.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })

        binding!!.sendBtn.setOnClickListener {
            val messageTxt = binding!!.messageBox.text.toString()
            if (messageTxt.isNullOrBlank()) {
                Toast.makeText(this, "Aachoi, jot moi click nagara naa", Toast.LENGTH_SHORT).show()

            } else {

                val date = Date()
                val message = Message(messageTxt, senderUid, date.time)
                binding!!.messageBox.setText("")
                val randomKey = database!!.reference.push().key
                val lastMsgObj = HashMap<String, Any>()
                lastMsgObj["lastMsg"] = message.message!!
                lastMsgObj["lastMsgTime"] = date.time
                database!!.reference.child("chats").child(senderRoom!!).updateChildren(lastMsgObj)
                database!!.reference.child("chats").child(receiverRoom!!).updateChildren(lastMsgObj)
                database!!.reference.child("chats").child(senderRoom!!).child("messages")
                    .child(randomKey!!).setValue(message).addOnSuccessListener {
                        database!!.reference.child("chats").child(receiverRoom!!).child("messages")
                            .child(randomKey).setValue(message)
                        binding!!.recyclerView.scrollToPosition(messages!!.size - 1)
                    }
            }

        }

        binding!!.attachment.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            // confrim to send dialog

            showLogoutDialog(intent)
        }

        val handler = Handler()
        binding!!.messageBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                database!!.reference.child("Presence").child(senderUid!!).setValue("typing...")
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(userStoppedTyping, 1000)
            }

            var userStoppedTyping = Runnable {
                database!!.reference.child("Presence").child(senderUid!!).setValue("Online")
            }
        })

        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding!!.imageViewBack.setOnClickListener {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 25 && resultCode == RESULT_OK) {
            if (data != null && data.data != null) {
                val selectedImage = data.data
                val calendar = Calendar.getInstance()
                val reference =
                    storage!!.reference.child("chats").child(calendar.timeInMillis.toString() + "")
                dialog!!.show()
                reference.putFile(selectedImage!!).addOnCompleteListener { task ->
                    dialog!!.dismiss()
                    if (task.isSuccessful) {
                        reference.downloadUrl.addOnSuccessListener { uri ->
                            val filePath = uri.toString()
                            val messageTxt = binding!!.messageBox.text.toString()
                            val date = Date()
                            val message = Message(messageTxt, senderUid, date.time)
                            message.message = "photo"
                            message.imageUrl = filePath
                            binding!!.messageBox.setText("")
                            val randomKey = database!!.reference.push().key
                            val lastMsgObj = HashMap<String, Any>()
                            lastMsgObj["lastMsg"] = message.message!!
                            lastMsgObj["lastMsgTime"] = date.time
                            database!!.reference.child("chats").child(senderRoom!!)
                                .updateChildren(lastMsgObj)
                            database!!.reference.child("chats").child(receiverRoom!!)
                                .updateChildren(lastMsgObj)
                            database!!.reference.child("chats").child(senderRoom!!)
                                .child("messages").child(randomKey!!).setValue(message)
                                .addOnSuccessListener {
                                    database!!.reference.child("chats").child(receiverRoom!!)
                                        .child("messages").child(randomKey).setValue(message)
                                    binding!!.recyclerView.scrollToPosition(messages!!.size - 1)
                                }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("Presence").child(currentId!!).setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("Presence").child(currentId!!).setValue("offline")
    }


    private fun showLogoutDialog(intent: Intent) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Image Sending")
        builder.setMessage("The image you are going to select will be sent to your friend.")

        builder.setPositiveButton("Yeah Got it") { dialog, which ->
            startActivityForResult(intent, 25)

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
