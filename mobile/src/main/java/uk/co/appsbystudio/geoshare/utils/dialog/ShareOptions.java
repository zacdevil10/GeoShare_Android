package uk.co.appsbystudio.geoshare.utils.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import uk.co.appsbystudio.geoshare.Application;
import uk.co.appsbystudio.geoshare.GPSTracking;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations;
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper;
import uk.co.appsbystudio.geoshare.utils.firebase.TrackingInfo;
import uk.co.appsbystudio.geoshare.utils.services.TrackingService;

public class ShareOptions extends DialogFragment {

    private SharedPreferences sharedPreferences;

    private String friendId;
    private String uid;

    private GPSTracking gpsTracking;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        String name = args.getString("name");
        friendId = args.getString("friendId");
        uid = args.getString("uid");

        gpsTracking = new GPSTracking(getActivity());

        sharedPreferences = getActivity().getSharedPreferences("tracking", Context.MODE_PRIVATE);

        AlertDialog.Builder optionsMenu = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);

        optionsMenu.setTitle("Share your location with " + name + "?").setMultiChoiceItems(R.array.shareLocationOptions, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                switch (which) {
                    case 0:
                        if (!isChecked) ((AlertDialog) dialog).getListView().setItemChecked(1, false);
                        break;
                    case 1:
                        if (isChecked) ((AlertDialog) dialog).getListView().setItemChecked(0, true);
                        break;
                }
            }
        }).setPositiveButton("Share", new OnPositiveButtonClick()).setNegativeButton("Cancel", null);

        return optionsMenu.create();
    }

    private class OnPositiveButtonClick implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int i) {
            if (((AlertDialog) dialog).getListView().getCheckedItemPositions().get(0) && !((AlertDialog) dialog).getListView().getCheckedItemPositions().get(1)) {
                DatabaseLocations databaseLocations = new DatabaseLocations(gpsTracking.getLongitude(), gpsTracking.getLatitude(), System.currentTimeMillis());

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                if (uid != null && friendId != null) {
                    databaseReference.child("current_location").child(friendId).child(uid).setValue(databaseLocations)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Application.getContext(), "Success", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Application.getContext(), "Failed", Toast.LENGTH_LONG).show();
                                }
                            });
                    sharedPreferences.edit().putBoolean(friendId, false).apply();
                }
            } else if (((AlertDialog) dialog).getListView().getCheckedItemPositions().get(0) && ((AlertDialog) dialog).getListView().getCheckedItemPositions().get(1)) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                if (uid != null && friendId != null) {
                    TrackingInfo trackingInfo = new TrackingInfo(true, System.currentTimeMillis());
                    databaseReference.child(FirebaseHelper.TRACKING).child(friendId).child("tracking").child(uid).setValue(trackingInfo)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    sharedPreferences.edit().putBoolean(friendId, true).apply();
                                    if (!TrackingService.isRunning) {
                                        Intent trackingService = new Intent(Application.getContext(), TrackingService.class);
                                        Application.getContext().startService(trackingService);
                                    }
                                    Toast.makeText(Application.getContext(), "Success", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Application.getContext(), "Failed", Toast.LENGTH_LONG).show();
                                }
                            });

                    DatabaseLocations databaseLocations = new DatabaseLocations(gpsTracking.getLongitude(), gpsTracking.getLatitude(), System.currentTimeMillis());

                    databaseReference.child(FirebaseHelper.TRACKING).child(uid).child("location").setValue(databaseLocations);

                    databaseReference.child("current_location").child(friendId).child(uid).removeValue();
                }
            }
        }
    }
}
