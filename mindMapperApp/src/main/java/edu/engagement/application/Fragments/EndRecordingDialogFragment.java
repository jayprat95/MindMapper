package edu.engagement.application.Fragments;

import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import edu.engagement.application.AttentionLevel;
import edu.engagement.application.Database.DataPointSource;
import edu.engagement.application.R;

/**
 * Created by IvenRee on 11/18/15.
 */
public class EndRecordingDialogFragment extends DialogFragment {

    private Button mSave;
    private EditText mAnnotation;
    private SeekBar mSeekBar;
    private TextView mQuestion;
    private String activityName;

    private AttentionLevel attentionLevel;
    DataPointSource mDataPointSource = null;

    //static RecordingDialogFragment newInstance(){
    //    return new RecordingDialogFragment();
    //}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.end_recording_dialog, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        activityName = getArguments().getString("activity");

        mDataPointSource = new DataPointSource(this.getActivity().getApplicationContext());
        mDataPointSource.open();

        mSave = (Button) dialogView.findViewById(R.id.saveNotes);
        mAnnotation = (EditText) dialogView.findViewById(R.id.annotation);
        mSeekBar = (SeekBar) dialogView.findViewById(R.id.annotation_bar);
        mQuestion = (TextView) dialogView.findViewById(R.id.questionView);

        String question = "<b>" + "Overall, " + "</b> " + "how focused do you feel while " + activityName;

        mQuestion.setText(Html.fromHtml(question));

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAnnotation.getText().length() == 0){
                    Toast.makeText(getActivity(), "Please describe your experience!", Toast.LENGTH_SHORT).show();
                }else
                {
                    mDataPointSource.createDataPointAnnotation(RecordingFragment.sessionId, System.currentTimeMillis(), mAnnotation.getText().toString(), mSeekBar.getProgress());
                    Toast.makeText(getActivity(), "Your experience saved!", Toast.LENGTH_SHORT).show();
                    mAnnotation.setText("");
                    mSeekBar.setProgress(0);
                    getTargetFragment().onActivityResult(1, 3, null);
                    dismiss();

                }
            }
        });

        //
        attentionLevel = AttentionLevel.LOW1;
        mSeekBar = (SeekBar) dialogView.findViewById(R.id.annotation_bar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                attentionLevel = AttentionLevel.fromInt((int) ((progress-0.0001)/4));
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