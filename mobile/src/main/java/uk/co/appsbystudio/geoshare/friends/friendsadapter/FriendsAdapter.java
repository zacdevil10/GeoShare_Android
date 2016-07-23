package uk.co.appsbystudio.geoshare.friends.friendsadapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.appsbystudio.geoshare.MainActivity;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.database.ReturnData;
import uk.co.appsbystudio.geoshare.friends.FriendsManagerFragment;
import uk.co.appsbystudio.geoshare.friends.pages.FriendSearchActivity;
import uk.co.appsbystudio.geoshare.json.DeleteRequestTask;
import uk.co.appsbystudio.geoshare.json.DownloadImageTask;
import uk.co.appsbystudio.geoshare.json.RequestFriendTask;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder>{
    private final Context context;
    private final ArrayList namesArray;

    public FriendsAdapter(Context context, ArrayList namesArray) {
        this.context = context;
        this.namesArray = namesArray;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friends_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.friend_name.setText(namesArray.get(position).toString());

        File file = new File(String.valueOf(context.getCacheDir()), namesArray.get(position).toString() + ".png");
        try {
            Bitmap image_bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            holder.friends_pictures.setImageBitmap(image_bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        new DownloadImageTask(holder.friends_pictures, null, context, namesArray.get(position).toString(), false).execute("https://geoshare.appsbystudio.co.uk/api/user/" + namesArray.get(position).toString() + "/img/");

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.removeFriend:
                                new DeleteRequestTask().onDeleteRequest("https://geoshare.appsbystudio.co.uk/api/user/" + (new ReturnData().getUsername(context)).replace(" ", "%20") + "/friends/" + namesArray.get(holder.getAdapterPosition()).toString().replace(" ", "%20"), new ReturnData().getpID(context), context);
                                return true;
                            case R.id.showProfile:
                                ((MainActivity) context).friendsDialog((String) holder.friend_name.getText());
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
                ((MainActivity) context).friendsDialog((String) holder.friend_name.getText());
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
        final ImageView more;
        final RelativeLayout item;

        ViewHolder(View itemView) {
            super(itemView);
            friend_name = (TextView) itemView.findViewById(R.id.friend_name);
            friends_pictures = (CircleImageView) itemView.findViewById(R.id.friend_profile_image);
            more = (ImageView) itemView.findViewById(R.id.more);
            item = (RelativeLayout) itemView.findViewById(R.id.item);
        }
    }
}
