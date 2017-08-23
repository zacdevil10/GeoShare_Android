package uk.co.appsbystudio.geoshare.friends.pages;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.utils.AddFriendsInfo;

public class FriendInfoActivity extends AppCompatActivity {

    String name;
    String userId;

    FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();

        Bundle bundle= getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString("name");
            userId = bundle.getString("uid");
        }

        ImageView backdropImage = (ImageView) findViewById(R.id.infoBackdropImage);
        CircleImageView profileImage = (CircleImageView) findViewById(R.id.avatar);

        Bitmap imageBitmap = BitmapFactory.decodeFile(getCacheDir() + "/" + userId + ".png");
        profileImage.setImageBitmap(imageBitmap);

        ((TextView) findViewById(R.id.name)).setText(name);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddFriendsInfo outgoing = new AddFriendsInfo(true);
                ref.child("pending").child(auth.getCurrentUser().getUid()).child(userId).setValue(outgoing);
                AddFriendsInfo incoming = new AddFriendsInfo(false);
                ref.child("pending").child(userId).child(auth.getCurrentUser().getUid()).setValue(incoming);
            }
        });
    }
}
