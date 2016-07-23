package uk.co.appsbystudio.geoshare.friends.friendsadapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.database.ReturnData;
import uk.co.appsbystudio.geoshare.friends.pages.FriendSearchActivity;
import uk.co.appsbystudio.geoshare.json.DownloadImageTask;
import uk.co.appsbystudio.geoshare.json.RequestFriendTask;

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

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Click");
                //new DeleteRequestTask().onPostRequest("https://geoshare.appsbystudio.co.uk/api/user/" + holder.friend_name.getText() + "/friends/request/", new ReturnData().getpID(context), context);
                ((FriendSearchActivity) context).friendsDialog((String) holder.friend_name.getText());
            }
        });

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.addFriend:
                                new RequestFriendTask().onPostRequest("https://geoshare.appsbystudio.co.uk/api/user/" + holder.friend_name.getText().toString().replace(" ", "%20") + "/friends/request/", new ReturnData().getpID(context), context);
                                return true;
                            case R.id.showProfile:
                                ((FriendSearchActivity) context).friendsDialog((String) holder.friend_name.getText());
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                MenuInflater menuInflater = popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.search_menu, popupMenu.getMenu());
                popupMenu.show();
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
        final ImageButton menu;
        final RelativeLayout item;

        ViewHolder(View itemView) {
            super(itemView);
            friend_name = (TextView) itemView.findViewById(R.id.friend_name);
            friends_pictures = (CircleImageView) itemView.findViewById(R.id.friend_profile_image);
            menu = (ImageButton) itemView.findViewById(R.id.menu);
            item = (RelativeLayout) itemView.findViewById(R.id.item);
        }
    }
}
