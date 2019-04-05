package com.dotcode.duoline.axdlockers.Network;


import android.content.ContentValues;
import android.content.Context;

import android.widget.Toast;

import com.dotcode.duoline.axdlockers.Models.RetroAddress;
import com.dotcode.duoline.axdlockers.Models.RetroAddressList;
import com.dotcode.duoline.axdlockers.Models.RetroLocker;
import com.dotcode.duoline.axdlockers.Models.RetroLockerList;
import com.dotcode.duoline.axdlockers.Models.RetroToken;
import com.dotcode.duoline.axdlockers.Models.RetroTokenList;
import com.dotcode.duoline.axdlockers.Models.RetroUser;
import com.dotcode.duoline.axdlockers.Utils.Helper;
import com.dotcode.duoline.axdlockers.Utils.SaveSharedPreferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetRequests {
    private int requestId;
    final private GetDataResponse mHandler;
    private Context context;
    GetDataService service;
    ContentValues parameters;

    public interface GetDataResponse {
        void onResponse(int currentRequestId, Object result);
        void onFailed(int currentRequestId, boolean mustLogOut);
    }
    //constructor
    public SetRequests(Context context, GetDataResponse handler, int currentRequestId, ContentValues cv){
        requestId = currentRequestId;
        mHandler = handler;
        this.context = context;
        parameters = cv;
        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        attemptGetNewToken();
    }


    private void attemptGetNewToken() {
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
            case Helper.REQUEST_LOCKERS:
                getLockers(parameters);
                break;

        }
    }
    private void tokenRequest() {
        Call<RetroTokenList> call = service.getToken(SaveSharedPreferences.getEmail(context), SaveSharedPreferences.getEncryptedPassword(context));
        call.enqueue(new Callback<RetroTokenList>() {
            @Override
            public void onResponse(Call<RetroTokenList> call, Response<RetroTokenList> response) {

                if (response.isSuccessful()) {
                    RetroTokenList token = response.body();
                    SaveSharedPreferences.setUserId(context, token.getToken().getUserId());
                    SaveSharedPreferences.setTokenExpireAt(context, token.getToken().getTokenExpiresAt());
                    SaveSharedPreferences.setIsAdmin(context, token.getToken().isSuperAdmin());
                    SaveSharedPreferences.setAccesToken(context, token.getToken().getAccessToken());
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
                        mHandler.onFailed(requestId, true); // should return to login screen

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
        Call<RetroUser> call = service.checkUser(SaveSharedPreferences.getUserId(context), SaveSharedPreferences.getAccesToken(context));
        call.enqueue(new Callback<RetroUser>() {
            @Override
            public void onResponse(Call<RetroUser> call, Response<RetroUser> response) {
                // showProgress(false);

                if (response.isSuccessful()) {
                    RetroUser user = response.body();
                    if (user != null) {
                        Object userObj = (Object) user;

                       // users.add(user);
                        mHandler.onResponse(requestId, userObj);
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
                        mHandler.onFailed(requestId, true); // should return to login screen
//                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RetroUser> call, Throwable t) {
                //     showProgress(false);

                Toast.makeText(context, "Something went wrong...Internet appear to be offline!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLockers(ContentValues parameters) {
        if (parameters.containsKey("qrCode")) {
            String qrCode = parameters.get("qrCode").toString();
            Call<RetroLockerList> call = service.getLockers(qrCode, SaveSharedPreferences.getAccesToken(context));
            call.enqueue(new Callback<RetroLockerList>() {
                @Override
                public void onResponse(Call<RetroLockerList> call, Response<RetroLockerList> response) {
                    if (response.isSuccessful()) {
                        RetroLockerList locker = response.body();
                        if (locker.getLocker() != null) {
                            Object lockerObj = locker.getLocker();

                          //  lockers.add(locker);
                            mHandler.onResponse(requestId, lockerObj);
                        } else {
                            mHandler.onResponse(requestId, null);
                        }
                    } else {
                        try {
                            SaveSharedPreferences.logOutUser(context);
                            String url = response.raw().request().url().toString();

                            String responseBody = "";
                            if (response.errorBody() != null) {
                                responseBody = response.errorBody().string();
                            }
                            Toast.makeText(context, response.code() + " " + responseBody, Toast.LENGTH_LONG).show();
                            mHandler.onFailed(requestId, true);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<RetroLockerList> call, Throwable t) {
                    Toast.makeText(context, "Something went wrong...Internet appear to be offline!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getAddressers(ContentValues parameters) {
        int perPage = 20;
        int page = 1;
        String expand = "";
        String sort = "";
        if (parameters.containsKey("per-page")) {
            perPage = (int) parameters.get("per-page");
        }
        if (parameters.containsKey("page")) {
            page = (int) parameters.get("page");
        }
        if (parameters.containsKey("sort")) {
            sort = parameters.get("sort").toString();
        }
        if (parameters.containsKey("expand")) {
            expand = parameters.get("expand").toString();
        }
            Call<RetroAddressList> call = service.getAddresses(sort, expand, perPage, page, SaveSharedPreferences.getAccesToken(context));
            call.enqueue(new Callback<RetroAddressList>() {
                @Override
                public void onResponse(Call<RetroAddressList> call, Response<RetroAddressList> response) {
                    if (response.isSuccessful()) {
                        RetroAddressList addresses = response.body();
                        Object addressesObj = addresses;
//                        List<Object> addressesObj = new ArrayList<Object>();
//                        lockers.add(locker);
                        mHandler.onResponse(requestId, addressesObj);

                    } else {
                        try {
                            SaveSharedPreferences.logOutUser(context);
                            String url = response.raw().request().url().toString();

                            String responseBody = "";
                            if (response.errorBody() != null) {
                                responseBody = response.errorBody().string();
                            }
                            Toast.makeText(context, response.code() + " " + responseBody, Toast.LENGTH_LONG).show();
                            mHandler.onFailed(requestId, true);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<RetroAddressList> call, Throwable t) {
                    Toast.makeText(context, "Something went wrong...Internet appear to be offline!", Toast.LENGTH_SHORT).show();
                }
            });

    }
}
