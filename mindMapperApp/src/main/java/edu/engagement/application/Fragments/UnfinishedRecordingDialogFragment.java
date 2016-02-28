package edu.engagement.application.Fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import edu.engagement.application.Database.DataPointSource;
import edu.engagement.application.R;

public class UnfinishedRecordingDialogFragment extends DialogFragment {

    private Button deleteButton;
    private Button saveButton;
    private OnFragmentInterfaceListener mListener;
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
         mListener = (OnFragmentInterfaceListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.fragment_unfinished_recording, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        setCancelable(false);

        deleteButton = (Button) dialogView.findViewById(R.id.unfinished_delete_button);
        saveButton = (Button) dialogView.findViewById(R.id.unfinished_resume_button);

        /*
         * The delete button is pressed
         */
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                int sessionId = prefs.getInt("sessionId", 1);

                DataPointSource dps = new DataPointSource(getContext());
                dps.open();
                dps.deleteSession(sessionId);
                dps.close();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("finishedRecording", true);
                mListener.onEndCall();
                dismiss();
            }
        });

        /*
         *  The resume button is pressed
         */
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onButtonClicked();
                dismiss();
            }
        });

        return dialogView;
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        dismiss();
    }


    public interface OnFragmentInterfaceListener{
        void onEndCall();
        void onButtonClicked();
    }
}
