package uk.co.appsbystudio.geoshare.friends.pages;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.database.DatabaseHelper;
import uk.co.appsbystudio.geoshare.database.databaseModel.UserModel;
import uk.co.appsbystudio.geoshare.json.JSONStringRequests;

public class FriendsFragment extends Fragment {

    private ListView friendsList;

    private SwipeRefreshLayout swipeRefresh;

    String pID;
    String mUsername;

    DatabaseHelper db;

    public FriendsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        db = new DatabaseHelper(getContext());

        friendsList = (ListView) view.findViewById(R.id.friend_list);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        List<UserModel> userModelList = db.getAllUsers();
        for (UserModel id: userModelList) {
            pID = id.getpID();
            mUsername = id.getUsername();
        }

        new JSONStringRequests(getActivity(), friendsList, swipeRefresh, "http://geoshare.appsbystudio.co.uk/api/user/" + mUsername + "/friends/", pID).execute();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new JSONStringRequests(getActivity(), friendsList, swipeRefresh, "http://geoshare.appsbystudio.co.uk/api/user/" + mUsername + "/friends/", pID).execute();
            }
        });

        return view;
    }
}