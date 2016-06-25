package uk.co.appsbystudio.geoshare.friends.pages;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.json.SearchFriends;

public class FriendSearchActivity extends AppCompatActivity {

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_search);

        final TextView searchBox = (TextView) findViewById(R.id.searchEditText_input);
        ImageView cancel = (ImageView) findViewById(R.id.search_cancel);
        final RecyclerView searchResults = (RecyclerView) findViewById(R.id.searchResults);
        searchResults.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        searchResults.setLayoutManager(layoutManager);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBox.setText("");
            }
        });

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                System.out.println(charSequence);
                if (charSequence.length() > 0) {
                    refresh(searchResults, charSequence);
                } else {
                    refresh(searchResults, "");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }

    public void refresh(RecyclerView searchResults, CharSequence name) {
        new SearchFriends(context, searchResults, "https://geoshare.appsbystudio.co.uk/api/search/" + name).execute();
    }
}
