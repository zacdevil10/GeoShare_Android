package uk.co.appsbystudio.geoshare.utils.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import uk.co.appsbystudio.geoshare.R;

public class ShareALocationDialog extends DialogFragment {

    ArrayList arrayList;
    CharSequence[] items;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        View view = View.inflate(getActivity(), R.layout.friend_selection_dialog, null);

        builder.setView(view);

        RecyclerView friendList = view.findViewById(R.id.friend_selection);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        friendList.setLayoutManager(layoutManager);

        builder.setTitle("Upload profile picture").setPositiveButton("Share", null).setNegativeButton("Cancel", null);

        return builder.create();
    }
}
