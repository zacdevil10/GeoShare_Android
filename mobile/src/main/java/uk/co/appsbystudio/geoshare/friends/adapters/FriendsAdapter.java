package uk.co.appsbystudio.geoshare.friends.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.friends.profile.Profile;
import uk.co.appsbystudio.geoshare.utils.ProfileUtils;
import uk.co.appsbystudio.geoshare.utils.firebase.listeners.GetUserFromDatabase;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder>{
    private final Context context;
    private final ArrayList userId;
    private final DatabaseReference databaseReference;

    public interface Callback {
        void onRemoveFriend(String friendId);
    }

    private final Callback callback;

    public FriendsAdapter(Context context, ArrayList userId, DatabaseReference databaseReference, Callback callback) {
        this.context = context;
        this.userId = userId;
        this.databaseReference = databaseReference;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friends_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        databaseReference.addListenerForSingleValueEvent(new GetUserFromDatabase(userId.get(position).toString(), holder.friend_name));

        if (!userId.isEmpty()) ProfileUtils.setProfilePicture(userId.get(position).toString(), holder.friends_pictures, context.getCacheDir().toString());

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.removeFriend:
                                callback.onRemoveFriend(userId.get(holder.getAdapterPosition()).toString());
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                MenuInflater menuInflater = popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.friend_menu, popupMenu.getMenu());
                popupMenu.show();
            }
        });

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Profile.class);
                intent.putExtra("uid", userId.get(holder.getAdapterPosition()).toString());
                context.startActivity(intent);
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
        final ImageView more;
        final RelativeLayout item;

        ViewHolder(View itemView) {
            super(itemView);
            friend_name = itemView.findViewById(R.id.friend_name);
            friends_pictures = itemView.findViewById(R.id.friend_profile_image);
            more = itemView.findViewById(R.id.more);
            item = itemView.findViewById(R.id.item);
        }
    }
}
