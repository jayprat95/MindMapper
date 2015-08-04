package edu.engagement.application.Fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import edu.engagement.application.R;

import static com.google.android.gms.internal.zzhl.runOnUiThread;


/**
 * Created by IvenRee on 7/30/15.
 */
public class StatusDialogFragment extends DialogFragment{

    private TextView titleView;
    private TextView tipView1;
    private TextView counterView;
    private TextView tipView2;
    private TextView tipView3;
    private Button reconnectButton;
    private Button endButton;
    private Button resumeButton;
    private ProgressBar mBar;

    private static final int counter = 5;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.eeg_status_dialog, container, false);

        titleView = (TextView) v.findViewById(R.id.titleView);
        tipView1 = (TextView) v.findViewById(R.id.tipView_1);
        tipView2 = (TextView) v.findViewById(R.id.tipView_2);
        tipView3 = (TextView) v.findViewById(R.id.tipView_3);
        counterView = (TextView) v. findViewById(R.id.counterView);
        reconnectButton = (Button) v.findViewById(R.id.reconnectButton);
        endButton = (Button) v.findViewById(R.id.endButton);
        resumeButton = (Button) v.findViewById(R.id.resumeButton);
        mBar = (ProgressBar) v.findViewById(R.id.progressBar);
        counterView.setText("    [ " + counter + " ]  minutes");
        //counter down
        final Timer T = new Timer();

        T.scheduleAtFixedRate(new TimerTask() {
            int temp = counter - 1;
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(temp > 0){
                            counterView.setText("    [ " + temp + " ]  minutes");
                            temp--;
                        }
                    }
                });
            }
        }, 60000, 60000);


        reconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                T.cancel();
                titleView.setText("Reconnecting ... ");
                tipView1.setVisibility(View.INVISIBLE);
                tipView2.setVisibility(View.INVISIBLE);
                counterView.setVisibility(View.INVISIBLE);
                mBar.setVisibility(View.VISIBLE);

                //Boolean isConnect = false;
                //Call eeg reconnect TODO
//
//                //resume or end
//                titleView.setText("Successful !");
//                reconnectButton.setVisibility(View.GONE);
//                resumeButton.setVisibility(View.VISIBLE);
//
//                //retry or end
//                titleView.setText("Failed !");
//                tipView3.setVisibility(View.VISIBLE);
//                resumeButton.setVisibility(View.GONE);
//                reconnectButton.setVisibility(View.VISIBLE);

            }
        });

        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO tell Recording Scrren to resume
                //
                dismiss();
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO tell Recording Scrren to stop
                //
                dismiss();
            }
        });


        return v;
    }
}
