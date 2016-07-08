package uk.co.appsbystudio.geoshare.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.json.DownloadImageTask;

public class FriendDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.friend_dialog, null);

        builder.setView(view);

        ImageView profile = (ImageView) view.findViewById(R.id.profile_picture);
        TextView name = (TextView) view.findViewById(R.id.user_name);
        Bundle args = getArguments();

        new DownloadImageTask(null, profile, getActivity(), args.getString("name")).execute("https://geoshare.appsbystudio.co.uk/api/user/" + args.getString("name") + "/img/");
        //new DownloadImageTask(null, profile, getActivity()).execute("https://geoshare.appsbystudio.co.uk/api/user/" + "zacdevil10" + "/img/");
        name.setText(args.getString("name"));

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.addFriend);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        return builder.create();
    }
}
