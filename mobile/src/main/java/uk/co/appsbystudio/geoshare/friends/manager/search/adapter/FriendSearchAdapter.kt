package uk.co.appsbystudio.geoshare.friends.manager.search.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.base.MainActivity
import uk.co.appsbystudio.geoshare.friends.manager.FriendsManager
import uk.co.appsbystudio.geoshare.utils.setProfilePicture
import java.util.*

class FriendSearchAdapter(private val context: Context, private val userMap: LinkedHashMap<String, String>, private val callback: Callback) : RecyclerView.Adapter<FriendSearchAdapter.ViewHolder>() {

    interface Callback {
        fun onSendRequest(friendId: String)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.friends_search_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = userMap.values.toTypedArray()[position]

        val uid = userMap.keys.toTypedArray()[position]

        if (!userMap.isEmpty()) holder.profile.setProfilePicture(uid, context.cacheDir.toString())

        when {
            MainActivity.friendsMap.containsKey(uid) -> {
                holder.sendRequestButton.setImageDrawable(context.getDrawable(R.drawable.ic_person_white_24dp))
                holder.sendRequestButton.imageTintList = ColorStateList.valueOf(context.resources.getColor(R.color.colorPrimary))
            }
            FriendsManager.pendingUid.containsKey(uid) -> {
                holder.sendRequestButton.setImageDrawable(context.getDrawable(R.drawable.ic_person_white_24dp))
                holder.sendRequestButton.imageTintList = ColorStateList.valueOf(context.resources.getColor(android.R.color.darker_gray))
            }
            else -> {
                holder.sendRequestButton.setImageDrawable(context.getDrawable(R.drawable.ic_send_black_24dp))
                holder.sendRequestButton.imageTintList = ColorStateList.valueOf(context.resources.getColor(android.R.color.darker_gray))
                holder.sendRequestButton.setOnClickListener {
                    callback.onSendRequest(uid)
                    /*holder.sendRequestButton.setImageDrawable(context.getDrawable(R.drawable.ic_person_white_24dp));
                    holder.sendRequestButton.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(android.R.color.darker_gray)));*/
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return userMap.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.text_name_search)
        val profile: CircleImageView = itemView.findViewById(R.id.image_profile_search)
        val sendRequestButton: ImageButton = itemView.findViewById(R.id.button_send_search)
        val item: ConstraintLayout = itemView.findViewById(R.id.item)

    }
}
