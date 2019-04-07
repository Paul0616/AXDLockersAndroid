package com.dotcode.duoline.axdlockers.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dotcode.duoline.axdlockers.Models.RetroUser;

import com.dotcode.duoline.axdlockers.Network.GetDataService;
import com.dotcode.duoline.axdlockers.Network.RetrofitClientInstance;
import com.dotcode.duoline.axdlockers.Network.SetRequests;
import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.Helper;

import java.util.List;

public class SplashActivity extends AppCompatActivity implements SetRequests.GetDataResponse {
    GetDataService service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
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
            int id = ((RetroUser) result).getUserId();
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onFailed(int currentRequestId, boolean mustLogOut) {
        if (mustLogOut) {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
    }

}
