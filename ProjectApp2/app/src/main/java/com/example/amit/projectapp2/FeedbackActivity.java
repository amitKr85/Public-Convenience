package com.example.amit.projectapp2;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amit.projectapp2.data.ServiceMgmtContract.*;

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener {

    int mUserid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Intent intent = getIntent();
        if (intent != null) {
            mUserid = (int) ContentUris.parseId(intent.getData());
        }

        Button sendButton = findViewById(R.id.feedback_send_button);
        sendButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.feedback_send_button) {
            /**
             * checking if any of the radio button has been checked or not
             */
            RadioGroup radioGroup = findViewById(R.id.feedback_radio_group);
            int suggType = -1;
            // if none of them has checked
            if (radioGroup.getCheckedRadioButtonId() == -1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Please choose category");
                builder.setPositiveButton("ok", null);
                builder.create().show();
                return;
            } else {
                int radioId = radioGroup.getCheckedRadioButtonId();
                if (radioId == R.id.feedback_like_radio_button)
                    suggType = SuggestionEntry.SUGGESTION_TYPE_LIKE;
                else if (radioId == R.id.feedback_dislike_radio_button)
                    suggType = SuggestionEntry.SUGGESTION_TYPE_DISLIKE;
                else
                    suggType = SuggestionEntry.SUGGESTION_TYPE_IDEA;
            }
            /**
             * checking for empty description
             */
            String description = ((EditText) findViewById(R.id.feedback_description)).getText().toString();
            // if empty
            if (EmptyString.isEmptyString(description)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Empty description !!");
                builder.setPositiveButton("ok", null);
                builder.create().show();
                return;
            }

            ContentValues values = new ContentValues();
            values.put(SuggestionEntry.COLUMN_USER_ID, mUserid);
            values.put(SuggestionEntry.COLUMN_SUGGESTION_TYPE,suggType);
            values.put(SuggestionEntry.COLUMN_DESCRIPTION,description);
            Uri rowUri=getContentResolver().insert(SuggestionEntry.SUGGESTION_CONTENT_URI,values);

            long id=ContentUris.parseId(rowUri);
           // Log.i("Feedback","Id="+id);

            if(id>0){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Suggestion sent. Thank you for feedback.");
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.create().show();
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Unable to send feedback try again later.");
                builder.setPositiveButton("ok", null);
                builder.create().show();
                return;
            }

        }
    }
}
