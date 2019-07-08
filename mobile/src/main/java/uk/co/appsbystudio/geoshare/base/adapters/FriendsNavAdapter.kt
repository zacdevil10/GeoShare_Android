package uk.co.appsbystudio.geoshare.base.adapters

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.utils.setProfilePicture

class FriendsNavAdapter(private val context: Context,
                        private val recyclerView: RecyclerView,
                        private val friends: LinkedHashMap<String, String>,
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

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.friends_nav_item, viewGroup, false)

        sharedPreferences = context.getSharedPreferences("tracking", Context.MODE_PRIVATE)
        showOnMapPreference = context.getSharedPreferences("showOnMap", Context.MODE_PRIVATE)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.friendName.text = friends.values.toTypedArray()[position]

        val uid = friends.keys.toTypedArray()[position]

        //Set friends profile picture
        holder.friendsPictures.setProfilePicture(uid, context.cacheDir.toString())

        if (hasTracking.containsKey(uid) && hasTracking.getValue(uid)) {
            holder.trackingIndicator.visibility = View.VISIBLE
        } else {
            holder.trackingIndicator.visibility = View.GONE
        }

        val isExpanded = position == expandedPosition
        holder.expandedView.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.nameItem.isActivated = isExpanded

        if (isExpanded) {
            holder.friendName.setTextColor(ResourcesCompat.getColor(context.resources, R.color.colorAccent, null))
        } else {
            holder.friendName.setTextColor(ResourcesCompat.getColor(context.resources, android.R.color.white, null))
        }

        holder.nameItem.setOnClickListener {
            expandedPosition = if (isExpanded) -1 else holder.adapterPosition
            TransitionManager.beginDelayedTransition(recyclerView)
            notifyDataSetChanged()
        }

        holder.showOnMapCheckBox.isChecked = showOnMapPreference!!.getBoolean(uid, true)

        holder.showOnMapLayout.setOnClickListener { holder.showOnMapCheckBox.isChecked = !holder.showOnMapCheckBox.isChecked }

        holder.showOnMapCheckBox.setOnCheckedChangeListener { compoundButton, b -> callback.setMarkerHidden(uid, b) }

        if (sharedPreferences!!.getBoolean(uid, false)) {
            holder.sendLocationText.setText(R.string.stop_sharing)
            holder.sendLocation.setOnClickListener {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    callback.stopSharing(uid)
                    expandedPosition = if (isExpanded) -1 else holder.adapterPosition
                    TransitionManager.beginDelayedTransition(recyclerView)
                    notifyDataSetChanged()
                }
            }
        } else {
            holder.sendLocationText.setText(R.string.share_current_location)
            holder.sendLocation.setOnClickListener {
                callback.sendLocationDialog(holder.friendName.text as String, uid)

                expandedPosition = if (isExpanded) -1 else holder.adapterPosition
                TransitionManager.beginDelayedTransition(recyclerView)
                notifyDataSetChanged()
            }
        }

        holder.findLocation.setOnClickListener {
            callback.findOnMapClicked(uid)
        }
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val friendName: TextView = itemView.findViewById(R.id.friend_name)
        val friendsPictures: CircleImageView = itemView.findViewById(R.id.friend_profile_image)
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
