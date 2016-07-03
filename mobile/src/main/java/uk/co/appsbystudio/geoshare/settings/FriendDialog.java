package uk.co.appsbystudio.geoshare.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.json.DownloadImageTask;

public class FriendDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.friend_dialog, null);

        builder.setView(view).setNeutralButton("Close", null);

        ImageView profile = (ImageView) view.findViewById(R.id.profile_picture);
        TextView name = (TextView) view.findViewById(R.id.user_name);
        Bundle args = getArguments();

        //new DownloadImageTask(profile, getActivity()).execute("https://geoshare.appsbystudio.co.uk/api/user/" + args.getString("name") + "/img/");
        new DownloadImageTask(null, profile, getActivity()).execute("https://geoshare.appsbystudio.co.uk/api/user/" + "J15t98J" + "/img/");
        name.setText(args.getString("name"));

        return builder.create();
    }
}
