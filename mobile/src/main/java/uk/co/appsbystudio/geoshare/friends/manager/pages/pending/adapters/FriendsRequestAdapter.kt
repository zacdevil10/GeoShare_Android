package uk.co.appsbystudio.geoshare.friends.manager.pages.pending.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.utils.ProfileUtils
import uk.co.appsbystudio.geoshare.utils.firebase.listeners.GetUserFromDatabase
import java.util.*

class FriendsRequestAdapter(private val context: Context?,
                            private val userIncoming: ArrayList<String>,
                            private val callback: Callback) : RecyclerView.Adapter<FriendsRequestAdapter.ViewHolder>() {

    interface Callback {
        fun onAcceptReject(accept: Boolean?, uid: String)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.friends_request_list_item, viewGroup, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        FirebaseDatabase.getInstance().reference.addListenerForSingleValueEvent(GetUserFromDatabase(userIncoming[position], holder.name))

        if (!userIncoming.isEmpty()) ProfileUtils.setProfilePicture(userIncoming[position], holder.profile, context?.cacheDir.toString())

        holder.accept.setOnClickListener { callback.onAcceptReject(true, userIncoming[holder.adapterPosition]) }

        holder.decline.setOnClickListener { callback.onAcceptReject(false, userIncoming[holder.adapterPosition]) }
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
