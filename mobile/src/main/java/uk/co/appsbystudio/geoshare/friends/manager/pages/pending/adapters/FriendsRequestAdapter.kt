package uk.co.appsbystudio.geoshare.friends.manager.pages.pending.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.friends.manager.pages.pending.FriendsPendingView
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper
import uk.co.appsbystudio.geoshare.utils.firebase.listeners.GetUserFromDatabase
import uk.co.appsbystudio.geoshare.utils.setProfilePicture
import java.util.*

class FriendsRequestAdapter(private val context: Context?,
                            private val userIncoming: ArrayList<String>,
                            private val view: FriendsPendingView) : RecyclerView.Adapter<FriendsRequestAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.friends_request_list_item, viewGroup, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.USERS}/${userIncoming[position]}").addListenerForSingleValueEvent(GetUserFromDatabase(holder.name))

        if (userIncoming.isNotEmpty()) holder.profile.setProfilePicture(userIncoming[position], context?.cacheDir.toString())

        holder.accept.setOnClickListener { view.accept(userIncoming[holder.adapterPosition], true) }

        holder.decline.setOnClickListener { view.accept(userIncoming[holder.adapterPosition], false) }
    }

    override fun getItemCount(): Int {
        return userIncoming.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.text_name_request)
        val profile: CircleImageView = itemView.findViewById(R.id.image_profile_request)
        val accept: ImageView = itemView.findViewById(R.id.image_accept_request)
        val decline: ImageView = itemView.findViewById(R.id.image_decline_request)

    }
}
