package uk.co.appsbystudio.geoshare.friends.profile.friends.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.base.MainActivity
import uk.co.appsbystudio.geoshare.utils.ProfileUtils
import uk.co.appsbystudio.geoshare.utils.firebase.listeners.GetUserFromDatabase
import java.util.*

class FriendshipStatusAdapter(private val context: Context?,
                              private val userId: ArrayList<String>,
                              private val callback: Callback?) : RecyclerView.Adapter<FriendshipStatusAdapter.ViewHolder>() {

    interface Callback {
        fun onSendRequest(friendId: String)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.friends_search_item, viewGroup, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        FirebaseDatabase.getInstance().reference.addListenerForSingleValueEvent(GetUserFromDatabase(userId[position], holder.name))

        if (!userId.isEmpty()) ProfileUtils.setProfilePicture(userId[position], holder.profile, context?.cacheDir.toString())

        when {
            MainActivity.friendsId.containsKey(userId[position]) -> {
                holder.sendRequestButton.setImageDrawable(context?.getDrawable(R.drawable.ic_person_white_24dp))
                holder.sendRequestButton.imageTintList = ColorStateList.valueOf(context?.resources?.getColor(R.color.colorPrimary)!!)
            }
            MainActivity.pendingId.containsKey(userId[position]) -> {
                holder.sendRequestButton.setImageDrawable(context?.getDrawable(R.drawable.ic_person_white_24dp))
                holder.sendRequestButton.imageTintList = ColorStateList.valueOf(context?.resources?.getColor(android.R.color.darker_gray)!!)
            }
            else -> {
                holder.sendRequestButton.setImageDrawable(context?.getDrawable(R.drawable.ic_send_black_24dp))
                holder.sendRequestButton.imageTintList = ColorStateList.valueOf(context?.resources?.getColor(android.R.color.darker_gray)!!)
                holder.sendRequestButton.setOnClickListener {
                    callback?.onSendRequest(userId[holder.adapterPosition])
                    /*holder.sendRequestButton.setImageDrawable(context.getDrawable(R.drawable.ic_person_white_24dp));
                    holder.sendRequestButton.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(android.R.color.darker_gray)));*/
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return userId.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.text_name_search)
        val profile: CircleImageView = itemView.findViewById(R.id.image_profile_search)
        val sendRequestButton: ImageButton = itemView.findViewById(R.id.button_send_search)
        val item: ConstraintLayout = itemView.findViewById(R.id.item)

    }
}