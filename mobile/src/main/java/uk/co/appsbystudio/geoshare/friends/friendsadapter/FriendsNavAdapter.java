package uk.co.appsbystudio.geoshare.friends.friendsadapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.transition.TransitionManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.appsbystudio.geoshare.MainActivity;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.utils.UserInformation;

public class FriendsNavAdapter extends RecyclerView.Adapter<FriendsNavAdapter.ViewHolder>{
    private final Context context;
    private final RecyclerView recyclerView;
    private final ArrayList userId;
    private final DatabaseReference databaseReference;

    private int expandedPosition = -1;

    public FriendsNavAdapter(Context context, RecyclerView recyclerView, ArrayList userId, DatabaseReference databaseReference) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.userId = userId;
        this.databaseReference = databaseReference;
    }

    @Override
    public FriendsNavAdapter.ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friends_nav_item, viewGroup, false);

        return new FriendsNavAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FriendsNavAdapter.ViewHolder holder, final int position) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInformation userInformation = dataSnapshot.child("users").child(userId.get(holder.getAdapterPosition()).toString()).getValue(UserInformation.class);
                assert userInformation != null;
                holder.friend_name.setText(userInformation.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Set friends profile picture
        if (!userId.isEmpty()) {
            File fileCheck = new File(context.getCacheDir() + "/" + userId.get(position) + ".png");

            if (fileCheck.exists()) {
                Bitmap imageBitmap = BitmapFactory.decodeFile(context.getCacheDir() + "/" + userId.get(position) + ".png");
                holder.friends_pictures.setImageBitmap(imageBitmap);
            } else {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference profileRef = storageReference.child("profile_pictures/" + userId.get(position) + ".png");
                profileRef.getFile(Uri.fromFile(new File(context.getCacheDir() + "/" + userId.get(position) + ".png")))
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Bitmap imageBitmap = BitmapFactory.decodeFile(context.getCacheDir() + "/" + userId.get(holder.getAdapterPosition()) + ".png");
                                holder.friends_pictures.setImageBitmap(imageBitmap);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                holder.friends_pictures.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_profile_picture));
                            }
                        });
            }
        }

        final boolean isExpanded = position == expandedPosition;
        holder.expandedView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.nameItem.setActivated(isExpanded);

        if (isExpanded) {
            holder.friend_name.setTextColor(context.getResources().getColor(R.color.colorAccent));
        } else {
            holder.friend_name.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }

        holder.nameItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandedPosition = isExpanded ? -1:position;
                TransitionManager.beginDelayedTransition(recyclerView);
                notifyDataSetChanged();
            }
        });

        holder.sendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) context).sendLocationDialog((String) holder.friend_name.getText(), userId.get(holder.getAdapterPosition()).toString());
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
        final ImageView arrow;
        final RelativeLayout sendLocation;
        final RelativeLayout requestLocation;
        final ConstraintLayout nameItem;
        final RelativeLayout showOnMapLayout;
        final LinearLayout expandedView;

        ViewHolder(View itemView) {
            super(itemView);
            friend_name = (TextView) itemView.findViewById(R.id.friend_name);
            friends_pictures = (CircleImageView) itemView.findViewById(R.id.friend_profile_image);
            arrow = (ImageView) itemView.findViewById(R.id.more);
            sendLocation = (RelativeLayout) itemView.findViewById(R.id.sendLocation);
            requestLocation = (RelativeLayout) itemView.findViewById(R.id.requestLocation);
            nameItem = (ConstraintLayout) itemView.findViewById(R.id.name_item);
            showOnMapLayout = itemView.findViewById(R.id.showOnMapLayout);
            expandedView = itemView.findViewById(R.id.expandedView);
        }
    }
}
