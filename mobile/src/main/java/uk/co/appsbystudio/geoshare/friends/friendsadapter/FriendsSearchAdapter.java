package uk.co.appsbystudio.geoshare.friends.friendsadapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.friends.pages.FriendInfoActivity;
import uk.co.appsbystudio.geoshare.utils.RecentSearches;

public class FriendsSearchAdapter extends RecyclerView.Adapter<FriendsSearchAdapter.ViewHolder>{

    private final Context context;
    private final ArrayList namesArray;
    private final ArrayList<Boolean> isSearch;
    private final ArrayList userId;
    private final ArrayList<Boolean> isRecent;

    private final DatabaseReference databaseReference;
    private final FirebaseAuth firebaseAuth;

    public interface Callback {
        void onSearchItemClick(String searchEntry);
    }

    private Callback callback;

    public FriendsSearchAdapter(Context context, DatabaseReference databaseReference, FirebaseAuth firebaseAuth, ArrayList namesArray, ArrayList<Boolean> isSearch, ArrayList<Boolean> isRecent, ArrayList userId, Callback callback) {
        this.context = context;
        this.namesArray = namesArray;
        this.isSearch = isSearch;
        this.databaseReference = databaseReference;
        this.firebaseAuth = firebaseAuth;
        this.callback = callback;
        this.userId = userId;
        this.isRecent = isRecent;
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

        if (isSearch.get(position)) {
            holder.friends_pictures.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_search_black_24dp));
        } else {
            if (!userId.isEmpty()) {
                System.out.println(userId);
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
        }

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSearch.get(holder.getAdapterPosition())) {
                    if (callback != null) {
                        callback.onSearchItemClick(namesArray.get(holder.getAdapterPosition()).toString());
                    }
                } else {
                    if (!isRecent.get(holder.getAdapterPosition())) {
                        RecentSearches recentSearches = new RecentSearches(namesArray.get(holder.getAdapterPosition()).toString(), userId.get(holder.getAdapterPosition()).toString(), String.valueOf(-1 * System.currentTimeMillis()), false);
                        databaseReference.child("recent_friends_search").child(firebaseAuth.getCurrentUser().getUid()).push().setValue(recentSearches);
                    }
                    Intent intent = new Intent(context, FriendInfoActivity.class);
                    intent.putExtra("name", namesArray.get(holder.getAdapterPosition()).toString());
                    intent.putExtra("uid", userId.get(holder.getAdapterPosition()).toString());
                    context.startActivity(intent);
                }
            }
        });

        if (isSearch.get(position)) {
            holder.menu.setVisibility(View.GONE);
        }

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.addFriend:

                                return true;
                            case R.id.showProfile:
                                if (!isRecent.get(holder.getAdapterPosition())) {
                                    RecentSearches recentSearches = new RecentSearches(namesArray.get(holder.getAdapterPosition()).toString(), userId.get(holder.getAdapterPosition()).toString(), String.valueOf(-1 * System.currentTimeMillis()), false);
                                    databaseReference.child("recent_friends_search").child(firebaseAuth.getCurrentUser().getUid()).push().setValue(recentSearches);
                                }
                                Intent intent = new Intent(context, FriendInfoActivity.class);
                                intent.putExtra("name", namesArray.get(holder.getAdapterPosition()).toString());
                                intent.putExtra("uid", userId.get(holder.getAdapterPosition()).toString());
                                context.startActivity(intent);
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
