package uk.co.appsbystudio.geoshare.friends.pages;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.utils.firebase.AddFriendsInfo;

public class FriendInfoActivity extends AppCompatActivity {

    private String name;
    private String userId;

    private FirebaseAuth auth;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);

        auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference();

        Bundle bundle= getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString("name");
            userId = bundle.getString("uid");
        }

        ImageView backdropImage = findViewById(R.id.infoBackdropImage);
        CircleImageView profileImage = findViewById(R.id.avatar);

        Bitmap imageBitmap = BitmapFactory.decodeFile(getCacheDir() + "/" + userId + ".png");
        profileImage.setImageBitmap(imageBitmap);

        ((TextView) findViewById(R.id.name)).setText(name);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (auth.getCurrentUser() != null){
                    AddFriendsInfo outgoing = new AddFriendsInfo(true);
                    ref.child("pending").child(auth.getCurrentUser().getUid()).child(userId).setValue(outgoing);
                    AddFriendsInfo incoming = new AddFriendsInfo(false);
                    ref.child("pending").child(userId).child(auth.getCurrentUser().getUid()).setValue(incoming);
                }
            }
        });
    }
}
