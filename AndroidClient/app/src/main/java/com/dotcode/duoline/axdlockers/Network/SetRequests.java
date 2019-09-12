package com.dotcode.duoline.axdlockers.Network;


import android.content.Context;

import android.util.Log;
import android.widget.Toast;

import com.dotcode.duoline.axdlockers.Models.RetroAddress;
import com.dotcode.duoline.axdlockers.Models.RetroAddressList;
import com.dotcode.duoline.axdlockers.Models.RetroCityList;
import com.dotcode.duoline.axdlockers.Models.RetroError;
import com.dotcode.duoline.axdlockers.Models.RetroFilteredResidentsList;
import com.dotcode.duoline.axdlockers.Models.RetroFullName;
import com.dotcode.duoline.axdlockers.Models.RetroLocker;
import com.dotcode.duoline.axdlockers.Models.RetroParcel;
import com.dotcode.duoline.axdlockers.Models.RetroParcelID;
import com.dotcode.duoline.axdlockers.Models.RetroLockerHistory;
import com.dotcode.duoline.axdlockers.Models.RetroLockerList;
import com.dotcode.duoline.axdlockers.Models.RetroNotification;
import com.dotcode.duoline.axdlockers.Models.RetroOrphanParcel;
import com.dotcode.duoline.axdlockers.Models.RetroSecurityCode;
import com.dotcode.duoline.axdlockers.Models.RetroTokenList;
import com.dotcode.duoline.axdlockers.Models.RetroUser;
import com.dotcode.duoline.axdlockers.Models.RetroVirtualParcel;
import com.dotcode.duoline.axdlockers.Models.RetroVirtualParcelID;
import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.Helper;
import com.dotcode.duoline.axdlockers.Utils.SaveSharedPreferences;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetRequests {
    private int requestId;
    final private GetDataResponse mHandler;
    private Context context;
    GetDataService service;
    Map<String,String> parameters;
    Object body;
    List<String> optionalList = null;

    public interface GetDataResponse {
        void onResponse(int currentRequestId, Object result);
        void onFailed(int currentRequestId, boolean mustLogOut);
    }
    //constructor
    public SetRequests(Context context, GetDataResponse handler, int currentRequestId, Map<String, String> cv, Object body, List<String> optionalList){
        requestId = currentRequestId;
        mHandler = handler;
        this.context = context;
        parameters = cv;
        this.body = body;
        this.optionalList = optionalList;
        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        attemptGetNewToken();
    }

    public SetRequests(Context context, GetDataResponse handler, int currentRequestId, Map<String, String> cv, Object body){
        requestId = currentRequestId;
        mHandler = handler;
        this.context = context;
        parameters = cv;
        this.body = body;
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
                Call<RetroUser> callCheckuser = service.checkUser(SaveSharedPreferences.getUserId(context),
                        SaveSharedPreferences.getAccesToken(context),
                        "userXRights.right.code,role,buildingXUsers.building");
                callRetrofit(callCheckuser);
                break;
            case Helper.REQUEST_LOCKERS:
                Call<RetroLockerList> callLockers = service.getLockers(parameters,
                        SaveSharedPreferences.getAccesToken(context));
                callRetrofit(callLockers);
                break;
            case Helper.REQUEST_MANUAL_LOCKERS:
                Call<RetroLockerList> callLockers1 = service.getLockerByNumberAndAddress(parameters,
                        SaveSharedPreferences.getAccesToken(context));
                callRetrofit(callLockers1);
                break;
            case Helper.REQUEST_ADDRESSES:
                Call<RetroAddressList> callAddresses = service.getAddresses(parameters,
                        SaveSharedPreferences.getAccesToken(context));
                callRetrofit(callAddresses);
                break;
            case Helper.REQUEST_CITIES:
                Call<RetroCityList> callCities = service.getCities(parameters,
                        SaveSharedPreferences.getAccesToken(context));
                callRetrofit(callCities);
                break;
            case Helper.REQUEST_INSERT_ADDRESS:
                Call<RetroAddress> callInsertAddress = service.createAddress(((RetroAddress)body),
                        SaveSharedPreferences.getAccesToken(context));
                callRetrofit(callInsertAddress);
                break;
            case Helper.REQUEST_INSERT_LOCKER:
                Call<RetroLocker> callSetLocker = service.createLocker(((RetroLocker)body),
                        SaveSharedPreferences.getAccesToken(context));
                callRetrofit(callSetLocker);
                break;
            case Helper.REQUEST_DELETE_PARCEL:
                int id = Integer.valueOf(parameters.get("ID"));
                Call<Void> callDeleteParcel = service.deleteParcel(id,
                        SaveSharedPreferences.getAccesToken(context));
                callRetrofit(callDeleteParcel);
                break;
            case Helper.REQUEST_DELETE_VIRTUAL_PARCEL:
                int id1 = Integer.valueOf(parameters.get("ID"));
                Call<Void> callDeleteVirtualParcel = service.deleteLVirtualParcel(id1,
                        SaveSharedPreferences.getAccesToken(context));
                callRetrofit(callDeleteVirtualParcel);
                break;
            case Helper.REQUEST_DELETE_LOCKER_HISTORY:
                int id2 = Integer.valueOf(parameters.get("ID"));
                Call<Void> callDeleteHistory = service.deleteLockerHistory(id2,
                        SaveSharedPreferences.getAccesToken(context));
                callRetrofit(callDeleteHistory);
                break;
            case Helper.REQUEST_INSERT_PARCEL:
                Call<RetroParcel> callInsertParcel = service.createParcel(((RetroParcel)body),
                        SaveSharedPreferences.getAccesToken(context));
                callRetrofit(callInsertParcel);
                break;
            case Helper.REQUEST_INSERT_LOCKER_HISTORY:
                Call<RetroLockerHistory> callInsertHistory = service.createLHs(((RetroLockerHistory)body),
                        SaveSharedPreferences.getAccesToken(context));
                callRetrofit(callInsertHistory);
                break;
            case Helper.REQUEST_INSERT_NOTIFICATION:
                Call<RetroNotification> callInsertNotification = service.createNotification(((RetroParcelID)body),
                        SaveSharedPreferences.getAccesToken(context));
                callRetrofit(callInsertNotification);
                break;
            case Helper.REQUEST_INSERT_VIRTUAL_NOTIFICATION:
                Call<RetroNotification> callInsertVirtualNotification = service.createVirtualNotification(((RetroVirtualParcelID)body),
                        SaveSharedPreferences.getAccesToken(context));
                callRetrofit(callInsertVirtualNotification);
                break;
            case Helper.REQUEST_RESIDENTS_GET_BY_FULL_NAME_OR_UNIT:
                Call<RetroFilteredResidentsList> callGetResidentByFullNameOrUnit = service.getResidentByFullNameOrUnit((RetroFullName) body,
                        SaveSharedPreferences.getAccesToken(context), parameters);
                callRetrofit(callGetResidentByFullNameOrUnit);
                break;
            case Helper.REQUEST_INSERT_ORPHAN_PARCEL:
                Call<RetroOrphanParcel> callInsertOrphan = service.createOrphanParcel(((RetroOrphanParcel) body),
                        SaveSharedPreferences.getAccesToken(context));
                callRetrofit(callInsertOrphan);
                break;
            case Helper.REQUEST_NEW_SECURITY_CODE:
                Call<RetroSecurityCode> callNewsecurity = service.getSecurityCode(SaveSharedPreferences.getAccesToken(context));
                callRetrofit(callNewsecurity);
                break;
            case Helper.REQUEST_INSERT_VIRTUAL_PARCEL:
                Call<RetroVirtualParcel> callInsertVirtualParcel = service.createVirtualParcel((RetroVirtualParcel) body,
                        SaveSharedPreferences.getAccesToken(context));
                callRetrofit(callInsertVirtualParcel);
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
                    SaveSharedPreferences.logOutUser(context);
                    String msg = "";
                    if (response.errorBody() != null) {
                        Gson gson = new Gson();
                        RetroError error = null;
                        try {
                            error = gson.fromJson(response.errorBody().charStream(), RetroError.class);
                            msg += response.code() + " - " + error.getMessage();
                        } catch (JsonSyntaxException e) {
                            msg += response.code();
                            e.printStackTrace();
                        } catch (JsonIOException e) {
                            msg += response.code();
                            e.printStackTrace();
                        }
                    } else {
                        msg += response.code();
                    }
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    mHandler.onFailed(requestId, true);

                }
            }

            @Override
            public void onFailure(Call<RetroTokenList> call, Throwable t) {

                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public <T> void callRetrofit(Call<T> call) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                String url = response.raw().request().url().toString();
                Log.v("API_CALL:", url);
                if(body != null)
                    Log.v("API CALL BODY:", body.toString());
                if (response.isSuccessful()) {
                    mHandler.onResponse(requestId, response.body());
                } else {
                        String msg = "";
                        if (response.errorBody() != null) {
                            Gson gson = new Gson();
                            RetroError error = null;
                            try {
                                error = gson.fromJson(response.errorBody().charStream(), RetroError.class);
                                msg += response.code() + " - " + error.getMessage();
                            } catch (JsonSyntaxException e) {
                                msg += response.code();
                                e.printStackTrace();
                            } catch (JsonIOException e) {
                                msg += response.code();
                                e.printStackTrace();
                            }
                        } else {
                            msg += response.code();
                        }
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                        mHandler.onFailed(requestId, false);
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
