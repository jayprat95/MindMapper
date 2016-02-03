package edu.engagement.application.Fragments;

import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import edu.engagement.application.AttentionLevel;
import edu.engagement.application.Database.DataPointSource;
import edu.engagement.application.R;

/**
 * Created by IvenRee on 12/14/15.
 */
public class PauseDialogFragment extends DialogFragment {

    public static final int PAUSE_RESULT_RESUME = 4;
    public static final int PAUSE_RESULT_END = 5;

    private Button mEnd;
    private Button mResume;

    private AttentionLevel attentionLevel;
    DataPointSource mDataPointSource = null;

    //static RecordingDialogFragment newInstance(){
    //    return new RecordingDialogFragment();
    //}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.pause_alert_dialog, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        mEnd = (Button) dialogView.findViewById(R.id.endButton);
        mResume = (Button) dialogView.findViewById(R.id.resumeButton);

        mEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTargetFragment().onActivityResult(RecordingFragment.PAUSE_REQUEST, PAUSE_RESULT_END, null);
                dismiss();
            }
        });
        mResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTargetFragment().onActivityResult(RecordingFragment.PAUSE_REQUEST, PAUSE_RESULT_RESUME, null);
                dismiss();

            }
        });

        return dialogView;
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        getTargetFragment().onActivityResult(RecordingFragment.PAUSE_REQUEST, PAUSE_RESULT_RESUME, null);
        dismiss();
    }
}
