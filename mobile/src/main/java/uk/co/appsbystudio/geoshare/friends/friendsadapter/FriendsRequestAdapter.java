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
import uk.co.appsbystudio.geoshare.database.ReturnData;
import uk.co.appsbystudio.geoshare.json.DownloadImageTask;
import uk.co.appsbystudio.geoshare.json.JSONObjectRequest;

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
        new DownloadImageTask(holder.friends_pictures, null, context).execute("https://geoshare.appsbystudio.co.uk/api/user/" + namesArray.get(position).toString() + "/img/");

        holder.accept_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JSONObjectRequest(context, "https://geoshare.appsbystudio.co.uk/api/user/" + new ReturnData().getUsername(context).replace(" ", "%20") + "/friends/request/" + namesArray.get(holder.getAdapterPosition()).toString().replace(" ", "%20"), "accept", new ReturnData().getpID(context)).execute();
            }
        });

        holder.decline_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JSONObjectRequest(context, "https://geoshare.appsbystudio.co.uk/api/user/" + new ReturnData().getUsername(context).replace(" ", "%20") + "/friends/request/" + namesArray.get(holder.getAdapterPosition()).toString().replace(" ", "%20"), "ignore", new ReturnData().getpID(context)).execute();
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

        public ViewHolder(View itemView) {
            super(itemView);
            friend_name = (TextView) itemView.findViewById(R.id.friend_name);
            friends_pictures = (CircleImageView) itemView.findViewById(R.id.friend_profile_image);
            accept_request = (ImageView) itemView.findViewById(R.id.friend_accept);
            decline_request = (ImageView) itemView.findViewById(R.id.friend_reject);
        }
    }
}
