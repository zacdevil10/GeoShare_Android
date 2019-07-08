package uk.co.appsbystudio.geoshare.friends.manager.pages.current.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.friends.manager.pages.current.FriendsView
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper
import uk.co.appsbystudio.geoshare.utils.firebase.listeners.GetUserFromDatabase
import uk.co.appsbystudio.geoshare.utils.setProfilePicture
import java.util.*

class FriendsCurrentAdapter(private val context: Context?, private val userId: ArrayList<String>, private val friendsView: FriendsView) : RecyclerView.Adapter<FriendsCurrentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.friends_list_item, viewGroup, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.USERS}/${userId[position]}").addListenerForSingleValueEvent(GetUserFromDatabase(holder.name))

        if (userId.isNotEmpty()) holder.profile.setProfilePicture(userId[position], context?.cacheDir.toString())

        if (context != null) holder.more.setOnClickListener { view ->
            val popupMenu = PopupMenu(context, view)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.removeFriend -> {
                        friendsView.adapterRemoveFriend(userId[holder.adapterPosition])
                        true
                    }
                    else -> false
                }
            }
            val menuInflater = popupMenu.menuInflater
            menuInflater.inflate(R.menu.friend_menu, popupMenu.menu)
            popupMenu.show()
        }

        holder.item.setOnClickListener {
            friendsView.showProfile(userId[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return userId.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.text_name_current)
        val profile: CircleImageView = itemView.findViewById(R.id.image_profile_current)
        val more: ImageView = itemView.findViewById(R.id.image_more_current)
        val item: ConstraintLayout = itemView.findViewById(R.id.item)

    }
}
