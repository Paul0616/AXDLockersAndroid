package com.dotcode.duoline.axdlockers.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.dotcode.duoline.axdlockers.Models.RetroToken;
import com.dotcode.duoline.axdlockers.Models.RetroTokenList;
import com.dotcode.duoline.axdlockers.Models.User;

import com.dotcode.duoline.axdlockers.Network.GetDataService;
import com.dotcode.duoline.axdlockers.Network.RetrofitClientInstance;
import com.dotcode.duoline.axdlockers.Network.SetRequests;
import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.Helper;
import com.dotcode.duoline.axdlockers.Utils.SaveSharedPreferences;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                    new SetRequests(this, this, Helper.REQUEST_CHECK_USER);
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }, 1000);


    }

//    private void attemptCheckUserIdAvalability() {
//        long tokenExpireAt = SaveSharedPreferences.getTokenExpireAt(getApplicationContext());
//        long now = Calendar.getInstance(Locale.getDefault()).getTimeInMillis() / 1000L;
//        boolean tokenIsExpired = false;
//        if (now > tokenExpireAt) {
//            tokenIsExpired = true;
//        }
//        if (tokenIsExpired) {
//            tokenRequest();
//        } else {
//            checkUser();
//        }
//    }

    @Override
    public void onResponse(int currentRequestId) {
        int yfdcdy = currentRequestId;
    }

    @Override
    public void onFailed(int currentRequestId) {

    }

//    private void tokenRequest() {
//        Call<RetroTokenList> call = service.getToken(SaveSharedPreferences.getEmail(getApplicationContext()), SaveSharedPreferences.getEncryptedPassword(getApplicationContext()));
//        call.enqueue(new Callback<RetroTokenList>() {
//            @Override
//            public void onResponse(Call<RetroTokenList> call, Response<RetroTokenList> response) {
//               // showProgress(false);
//
//                if (response.isSuccessful()) {
//                    RetroToken token = response.body().getToken();
//                    SaveSharedPreferences.setUserId(getApplicationContext(), token.getUserId());
//                    SaveSharedPreferences.setTokenExpireAt(getApplicationContext(), token.getTokenExpiresAt());
//                    SaveSharedPreferences.setIsAdmin(getApplicationContext(), token.isSuperAdmin());
//                    SaveSharedPreferences.setAccesToken(getApplicationContext(), token.getAccessToken());
//                    checkUser();
//                } else {
//                    try {
//                        SaveSharedPreferences.logOutUser(getApplicationContext());
//                        String url = response.raw().request().url().toString();
//
//                        String responseBody = "";
//                        if (response.errorBody() != null) {
//                            responseBody= response.errorBody().string();
//                        }
//                        Toast.makeText(SplashActivity.this, response.code() + " " + responseBody, Toast.LENGTH_LONG).show();
//                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//                        finish();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<RetroTokenList> call, Throwable t) {
//           //     showProgress(false);
//
//                Toast.makeText(SplashActivity.this, "Something went wrong...Internet appear to be offline!", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

//    private void checkUser() {
//        Call<User> call = service.checkUser(SaveSharedPreferences.getUserId(getApplicationContext()), SaveSharedPreferences.getAccesToken(getApplicationContext()));
//        call.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//                // showProgress(false);
//
//                if (response.isSuccessful()) {
//                    User user = response.body();
//                    if (user != null) {
//                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                        finish();
//                    }
//                } else {
//                    try {
//                        SaveSharedPreferences.logOutUser(getApplicationContext());
//                        String url = response.raw().request().url().toString();
//
//                        String responseBody = "";
//                        if (response.errorBody() != null) {
//                            responseBody= response.errorBody().string();
//                        }
//                        Toast.makeText(SplashActivity.this, response.code() + " " + responseBody, Toast.LENGTH_LONG).show();
//                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//                        finish();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<User> call, Throwable t) {
//                //     showProgress(false);
//
//                Toast.makeText(SplashActivity.this, "Something went wrong...Internet appear to be offline!", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
