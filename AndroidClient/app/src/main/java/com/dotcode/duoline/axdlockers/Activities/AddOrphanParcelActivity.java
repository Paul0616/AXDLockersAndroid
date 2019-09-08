package com.dotcode.duoline.axdlockers.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dotcode.duoline.axdlockers.Models.RetroOrphanParcel;
import com.dotcode.duoline.axdlockers.Models.RetroUser;
import com.dotcode.duoline.axdlockers.Models.RetroUserXRight;
import com.dotcode.duoline.axdlockers.Network.SetRequests;
import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.Helper;
import com.dotcode.duoline.axdlockers.Utils.SaveSharedPreferences;

import java.util.ArrayList;
import java.util.List;



public class AddOrphanParcelActivity extends AppCompatActivity implements SetRequests.GetDataResponse {

    private EditText parcelDescriptionsEditText, commentsEditText;
    private TextView bAdd;
    private ArrayList<String> first4Strings = new ArrayList<String>();
    private CardView addCardView;
    private ProgressBar progressBar;
    private RetroOrphanParcel orphanParcel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_orphan_parcel);
        ArrayList<String> lines = getIntent().getStringArrayListExtra("first4lines");

        parcelDescriptionsEditText = (EditText) findViewById(R.id.parcelDescriptionsEditText);
        commentsEditText = (EditText) findViewById(R.id.commentsEditText);
        addCardView = (CardView) findViewById(R.id.bAddCardView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar12);
        bAdd = (TextView) findViewById(R.id.bAdd);

        if(lines != null){
            first4Strings = lines;
            String parcelDesc = "";
            if(!first4Strings.isEmpty()){
                parcelDesc += first4Strings.get(0) + "\n";
                parcelDesc += first4Strings.get(2).equals("") ? first4Strings.get(2) : first4Strings.get(2) + " - ";
                parcelDesc += first4Strings.get(1).equals("") ? first4Strings.get(2) : "Unit # "  + first4Strings.get(1) + "\n";
                parcelDesc += first4Strings.get(3);
                parcelDescriptionsEditText.setText(parcelDesc);
            }
            // Toast.makeText(this, ""+first4Strings.toString(), Toast.LENGTH_SHORT).show();
        }
        if (SaveSharedPreferences.getUserId(getApplicationContext()) != 0) {
            progressBar.setVisibility(View.VISIBLE);
            new SetRequests(getApplicationContext(), AddOrphanParcelActivity.this, Helper.REQUEST_CHECK_USER, null, null);
        }
        setAddButtonState(false);
        addCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orphanParcel = new RetroOrphanParcel(parcelDescriptionsEditText.getText().toString());
                if (!commentsEditText.getText().toString().equals("")){
                    orphanParcel.setComments(commentsEditText.getText().toString());
                }
                progressBar.setVisibility(View.VISIBLE);
                new SetRequests(getApplicationContext(), AddOrphanParcelActivity.this, Helper.REQUEST_INSERT_ORPHAN_PARCEL, null, orphanParcel);
            }
        });

    }

    private void setAddButtonState(boolean state){
        addCardView.setEnabled(state);
        bAdd.setTextColor(state ? ContextCompat.getColor(getApplicationContext(), R.color.white) : ContextCompat.getColor(getApplicationContext(), R.color.white_overlay));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void alertDialogRights(Context ctx, String title, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    @Override
    public void onResponse(int currentRequestId, Object result) {
        progressBar.setVisibility(View.INVISIBLE);
        if (currentRequestId == Helper.REQUEST_CHECK_USER && result instanceof RetroUser) {
            List<RetroUserXRight> userXRights = ((RetroUser) result).getUserXRights();
            boolean state = Helper.userHaveRight(userXRights, "CREATE_ORPHAN_PARCEL");
            if (!state){
                alertDialogRights(AddOrphanParcelActivity.this, getString(R.string.no_proper_rights), getString(R.string.no_create_orphan_parcel));
            }
            setAddButtonState(state);
        }
        if (currentRequestId == Helper.REQUEST_INSERT_ORPHAN_PARCEL && result instanceof RetroOrphanParcel) {
            orphanParcel = (RetroOrphanParcel) result;
            Intent i = new Intent(AddOrphanParcelActivity.this, SummaryOrphanActivity.class);
            i.putExtra("createdAt", orphanParcel.getCreatedAt());
            i.putExtra("parceldescriptions", orphanParcel.getParcelDescriptions());
            if (orphanParcel.getComments() != null){
                i.putExtra("comments", orphanParcel.getComments());
            }
            startActivity(i);
        }
    }

    @Override
    public void onFailed(int currentRequestId, boolean mustLogOut) {
        progressBar.setVisibility(View.INVISIBLE);
    }
}
