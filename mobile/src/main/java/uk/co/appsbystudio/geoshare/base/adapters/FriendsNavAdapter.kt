package uk.co.appsbystudio.geoshare.base.adapters

import android.content.Context
import android.content.SharedPreferences
import android.support.constraint.ConstraintLayout
import android.support.transition.TransitionManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.base.MainActivity
import uk.co.appsbystudio.geoshare.utils.setProfilePicture
import java.util.*

class FriendsNavAdapter(private val context: Context,
                        private val recyclerView: RecyclerView,
                        private val userId: ArrayList<String>,
                        private val hasTracking: HashMap<String, Boolean>,
                        private val callback: Callback) : RecyclerView.Adapter<FriendsNavAdapter.ViewHolder>() {

    private var sharedPreferences: SharedPreferences? = null
    private var showOnMapPreference: SharedPreferences? = null

    private var expandedPosition = -1

    interface Callback {
        fun setMarkerHidden(friendId: String, visible: Boolean)
        fun findOnMapClicked(friendId: String)
        fun sendLocationDialog(name: String, friendId: String)

        fun stopSharing(friendId: String)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FriendsNavAdapter.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.friends_nav_item, viewGroup, false)

        sharedPreferences = context.getSharedPreferences("tracking", Context.MODE_PRIVATE)
        showOnMapPreference = context.getSharedPreferences("showOnMap", Context.MODE_PRIVATE)

        return FriendsNavAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendsNavAdapter.ViewHolder, position: Int) {
        if (MainActivity.friendNames.containsKey(userId[position])) holder.friend_name.text = MainActivity.friendNames[userId[position]]

        //Set friends profile picture
        if (!userId.isEmpty()) holder.friends_pictures.setProfilePicture(userId[position], context.cacheDir.toString())

        if (hasTracking.containsKey(userId[position]) && hasTracking.getValue(userId[position])) {
            holder.trackingIndicator.visibility = View.VISIBLE
        } else {
            holder.trackingIndicator.visibility = View.GONE
        }

        val isExpanded = position == expandedPosition
        holder.expandedView.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.nameItem.isActivated = isExpanded

        if (isExpanded) {
            holder.friend_name.setTextColor(context.resources.getColor(R.color.colorAccent))
        } else {
            holder.friend_name.setTextColor(context.resources.getColor(android.R.color.white))
        }

        holder.nameItem.setOnClickListener {
            expandedPosition = if (isExpanded) -1 else holder.adapterPosition
            TransitionManager.beginDelayedTransition(recyclerView)
            notifyDataSetChanged()
        }

        holder.showOnMapCheckBox.isChecked = showOnMapPreference!!.getBoolean(userId[position], true)

        holder.showOnMapLayout.setOnClickListener { holder.showOnMapCheckBox.isChecked = !holder.showOnMapCheckBox.isChecked }

        holder.showOnMapCheckBox.setOnCheckedChangeListener { compoundButton, b -> callback.setMarkerHidden(userId[holder.adapterPosition], b) }

        if (sharedPreferences!!.getBoolean(userId[position], false)) {
            holder.sendLocationText.setText(R.string.stop_sharing)
            holder.sendLocation.setOnClickListener {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    callback.stopSharing(userId[holder.adapterPosition])
                    expandedPosition = if (isExpanded) -1 else holder.adapterPosition
                    TransitionManager.beginDelayedTransition(recyclerView)
                    notifyDataSetChanged()
                }
            }
        } else {
            holder.sendLocationText.setText(R.string.share_current_location)
            holder.sendLocation.setOnClickListener {
                callback.sendLocationDialog(holder.friend_name.text as String, userId[holder.adapterPosition])

                expandedPosition = if (isExpanded) -1 else holder.adapterPosition
                TransitionManager.beginDelayedTransition(recyclerView)
                notifyDataSetChanged()
            }
        }

        holder.findLocation.setOnClickListener {
            callback.findOnMapClicked(userId[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return userId.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val friend_name: TextView = itemView.findViewById(R.id.friend_name)
        val friends_pictures: CircleImageView = itemView.findViewById(R.id.friend_profile_image)
        val trackingIndicator: CircleImageView = itemView.findViewById(R.id.trackingIndicator)
        val sendLocation: RelativeLayout = itemView.findViewById(R.id.sendLocation)
        val sendLocationText: TextView = itemView.findViewById(R.id.sendLocationText)
        val findLocation: RelativeLayout = itemView.findViewById(R.id.findLocation)
        val findLocationText: TextView = itemView.findViewById(R.id.findLocationText)
        val nameItem: ConstraintLayout = itemView.findViewById(R.id.name_item)
        val showOnMapLayout: RelativeLayout = itemView.findViewById(R.id.showOnMapLayout)
        val showOnMapCheckBox: CheckBox = itemView.findViewById(R.id.showOnMapCheckBox)
        val expandedView: LinearLayout = itemView.findViewById(R.id.expandedView)

    }
}
