package com.dotcode.duoline.axdlockers.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dotcode.duoline.axdlockers.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SummaryOrphanActivity extends AppCompatActivity {

    private TextView summaryTextView, parcelDescriptionsTextView, commentsTextView;
    private Button addbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_orphan);

        summaryTextView = (TextView) findViewById(R.id.createdAtTextView);
        parcelDescriptionsTextView = (TextView) findViewById(R.id.parcelDescriptionsTextView);
        commentsTextView = (TextView) findViewById(R.id.comments);
        addbutton = (Button) findViewById(R.id.buttonOK);

        long createdAt = getIntent().getLongExtra("createdAt", 0);
        String parcelD = getIntent().getStringExtra("parceldescriptions");
        String comments = getIntent().getStringExtra("comments");
        if (createdAt != 0) {
            String dateString = new SimpleDateFormat("MMMM d, YYYY HH:mm", new Locale("en", "en_US")).format(new Date(createdAt*1000L));
            summaryTextView.setText("You just added at\n"+dateString+"\nan unknown parcel with specifications:");
        } else {
            summaryTextView.setText("");
        }

        if(parcelD != null){
            parcelDescriptionsTextView.setText(parcelD);
        }

        if(comments != null){
            commentsTextView.setText(comments);
        }

        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SummaryOrphanActivity.this, OCRResultActivity.class));
            }
        });


    }
}
