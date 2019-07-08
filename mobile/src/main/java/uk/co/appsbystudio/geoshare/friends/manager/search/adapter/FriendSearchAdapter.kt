package uk.co.appsbystudio.geoshare.friends.manager.search.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.friends.manager.FriendsManager
import uk.co.appsbystudio.geoshare.friends.manager.search.FriendSearchView
import uk.co.appsbystudio.geoshare.utils.setProfilePicture
import java.util.*

class FriendSearchAdapter(private val context: Context, private val userMap: LinkedHashMap<String, String>, private val view: FriendSearchView) : RecyclerView.Adapter<FriendSearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.friends_search_item, viewGroup, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = userMap.values.toTypedArray()[position]

        val uid = userMap.keys.toTypedArray()[position]

        holder.profile.setProfilePicture(uid, context.cacheDir.toString())

        when {
            FriendsManager.friendsMap.containsKey(uid) -> holder.sendRequestButton.setRequestButton(R.drawable.ic_person_white_24dp, R.color.colorPrimary)
            FriendsManager.pendingUid.containsKey(uid) -> holder.sendRequestButton.setRequestButton(R.drawable.ic_person_white_24dp, android.R.color.darker_gray)
            else -> {
                holder.sendRequestButton.setRequestButton(R.drawable.ic_send_black_24dp, android.R.color.darker_gray)
                holder.sendRequestButton.setOnClickListener { view.onSendRequest(uid) }
            }
        }
    }

    private fun ImageButton.setRequestButton(drawable: Int, color: Int) {
        this@setRequestButton.setImageDrawable(context.getDrawable(drawable))
        this@setRequestButton.imageTintList = ColorStateList.valueOf(ResourcesCompat.getColor(context.resources, color, null))
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
