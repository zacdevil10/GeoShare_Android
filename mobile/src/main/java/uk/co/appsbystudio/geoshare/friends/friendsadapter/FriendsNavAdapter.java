package uk.co.appsbystudio.geoshare.friends.friendsadapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import uk.co.appsbystudio.geoshare.json.DownloadImageTask;

public class FriendsNavAdapter extends RecyclerView.Adapter<FriendsNavAdapter.ViewHolder>{
    private final Context context;
    private final ArrayList namesArray;

    public FriendsNavAdapter(Context context, ArrayList namesArray) {
        this.context = context;
        this.namesArray = namesArray;
    }

    @Override
    public FriendsNavAdapter.ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friends_nav_item, viewGroup, false);

        return new FriendsNavAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FriendsNavAdapter.ViewHolder holder, int position) {
        holder.friend_name.setText(namesArray.get(position).toString());

        File file = new File(String.valueOf(context.getCacheDir()), namesArray.get(position).toString() + ".png");
        try {
            Bitmap image_bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            holder.friends_pictures.setImageBitmap(image_bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        new DownloadImageTask(holder.friends_pictures, null, context, namesArray.get(position).toString(), false).execute("https://geoshare.appsbystudio.co.uk/api/user/" + namesArray.get(position).toString() + "/img/");

        final Animation scaleOpen = AnimationUtils.loadAnimation(context, R.anim.scale_list_open);
        final Animation scaleClose = AnimationUtils.loadAnimation(context, R.anim.scale_list_close);
        final Animation rotateUp = AnimationUtils.loadAnimation(context, R.anim.rotate_up);
        final Animation rotateDown = AnimationUtils.loadAnimation(context, R.anim.rotate_down);

        holder.nameItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.sendLocation.getVisibility() == View.GONE) {
                    holder.sendLocation.setVisibility(View.VISIBLE);
                    holder.requestLocation.setVisibility(View.VISIBLE);
                    holder.showOnMapLayout.setVisibility(View.VISIBLE);
                    //holder.test.startAnimation(scaleOpen);
                    //holder.more.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_48px));
                    holder.arrow.startAnimation(rotateUp);
                } else {
                    //holder.test.startAnimation(scaleClose);
                    holder.sendLocation.setVisibility(View.GONE);
                    holder.requestLocation.setVisibility(View.GONE);
                    holder.showOnMapLayout.setVisibility(View.GONE);
                    //holder.more.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_48px));
                    holder.arrow.startAnimation(rotateDown);
                }

            }
        });

        holder.sendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) context).sendLocationDialog((String) holder.friend_name.getText());
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
        final ImageView arrow;
        final RelativeLayout sendLocation;
        final RelativeLayout requestLocation;
        final RelativeLayout nameItem;
        final RelativeLayout showOnMapLayout;

        ViewHolder(View itemView) {
            super(itemView);
            friend_name = (TextView) itemView.findViewById(R.id.friend_name);
            friends_pictures = (CircleImageView) itemView.findViewById(R.id.friend_profile_image);
            arrow = (ImageView) itemView.findViewById(R.id.more);
            sendLocation = (RelativeLayout) itemView.findViewById(R.id.sendLocation);
            requestLocation = (RelativeLayout) itemView.findViewById(R.id.requestLocation);
            nameItem = (RelativeLayout) itemView.findViewById(R.id.name_item);
            showOnMapLayout = (RelativeLayout) itemView.findViewById(R.id.showOnMapLayout);
        }
    }
}
