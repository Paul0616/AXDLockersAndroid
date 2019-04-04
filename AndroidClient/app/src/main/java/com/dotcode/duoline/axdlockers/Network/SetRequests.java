package com.dotcode.duoline.axdlockers.Network;


import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.dotcode.duoline.axdlockers.Activities.LoginActivity;
import com.dotcode.duoline.axdlockers.Activities.SplashActivity;
import com.dotcode.duoline.axdlockers.Models.RetroToken;
import com.dotcode.duoline.axdlockers.Models.RetroTokenList;
import com.dotcode.duoline.axdlockers.Models.User;
import com.dotcode.duoline.axdlockers.Utils.Helper;
import com.dotcode.duoline.axdlockers.Utils.SaveSharedPreferences;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetRequests {
    private int requestId;
    final private GetDataResponse mHandler;
    private Context context;
    GetDataService service;

    public interface GetDataResponse {
        void onResponse(int currentRequestId);
        void onFailed(int currentRequestId, );
    }
    //constructor
    public SetRequests(Context context, GetDataResponse handler, int currentRequestId){
        requestId = currentRequestId;
        mHandler = handler;
        context = context;
        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        attemptCheckUserIdAvalability();
    }


    private void attemptCheckUserIdAvalability() {
        long tokenExpireAt = SaveSharedPreferences.getTokenExpireAt(context);
        long now = Calendar.getInstance(Locale.getDefault()).getTimeInMillis() / 1000L;
        boolean tokenIsExpired = false;
        if (now > tokenExpireAt) {
            tokenIsExpired = true;
        }
        if (tokenIsExpired) {
           tokenRequest();
        } else {
          callMethod(requestId);
        }
    }

    private void callMethod(int requestId){
        switch (requestId) {
            case Helper.REQUEST_CHECK_USER:
                checkUser();
                break;
        }
    }
    private void tokenRequest() {
        Call<RetroTokenList> call = service.getToken(SaveSharedPreferences.getEmail(context), SaveSharedPreferences.getEncryptedPassword(context));
        call.enqueue(new Callback<RetroTokenList>() {
            @Override
            public void onResponse(Call<RetroTokenList> call, Response<RetroTokenList> response) {

                if (response.isSuccessful()) {
                    RetroToken token = response.body().getToken();
                    SaveSharedPreferences.setUserId(context, token.getUserId());
                    SaveSharedPreferences.setTokenExpireAt(context, token.getTokenExpiresAt());
                    SaveSharedPreferences.setIsAdmin(context, token.isSuperAdmin());
                    SaveSharedPreferences.setAccesToken(context, token.getAccessToken());
                    callMethod(requestId);
                } else {
                    try {
                        SaveSharedPreferences.logOutUser(context);
                        String url = response.raw().request().url().toString();

                        String responseBody = "";
                        if (response.errorBody() != null) {
                            responseBody= response.errorBody().string();
                        }
                        Toast.makeText(context, response.code() + " " + responseBody, Toast.LENGTH_LONG).show();
                        mHandler.onFailed(requestId); // should return to login screen
//                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RetroTokenList> call, Throwable t) {

                Toast.makeText(context, "Something went wrong...Internet appear to be offline!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUser() {
        Call<User> call = service.checkUser(SaveSharedPreferences.getUserId(context), SaveSharedPreferences.getAccesToken(context));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                // showProgress(false);

                if (response.isSuccessful()) {
                    User user = response.body();
                    if (user != null) {
                        mHandler.onResponse(requestId);
//                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                        finish();
                    }
                } else {
                    try {
                        SaveSharedPreferences.logOutUser(context);
                        String url = response.raw().request().url().toString();

                        String responseBody = "";
                        if (response.errorBody() != null) {
                            responseBody= response.errorBody().string();
                        }
                        Toast.makeText(context, response.code() + " " + responseBody, Toast.LENGTH_LONG).show();
                        mHandler.onFailed(requestId); // should return to login screen
//                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                //     showProgress(false);

                Toast.makeText(context, "Something went wrong...Internet appear to be offline!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
