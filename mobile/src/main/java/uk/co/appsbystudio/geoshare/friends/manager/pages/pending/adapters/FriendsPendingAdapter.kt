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

class FriendsPendingAdapter(private val context: Context?,
                            private val uid: ArrayList<String>,
                            private val view: FriendsPendingView) : RecyclerView.Adapter<FriendsPendingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.friends_pending_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.USERS}/${uid[position]}").addListenerForSingleValueEvent(GetUserFromDatabase(holder.name))

        if (uid.isNotEmpty()) holder.profile.setProfilePicture(uid[position], context?.cacheDir.toString())

        holder.decline.setOnClickListener { view.accept(uid[holder.adapterPosition], false) }
    }

    override fun getItemCount(): Int {
        return uid.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val name: TextView = view.findViewById(R.id.text_name_pending)
        val profile: CircleImageView = view.findViewById(R.id.image_profile_pending)
        val decline: ImageView = view.findViewById(R.id.image_remove_pending)

    }
}
