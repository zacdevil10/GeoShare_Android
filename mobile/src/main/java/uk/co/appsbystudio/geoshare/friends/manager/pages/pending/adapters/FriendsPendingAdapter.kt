package uk.co.appsbystudio.geoshare.friends.manager.pages.pending.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.google.firebase.database.FirebaseDatabase

import java.util.ArrayList

import de.hdodenhof.circleimageview.CircleImageView
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.utils.ProfileUtils
import uk.co.appsbystudio.geoshare.utils.firebase.listeners.GetUserFromDatabase

class FriendsPendingAdapter(private val context: Context?,
                            private val uid: ArrayList<String>,
                            private val callback: Callback) : RecyclerView.Adapter<FriendsPendingAdapter.ViewHolder>() {

    interface Callback {
        fun onReject(uid: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.friends_pending_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        FirebaseDatabase.getInstance().reference.addListenerForSingleValueEvent(GetUserFromDatabase(uid[position], holder.name))

        if (!uid.isEmpty()) ProfileUtils.setProfilePicture(uid[position], holder.profile, context?.cacheDir.toString())

        holder.decline.setOnClickListener { callback.onReject(uid[holder.adapterPosition]) }
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
