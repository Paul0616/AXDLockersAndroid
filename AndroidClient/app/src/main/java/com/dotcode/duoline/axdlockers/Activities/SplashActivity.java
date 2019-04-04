package com.dotcode.duoline.axdlockers.Activities;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.dotcode.duoline.axdlockers.Models.RetroToken;
import com.dotcode.duoline.axdlockers.Models.RetroTokenList;
import com.dotcode.duoline.axdlockers.Network.GetDataService;
import com.dotcode.duoline.axdlockers.Network.RetrofitClientInstance;
import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.Helper;
import com.dotcode.duoline.axdlockers.Utils.SaveSharedPreferences;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
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
                    //attemptCheckUserIdAvalability();
//                    RetrofitClientInstance controller = new RetrofitClientInstance();
//                    controller.start();
                } else {
                    //startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//                    finish();

                    Call<RetroTokenList> call = service.getToken(SaveSharedPreferences.getEmail(getApplicationContext()), SaveSharedPreferences.getEncryptedPassword(getApplicationContext()));
                    call.enqueue(new Callback<RetroTokenList>() {
                        @Override
                        public void onResponse(Call<RetroTokenList> call, Response<RetroTokenList> response) {
                           // progressDoalog.dismiss();

                            if (response.isSuccessful()) {
                                RetroToken token = response.body().getToken();
                                SaveSharedPreferences.setUserId(getApplicationContext(), token.getUserId());
                                SaveSharedPreferences.setTokenExpireAt(getApplicationContext(), token.getTokenExpiresAt());
                                SaveSharedPreferences.setIsAdmin(getApplicationContext(), token.isSuperAdmin());
                                SaveSharedPreferences.setAccesToken(getApplicationContext(), token.getAccessToken());
                            } else {
                                try {
                                    if (response.errorBody() != null) {
                                        String responseBody = response.errorBody().string();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<RetroTokenList> call, Throwable t) {
                          //  progressDoalog.dismiss();
                            Toast.makeText(SplashActivity.this, "Something went wrong...Internet appear to be offline!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }, 1000);
    }
}
