package uk.co.appsbystudio.geoshare.friends.friendsadapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.transition.TransitionManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.appsbystudio.geoshare.Application;
import uk.co.appsbystudio.geoshare.base.MainActivity;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.utils.ProfileUtils;

public class FriendsNavAdapter extends RecyclerView.Adapter<FriendsNavAdapter.ViewHolder>{
    private final Context context;
    private final RecyclerView recyclerView;
    private final ArrayList userId;
    private final HashMap<String, Boolean> hasTracking;

    private SharedPreferences sharedPreferences;
    private SharedPreferences showOnMapPreference;

    private int expandedPosition = -1;

    public interface Callback {
        void setMarkerHidden(String friendId, boolean visible);
        void findOnMapClicked(String friendId);
        void sendLocationDialog(String name, String friendId);

        void stopSharing(FirebaseUser user, String friendId);
    }

    private final Callback callback;

    public FriendsNavAdapter(Context context, RecyclerView recyclerView, ArrayList userId, HashMap<String, Boolean> hasTracking, Callback callback) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.userId = userId;
        this.hasTracking = hasTracking;
        this.callback = callback;
    }

    @Override
    public FriendsNavAdapter.ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friends_nav_item, viewGroup, false);

        sharedPreferences = Application.getContext().getSharedPreferences("tracking", Context.MODE_PRIVATE);
        showOnMapPreference = Application.getContext().getSharedPreferences("showOnMap", Context.MODE_PRIVATE);

        return new FriendsNavAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FriendsNavAdapter.ViewHolder holder, int position) {
        if (MainActivity.Companion.getFriendNames().containsKey(userId.get(position).toString())) holder.friend_name.setText(MainActivity.Companion.getFriendNames().get(userId.get(position).toString()));

        //Set friends profile picture
        if (!userId.isEmpty()) ProfileUtils.setProfilePicture(userId.get(position).toString(), holder.friends_pictures, context.getCacheDir().toString());

        if (hasTracking.containsKey(userId.get(position).toString()) && hasTracking.get(userId.get(position).toString())) {
            holder.trackingIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.trackingIndicator.setVisibility(View.GONE);
        }

        final boolean isExpanded = position == expandedPosition;
        holder.expandedView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.nameItem.setActivated(isExpanded);

        if (isExpanded) {
            holder.friend_name.setTextColor(Application.getContext().getResources().getColor(R.color.colorAccent));
        } else {
            holder.friend_name.setTextColor(Application.getContext().getResources().getColor(android.R.color.white));
        }

        holder.nameItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandedPosition = isExpanded ? -1:holder.getAdapterPosition();
                TransitionManager.beginDelayedTransition(recyclerView);
                notifyDataSetChanged();
            }
        });

        holder.showOnMapCheckBox.setChecked(showOnMapPreference.getBoolean(userId.get(position).toString(), true));

        holder.showOnMapLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.showOnMapCheckBox.setChecked(!holder.showOnMapCheckBox.isChecked());
            }
        });

        holder.showOnMapCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                callback.setMarkerHidden(userId.get(holder.getAdapterPosition()).toString(), b);
            }
        });

        if (sharedPreferences.getBoolean(userId.get(position).toString(), false)) {
            holder.sendLocationText.setText(R.string.stop_sharing);
            holder.sendLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        callback.stopSharing(user, userId.get(holder.getAdapterPosition()).toString());
                    }
                }
            });
        } else {
            holder.sendLocationText.setText(R.string.share_current_location);
            holder.sendLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.sendLocationDialog((String) holder.friend_name.getText(), userId.get(holder.getAdapterPosition()).toString());

                    expandedPosition = isExpanded ? -1:holder.getAdapterPosition();
                    TransitionManager.beginDelayedTransition(recyclerView);
                    notifyDataSetChanged();

                }
            });
        }

        holder.findLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Adapter: " + userId);
                callback.findOnMapClicked(userId.get(holder.getAdapterPosition()).toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return userId.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        final TextView friend_name;
        final CircleImageView friends_pictures;
        final CircleImageView trackingIndicator;
        final ImageView arrow;
        final RelativeLayout sendLocation;
        final TextView sendLocationText;
        final RelativeLayout findLocation;
        final TextView findLocationText;
        final ConstraintLayout nameItem;
        final RelativeLayout showOnMapLayout;
        final CheckBox showOnMapCheckBox;
        final LinearLayout expandedView;

        ViewHolder(View itemView) {
            super(itemView);
            friend_name = itemView.findViewById(R.id.friend_name);
            friends_pictures = itemView.findViewById(R.id.friend_profile_image);
            trackingIndicator = itemView.findViewById(R.id.trackingIndicator);
            arrow = itemView.findViewById(R.id.more);
            sendLocation = itemView.findViewById(R.id.sendLocation);
            sendLocationText = itemView.findViewById(R.id.sendLocationText);
            findLocation = itemView.findViewById(R.id.findLocation);
            findLocationText = itemView.findViewById(R.id.findLocationText);
            nameItem = itemView.findViewById(R.id.name_item);
            showOnMapLayout = itemView.findViewById(R.id.showOnMapLayout);
            showOnMapCheckBox = itemView.findViewById(R.id.showOnMapCheckBox);
            expandedView = itemView.findViewById(R.id.expandedView);
        }
    }
}
