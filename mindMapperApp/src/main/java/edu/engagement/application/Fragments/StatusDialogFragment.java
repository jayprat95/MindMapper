package edu.engagement.application.Fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import edu.engagement.application.App;
import edu.engagement.application.R;


/**
 * Created by IvenRee on 7/30/15.
 */
public class StatusDialogFragment extends DialogFragment {

    private TextView titleView;
    private TextView tipView1;
    private TextView counterView;
    private TextView tipView2;
    private TextView tipView3;
    private Button reconnectButton;
    private Button endButton;
    private Button resumeButton;
    private ProgressBar mBar;

    private Timer t;

    private ConnectionLostDialogListener connectionListener;

    private static final int counter = 5;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.connectionListener = (ConnectionLostDialogListener)activity;
    }

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

        // Don't allow the dialog to be cancelled
        setCancelable(false);
        setStyle(STYLE_NO_TITLE, 0);

        startTimer();

        reconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t.cancel();
                titleView.setText("Reconnecting ... ");
                tipView1.setVisibility(View.INVISIBLE);
                tipView2.setVisibility(View.INVISIBLE);
                counterView.setVisibility(View.INVISIBLE);
                mBar.setVisibility(View.VISIBLE);

                reconnectButton.setVisibility(View.GONE);

                connectionListener.onClickReconnect();

            }
        });

        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO tell Recording Scrren to resume
                connectionListener.onClickResume();
                dismiss();
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO tell Recording Scrren to stop
                //
                t.cancel();

                connectionListener.onClickEndSession();
                dismiss();
            }
        });


        return v;
    }

    public void onReconnectSuccess() {
        titleView.setText("Reconnection Successful!");
        mBar.setVisibility(View.GONE);
        resumeButton.setVisibility(View.VISIBLE);
    }

    public void onReconnectFail() {
        titleView.setText("Reconnection Failed!");
        tipView1.setVisibility(View.VISIBLE);
        tipView2.setVisibility(View.VISIBLE);
        counterView.setVisibility(View.VISIBLE);
        mBar.setVisibility(View.GONE);

        counterView.setText("    [ " + counter + " ]  minutes");

        startTimer();

        reconnectButton.setVisibility(View.VISIBLE);
    }

    private void startTimer() {
        //counter down
        t = new Timer();
        //
        t.scheduleAtFixedRate(new TimerTask() {
            int temp = counter - 1;
            @Override
            public void run() {
                Log.d(App.NAME, "TIMER RUNNING " + temp);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(temp > 0){
                            counterView.setText("    [ " + temp + " ]  minutes");
                            temp--;
                        } else {
                            t.cancel();
                            connectionListener.onTimeout();
                        }
                    }
                });
            }
        }, 60000, 60000);
    }

    public interface ConnectionLostDialogListener {
        void onClickReconnect();
        void onClickResume();
        void onClickEndSession();
        void onTimeout();
    }
}
