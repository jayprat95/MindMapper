package edu.engagement.application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        nextButton = (Button) findViewById(R.id.nextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String result = String.valueOf(mText.getText());
                if(result.length() != 0){
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(RETURN_RESULT_KEY,result);
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please input something...", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}
