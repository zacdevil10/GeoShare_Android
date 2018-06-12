package uk.co.appsbystudio.geoshare.friends.manager.pages.current.adapter

import android.content.Context
import android.content.Intent
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.friends.profile.ProfileActivity
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper
import uk.co.appsbystudio.geoshare.utils.firebase.listeners.GetUserFromDatabase
import uk.co.appsbystudio.geoshare.utils.setProfilePicture
import java.util.*

class FriendsCurrentAdapter(private val context: Context?, private val userId: ArrayList<String>, private val callback: Callback) : RecyclerView.Adapter<FriendsCurrentAdapter.ViewHolder>() {

    interface Callback {
        fun onRemoveFriend(friendId: String)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.friends_list_item, viewGroup, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.USERS}/${userId[position]}").addListenerForSingleValueEvent(GetUserFromDatabase(holder.name))

        if (!userId.isEmpty()) holder.profile.setProfilePicture(userId[position].toString(), context?.cacheDir.toString())

        holder.more.setOnClickListener { view ->
            val popupMenu = PopupMenu(context!!, view)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.removeFriend -> {
                        callback.onRemoveFriend(userId[holder.adapterPosition].toString())
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
            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra("uid", userId[holder.adapterPosition].toString())
            context?.startActivity(intent)
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
