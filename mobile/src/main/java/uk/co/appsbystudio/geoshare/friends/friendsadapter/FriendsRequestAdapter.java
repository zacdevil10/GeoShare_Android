package uk.co.appsbystudio.geoshare.friends.friendsadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.appsbystudio.geoshare.R;

public class FriendsRequestAdapter extends RecyclerView.Adapter<FriendsRequestAdapter.ViewHolder>{
    private final Context context;
    private final ArrayList namesArray;

    public FriendsRequestAdapter(Context context, ArrayList namesArray) {
        this.context = context;
        this.namesArray = namesArray;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friends_request_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.friend_name.setText(namesArray.get(position).toString());


        //TODO: Friends picture

        holder.accept_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: accept friend request
            }
        });

        holder.decline_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: decline friend request
            }
        });
    }

    @Override
    public int getItemCount() {
        return namesArray.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        final TextView friend_name;
        final CircleImageView friends_pictures;
        final ImageView accept_request;
        final ImageView decline_request;

        ViewHolder(View itemView) {
            super(itemView);
            friend_name = itemView.findViewById(R.id.friend_name);
            friends_pictures = itemView.findViewById(R.id.friend_profile_image);
            accept_request = itemView.findViewById(R.id.friend_accept);
            decline_request = itemView.findViewById(R.id.friend_reject);
        }
    }
}
