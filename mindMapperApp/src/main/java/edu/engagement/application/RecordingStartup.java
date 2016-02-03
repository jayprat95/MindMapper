package edu.engagement.application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RecordingStartup extends Activity {

    EditText mText;
    Button nextButton;
    public static final String RETURN_RESULT_KEY = "result";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_startup);

        mText = (EditText) findViewById(R.id.editText);

        mText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d("Hahaha", "hohohohohoho");
                return actionId == EditorInfo.IME_ACTION_GO && processInput(String.valueOf(v.getText()));
            }
        });

        nextButton = (Button) findViewById(R.id.nextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processInput(String.valueOf(mText.getText()));
            }
        });
    }

    private boolean processInput(String input) {
        if (input.length() <= 0) {
            Toast.makeText(getApplicationContext(), "Please input something...", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(RETURN_RESULT_KEY, input);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
            return true;
        }
    }
}
