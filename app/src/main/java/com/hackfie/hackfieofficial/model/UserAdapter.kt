import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.hackfie.hackfieofficial.R
import com.hackfie.hackfieofficial.UsersProfileHackFIe
import com.hackfie.hackfieofficial.model.User

class UserAdapter(private val userList: List<User>, private val context: Context) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.user_name)
        val bio: TextView = itemView.findViewById(R.id.bio)
        val followButton: Button = itemView.findViewById(R.id.follow_button)
        val userImage: ImageView = itemView.findViewById(R.id.user_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_user_with_follow_layout, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        val currentUser = auth.currentUser?.uid ?: return

        holder.userName.text = user.name
        holder.bio.text = user.bio

        if (user.profile.isNotEmpty()) {
            Glide.with(holder.userImage.context)
                .load(user.profile)
                .placeholder(R.drawable.user_avatar)
                .into(holder.userImage)
        } else {
            holder.userImage.setImageDrawable(ContextCompat.getDrawable(holder.userImage.context, R.drawable.user_avatar))
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, UsersProfileHackFIe::class.java)
            intent.putExtra("user", user) // Pass the user object to the profile activity
            context.startActivity(intent)
        }

        updateFollowButton(currentUser, user.uid, holder.followButton)

        holder.followButton.setOnClickListener {
            if (holder.followButton.text == "Follow") {
                followUser(user.uid, currentUser, holder.followButton)
            } else {
                unfollowUser(user.uid, currentUser, holder.followButton)
            }
        }
    }

    override fun getItemCount() = userList.size

    private fun updateFollowButton(currentUser: String, userId: String, followButton: Button) {
        firestore.collection("Users").document(currentUser)
            .get()
            .addOnSuccessListener { document ->
                val followingList = document.get("following") as? List<String> ?: listOf()
                if (followingList.contains(userId)) {
                    followButton.text = "Unfollow"
                    followButton.setBackgroundColor(ContextCompat.getColor(context, R.color.gray))
                } else {
                    followButton.text = "Follow"
                    followButton.setBackgroundColor(ContextCompat.getColor(context, R.color.follow_button))
                }
            }
            .addOnFailureListener { exception ->
                Log.e("UserAdapter", "Error getting following list: ", exception)
            }
    }

    private fun followUser(userId: String, currentUser: String, followButton: Button) {
        val currentUserRef = firestore.collection("Users").document(currentUser)
        val followedUserRef = firestore.collection("Users").document(userId)

        firestore.runTransaction { transaction ->
            transaction.update(currentUserRef, "following", FieldValue.arrayUnion(userId))
            transaction.update(followedUserRef, "followers", FieldValue.arrayUnion(currentUser))
        }.addOnSuccessListener {
            followButton.text = "Unfollow  "
            followButton.setBackgroundColor(ContextCompat.getColor(context, R.color.gray))
        }.addOnFailureListener { exception ->
            Log.e("UserAdapter", "Error following user: ", exception)
        }
    }

    private fun unfollowUser(userId: String, currentUser: String, followButton: Button) {
        val currentUserRef = firestore.collection("Users").document(currentUser)
        val followedUserRef = firestore.collection("Users").document(userId)

        firestore.runTransaction { transaction ->
            transaction.update(currentUserRef, "following", FieldValue.arrayRemove(userId))
            transaction.update(followedUserRef, "followers", FieldValue.arrayRemove(currentUser))
        }.addOnSuccessListener {
            followButton.text = "Follow"
            followButton.setBackgroundColor(ContextCompat.getColor(context, R.color.follow_button))
        }.addOnFailureListener { exception ->
            Log.e("UserAdapter", "Error unfollowing user: ", exception)
        }
    }
}
