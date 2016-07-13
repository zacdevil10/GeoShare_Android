package uk.co.appsbystudio.geoshare.friends.friendsadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.friends.pages.FriendSearchActivity;
import uk.co.appsbystudio.geoshare.json.DownloadImageTask;

public class FriendsSearchAdapter extends RecyclerView.Adapter<FriendsSearchAdapter.ViewHolder>{
    private final Context context;
    private final ArrayList namesArray;

    public FriendsSearchAdapter(Context context, ArrayList namesArray) {
        this.context = context;
        this.namesArray = namesArray;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int viewType) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friends_search_item, viewGroup, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.friend_name.setText(namesArray.get(position).toString());
        new DownloadImageTask(holder.friends_pictures, null, context, namesArray.get(position).toString(), false).execute("https://geoshare.appsbystudio.co.uk/api/user/" + namesArray.get(position).toString() + "/img/");

        holder.addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Click");
                //new JSONRequests().onPostRequest("https://geoshare.appsbystudio.co.uk/api/user/" + holder.friend_name.getText() + "/friends/request/", new ReturnData().getpID(context), context);
                ((FriendSearchActivity) context).friendsDialog((String) holder.friend_name.getText());
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
        final ImageButton addFriend;

        public ViewHolder(View itemView) {
            super(itemView);
            friend_name = (TextView) itemView.findViewById(R.id.friend_name);
            friends_pictures = (CircleImageView) itemView.findViewById(R.id.friend_profile_image);
            addFriend = (ImageButton) itemView.findViewById(R.id.addFriend);
        }
    }
}
