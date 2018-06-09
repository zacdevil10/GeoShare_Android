package uk.co.appsbystudio.geoshare.friends.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.appsbystudio.geoshare.base.MainActivity;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.utils.ProfileUtils;
import uk.co.appsbystudio.geoshare.utils.firebase.listeners.GetUserFromDatabase;

public class FriendshipStatusAdapter extends RecyclerView.Adapter<FriendshipStatusAdapter.ViewHolder>{

    private final Context context;
    private final ArrayList userId;

    public interface Callback{
        void onSendRequest(String friendId);
    }

    private final Callback callback;

    public FriendshipStatusAdapter(Context context, ArrayList userId, Callback callback) {
        this.context = context;
        this.userId = userId;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int viewType) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friends_search_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new GetUserFromDatabase(userId.get(position).toString(), holder.friend_name));

        if (!userId.isEmpty()) ProfileUtils.setProfilePicture(userId.get(position).toString(), holder.friends_pictures, context.getCacheDir().toString());

        if (MainActivity.Companion.getFriendsId().containsKey(userId.get(position).toString())) {
            holder.sendRequestButton.setImageDrawable(context.getDrawable(R.drawable.ic_person_white_24dp));
            holder.sendRequestButton.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary)));
        } else if (MainActivity.Companion.getPendingId().containsKey(userId.get(position).toString())) {
            holder.sendRequestButton.setImageDrawable(context.getDrawable(R.drawable.ic_person_white_24dp));
            holder.sendRequestButton.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(android.R.color.darker_gray)));
        } else {
            holder.sendRequestButton.setImageDrawable(context.getDrawable(R.drawable.ic_send_black_24dp));
            holder.sendRequestButton.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(android.R.color.darker_gray)));
            holder.sendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onSendRequest(userId.get(holder.getAdapterPosition()).toString());
                    /*holder.sendRequestButton.setImageDrawable(context.getDrawable(R.drawable.ic_person_white_24dp));
                    holder.sendRequestButton.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(android.R.color.darker_gray)));*/
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return userId.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        final TextView friend_name;
        final CircleImageView friends_pictures;
        final ImageButton sendRequestButton;
        final RelativeLayout item;

        ViewHolder(View itemView) {
            super(itemView);
            friend_name = itemView.findViewById(R.id.friend_name);
            friends_pictures = itemView.findViewById(R.id.friend_profile_image);
            sendRequestButton = itemView.findViewById(R.id.sendRequestButton);
            item = itemView.findViewById(R.id.item);
        }
    }
}
