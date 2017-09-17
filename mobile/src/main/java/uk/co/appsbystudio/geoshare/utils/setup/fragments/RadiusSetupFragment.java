package uk.co.appsbystudio.geoshare.utils.setup.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.utils.ui.SeekBarTextIndicator;

public class RadiusSetupFragment extends Fragment {

    private SeekBarTextIndicator seekBarTextIndicator;
    private TextView progressText;

    public RadiusSetupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_radius_setup, container, false);

        seekBarTextIndicator = view.findViewById(R.id.setRadiusSeekBar);
        progressText = view.findViewById(R.id.progressText);

        seekBarTextIndicator.setMax(200);
        seekBarTextIndicator.setProgress(100);

        seekBarTextIndicator.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressText.setText(String.valueOf(i));
                progressText.measure(0, 0);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ABOVE, seekBar.getId());

                Rect thumb = seekBarTextIndicator.getSeekBarThumb().getBounds();

                params.setMarginStart(thumb.centerX() - progressText.getMeasuredWidth());
                params.addRule(RelativeLayout.ALIGN_START, seekBar.getId());

                progressText.setLayoutParams(params);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return view;
    }

}
