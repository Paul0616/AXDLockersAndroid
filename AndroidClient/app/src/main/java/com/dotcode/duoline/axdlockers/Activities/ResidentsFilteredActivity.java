package com.dotcode.duoline.axdlockers.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.dotcode.duoline.axdlockers.Network.SetRequests;
import com.dotcode.duoline.axdlockers.R;

import java.util.ArrayList;
import java.util.List;

public class ResidentsFilteredActivity extends AppCompatActivity implements SetRequests.GetDataResponse {

    private ArrayList<String> first4Strings = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_residents_filtered);

        ArrayList<String> lines = getIntent().getStringArrayListExtra("first4lines");
        if(lines != null){
            first4Strings = lines;
            Toast.makeText(this, ""+first4Strings.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResponse(int currentRequestId, Object result) {

    }

    @Override
    public void onFailed(int currentRequestId, boolean mustLogOut) {

    }
}
