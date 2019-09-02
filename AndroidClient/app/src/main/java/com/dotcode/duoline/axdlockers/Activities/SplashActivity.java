package com.dotcode.duoline.axdlockers.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.dotcode.duoline.axdlockers.BuildConfig;
import com.dotcode.duoline.axdlockers.Models.RetroBuildingXUser;
import com.dotcode.duoline.axdlockers.Models.RetroRole;
import com.dotcode.duoline.axdlockers.Models.RetroUser;

import com.dotcode.duoline.axdlockers.Network.GetDataService;
import com.dotcode.duoline.axdlockers.Network.RetrofitClientInstance;
import com.dotcode.duoline.axdlockers.Network.SetRequests;
import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.Helper;
import com.dotcode.duoline.axdlockers.Utils.SaveSharedPreferences;

import java.util.List;

public class SplashActivity extends AppCompatActivity implements SetRequests.GetDataResponse {
    GetDataService service;
    private TextView versionTextview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        versionTextview = (TextView) findViewById(R.id.version);
        versionTextview.setText("Version " +  BuildConfig.VERSION_NAME);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(Helper.isUserLogged(getApplicationContext())){
                    new SetRequests(getApplicationContext(), SplashActivity.this, Helper.REQUEST_CHECK_USER, null, null);
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }, 1000);
    }

    @Override
    public void onResponse(int currentRequestId, Object result) {
        if (currentRequestId == Helper.REQUEST_CHECK_USER && result instanceof RetroUser) {
            RetroRole role = ((RetroUser) result).getRole();
            if(role.getHasRelatedBuildings()){
               List<RetroBuildingXUser> buildingXUsers = ((RetroUser) result).getBuildingXUsers();

                if(buildingXUsers.size() == 0){
                    showAlert(SplashActivity.this, getString(R.string.no_user_building_title), getString(R.string.no_user_building_message) + getString(R.string.login_redirected_message));
                } else {
                    startActivity(new Intent(SplashActivity.this, MainMenuActivity.class));
                    finish();
                }

            } else {
                startActivity(new Intent(SplashActivity.this, MainMenuActivity.class));
                finish();
            }
        }
    }

    @Override
    public void onFailed(int currentRequestId, boolean mustLogOut) {
        if (mustLogOut) {
            SaveSharedPreferences.logOutUser(getApplicationContext());
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void showAlert(Context ctx, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(msg);
//        builder.setIcon(R.drawable.ic_error_outline_yellow_24dp);
        builder.setCancelable(false);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SaveSharedPreferences.logOutUser(getApplicationContext());
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
        builder.show();
    }

}
