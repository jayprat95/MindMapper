package edu.engagement.application.Fragments;

import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import edu.engagement.application.AttentionLevel;
import edu.engagement.application.Database.DataPointSource;
import edu.engagement.application.R;

/**
 * Created by IvenRee on 11/18/15.
 */
public class EndRecordingDialogFragment extends DialogFragment {

    private Button mSave;
    private EditText mAnnotation;
    private Button mSkip;
    private SeekBar mSeekBar;

    private AttentionLevel attentionLevel;
    DataPointSource mDataPointSource = null;

    //static RecordingDialogFragment newInstance(){
    //    return new RecordingDialogFragment();
    //}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.end_recording_dialog, container, false);

        mDataPointSource = new DataPointSource(this.getActivity().getApplicationContext());
        mDataPointSource.open();

        mSave = (Button) dialogView.findViewById(R.id.saveNotes);
        mSkip = (Button) dialogView.findViewById(R.id.skipButton);
        mAnnotation = (EditText) dialogView.findViewById(R.id.annotation);
        mSeekBar = (SeekBar) dialogView.findViewById(R.id.annotation_bar);

        mSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataPointSource.close();
                dismiss();
                getTargetFragment().onActivityResult(1, 2, null);
            }
        });
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();

            }
        });

        //
        attentionLevel = AttentionLevel.LOW1;
        mSeekBar = (SeekBar) dialogView.findViewById(R.id.annotation_bar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                attentionLevel = AttentionLevel.fromInt(progress);
                setProgressBarColor(seekBar, attentionLevel.getColor());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return dialogView;
    }

    private void setProgressBarColor(SeekBar seekBar, int newColor) {
        LayerDrawable ld = (LayerDrawable) seekBar.getProgressDrawable();
        ClipDrawable d1 = (ClipDrawable) ld.findDrawableByLayerId(R.id.progressShape);
        d1.setColorFilter(newColor, PorterDuff.Mode.SRC_IN);
    }

}