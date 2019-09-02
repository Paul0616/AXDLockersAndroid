package com.dotcode.duoline.axdlockers.Network;


import android.content.Context;

import android.widget.Toast;

import com.dotcode.duoline.axdlockers.Models.RetroAddress;
import com.dotcode.duoline.axdlockers.Models.RetroAddressList;
import com.dotcode.duoline.axdlockers.Models.RetroBuildingList;
import com.dotcode.duoline.axdlockers.Models.RetroCityList;
import com.dotcode.duoline.axdlockers.Models.RetroFilteredResidentsList;
import com.dotcode.duoline.axdlockers.Models.RetroLocker;
import com.dotcode.duoline.axdlockers.Models.RetroLockerBuildingResident;
import com.dotcode.duoline.axdlockers.Models.RetroLockerBuildingResidentID;
import com.dotcode.duoline.axdlockers.Models.RetroLockerBuildingResidentsList;
import com.dotcode.duoline.axdlockers.Models.RetroLockerHistory;
import com.dotcode.duoline.axdlockers.Models.RetroLockerHistoryList;
import com.dotcode.duoline.axdlockers.Models.RetroLockerList;
import com.dotcode.duoline.axdlockers.Models.RetroNotification;
import com.dotcode.duoline.axdlockers.Models.RetroTokenList;
import com.dotcode.duoline.axdlockers.Models.RetroUser;
import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.Helper;
import com.dotcode.duoline.axdlockers.Utils.SaveSharedPreferences;

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
                checkUser();
                break;
            case Helper.REQUEST_LOCKERS:
                getLockers(parameters);
                break;
            case Helper.REQUEST_ADDRESSES:
                getAddressers(parameters);
                break;
            case Helper.REQUEST_CITIES:
                getCities(parameters);
                break;
            case Helper.REQUEST_INSERT_ADDRESS:
                setAddress(body);
                break;
            case Helper.REQUEST_INSERT_LOCKER:
                setLocker(body);
                break;
            case Helper.REQUEST_LOCKER_HISTORIES:
                getLockerHistories(parameters, optionalList);
                break;
            case Helper.REQUEST_FILTERED_RESIDENTS:
                getFilteredResidents(parameters);
                break;
            case Helper.REQUEST_CHECK_BUILDING:
                getBuildings(parameters, optionalList);
                break;
            case Helper.REQUEST_DELETE_LOCKER_BUILDING_RESIDENT:
                deleteLBR(parameters);
                break;
            case Helper.REQUEST_DELETE_LOCKER_HISTORY:
                deleteLH(parameters);
                break;
            case Helper.REQUEST_LOCKER_BUILDING_RESIDENT:
                getLBRs(parameters);
                break;
            case Helper.REQUEST_INSERT_LOCKER_BUILDING_RESIDENT:
                createLBRs(body);
                break;
            case Helper.REQUEST_INSERT_LOCKER_HISTORY:
                createLHs(body);
                break;
            case Helper.REQUEST_INSERT_NOTIFICATION:
                createNotification(body);
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

                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUser() {
        Call<RetroUser> call = service.checkUser(SaveSharedPreferences.getUserId(context), SaveSharedPreferences.getAccesToken(context), "userXRights.right.code,role,buildingXUsers.building");
        call.enqueue(new Callback<RetroUser>() {
            @Override
            public void onResponse(Call<RetroUser> call, Response<RetroUser> response) {
                // showProgress(false);

                if (response.isSuccessful()) {
                    RetroUser user = response.body();
                    if (user != null) {
                        Object userObj = (Object) user;

                       SaveSharedPreferences.setFirstName(context, ((RetroUser) userObj).getFirstName());
                        SaveSharedPreferences.setLastName(context, ((RetroUser) userObj).getLastName());
                        mHandler.onResponse(requestId, userObj);
//                        startActivity(new Intent(SplashActivity.this, QRScanActivity.class));
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

                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLockers(Map<String, String> parameters) {
        //if (parameters.containsKey("qrCode")) {
        //    String qrCode = parameters.get("qrCode").toString();
            Call<RetroLockerList> call = service.getLockers(parameters, SaveSharedPreferences.getAccesToken(context));
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
                    Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            });
       // }
    }

    private void setLocker(Object locker) {

        Call<RetroLocker> call = service.createLocker(((RetroLocker)locker), SaveSharedPreferences.getAccesToken(context));
        call.enqueue(new Callback<RetroLocker>() {
            @Override
            public void onResponse(Call<RetroLocker> call, Response<RetroLocker> response) {
                if (response.isSuccessful()) {
                    RetroLocker locker = response.body();
                    Object addresObj = locker;
//                        List<Object> addressesObj = new ArrayList<Object>();
//                        lockers.add(locker);
                    mHandler.onResponse(requestId, addresObj);

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
            public void onFailure(Call<RetroLocker> call, Throwable t) {
                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getAddressers(Map<String, String> parameters) {
            Call<RetroAddressList> call = service.getAddresses(parameters, SaveSharedPreferences.getAccesToken(context));
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
                    Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            });

    }

    private void setAddress(Object address) {

        Call<RetroAddress> call = service.createAddress(((RetroAddress)address), SaveSharedPreferences.getAccesToken(context));
        call.enqueue(new Callback<RetroAddress>() {
            @Override
            public void onResponse(Call<RetroAddress> call, Response<RetroAddress> response) {
                if (response.isSuccessful()) {
                    RetroAddress address = response.body();
                    Object addresObj = address;
//                        List<Object> addressesObj = new ArrayList<Object>();
//                        lockers.add(locker);
                    mHandler.onResponse(requestId, addresObj);

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
            public void onFailure(Call<RetroAddress> call, Throwable t) {
                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getCities(Map<String, String> parameters) {
        Call<RetroCityList> call = service.getCities(parameters, SaveSharedPreferences.getAccesToken(context));
        call.enqueue(new Callback<RetroCityList>() {
            @Override
            public void onResponse(Call<RetroCityList> call, Response<RetroCityList> response) {
                if (response.isSuccessful()) {
                    RetroCityList cities = response.body();
                    Object citiesObj = cities;

                    mHandler.onResponse(requestId, cities);

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
            public void onFailure(Call<RetroCityList> call, Throwable t) {
                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getLockerHistories(Map<String, String> parameters, List<String> optionalList) {
        Call<RetroLockerHistoryList> call = service.getHistories(parameters, SaveSharedPreferences.getAccesToken(context), optionalList);
        call.enqueue(new Callback<RetroLockerHistoryList>() {
            @Override
            public void onResponse(Call<RetroLockerHistoryList> call, Response<RetroLockerHistoryList> response) {
                String url = response.raw().request().url().toString();
                if (response.isSuccessful()) {
                    RetroLockerHistoryList lockerHistories = response.body();
                    Object lockerHistoriesObj = lockerHistories;

                    mHandler.onResponse(requestId, lockerHistories);

                } else {
                    try {
                        SaveSharedPreferences.logOutUser(context);

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
            public void onFailure(Call<RetroLockerHistoryList> call, Throwable t) {
                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getFilteredResidents(Map<String, String> parameters) {
        Call<RetroFilteredResidentsList> call = service.getFilteredResidents(parameters, SaveSharedPreferences.getAccesToken(context));
        call.enqueue(new Callback<RetroFilteredResidentsList>() {
            @Override
            public void onResponse(Call<RetroFilteredResidentsList> call, Response<RetroFilteredResidentsList> response) {
                if (response.isSuccessful()) {
                    RetroFilteredResidentsList filteredResidents = response.body();
                    Object filteredResidentsObj = filteredResidents;

                    mHandler.onResponse(requestId, filteredResidentsObj);

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
            public void onFailure(Call<RetroFilteredResidentsList> call, Throwable t) {
                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getBuildings(Map<String, String> parameters, List<String> optionalList) {
        Call<RetroBuildingList> call = service.getBuildings(parameters, SaveSharedPreferences.getAccesToken(context), optionalList);
        call.enqueue(new Callback<RetroBuildingList>() {
            @Override
            public void onResponse(Call<RetroBuildingList> call, Response<RetroBuildingList> response) {
                String url = response.raw().request().url().toString();
                if (response.isSuccessful()) {
                    RetroBuildingList buildingList = response.body();
                    Object buildingListObj = buildingList;

                    mHandler.onResponse(requestId, buildingListObj);

                } else {
                    try {
                        SaveSharedPreferences.logOutUser(context);


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
            public void onFailure(Call<RetroBuildingList> call, Throwable t) {
                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void deleteLBR(Map<String, String> parameters) {
        int id = Integer.valueOf(parameters.get("ID"));
        Call<Void> call = service.deleteLBR(id, SaveSharedPreferences.getAccesToken(context));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                    mHandler.onResponse(requestId, null);

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
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void deleteLH(Map<String, String> parameters) {
        int id = Integer.valueOf(parameters.get("ID"));
        Call<Void> call = service.deleteLH(id, SaveSharedPreferences.getAccesToken(context));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                    mHandler.onResponse(requestId, null);

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
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getLBRs(Map<String, String> parameters) {
        Call<RetroLockerBuildingResidentsList> call = service.getLBRs(parameters, SaveSharedPreferences.getAccesToken(context));
        call.enqueue(new Callback<RetroLockerBuildingResidentsList>() {
            @Override
            public void onResponse(Call<RetroLockerBuildingResidentsList> call, Response<RetroLockerBuildingResidentsList> response) {
                if (response.isSuccessful()) {
                    RetroLockerBuildingResidentsList lbrs = response.body();
                    Object lbrsObj = lbrs;

                    mHandler.onResponse(requestId, lbrsObj);

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
            public void onFailure(Call<RetroLockerBuildingResidentsList> call, Throwable t) {
                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void createLBRs(Object lbr) {

        Call<RetroLockerBuildingResident> call = service.createLBRs(((RetroLockerBuildingResident)lbr), SaveSharedPreferences.getAccesToken(context));
        call.enqueue(new Callback<RetroLockerBuildingResident>() {
            @Override
            public void onResponse(Call<RetroLockerBuildingResident> call, Response<RetroLockerBuildingResident> response) {
                String url = response.raw().request().url().toString();
                if (response.isSuccessful()) {
                    RetroLockerBuildingResident lbr = response.body();
                    Object lbrObj = lbr;
//                        List<Object> addressesObj = new ArrayList<Object>();
//                        lockers.add(locker);
                    mHandler.onResponse(requestId, lbrObj);

                } else {
                    try {
                        SaveSharedPreferences.logOutUser(context);


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
            public void onFailure(Call<RetroLockerBuildingResident> call, Throwable t) {
                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void createLHs(Object lh) {

        Call<RetroLockerHistory> call = service.createLHs(((RetroLockerHistory)lh), SaveSharedPreferences.getAccesToken(context));
        call.enqueue(new Callback<RetroLockerHistory>() {
            @Override
            public void onResponse(Call<RetroLockerHistory> call, Response<RetroLockerHistory> response) {
                if (response.isSuccessful()) {
                    RetroLockerHistory lh = response.body();
                    Object lhObj = lh;

                    mHandler.onResponse(requestId, lhObj);

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
            public void onFailure(Call<RetroLockerHistory> call, Throwable t) {
                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void createNotification(Object lh) {

        Call<RetroNotification> call = service.createNotification(((RetroLockerBuildingResidentID)lh), SaveSharedPreferences.getAccesToken(context));
        call.enqueue(new Callback<RetroNotification>() {
            @Override
            public void onResponse(Call<RetroNotification> call, Response<RetroNotification> response) {
                if (response.isSuccessful()) {
                    RetroNotification lh = response.body();
                    Object lhObj = lh;

                    mHandler.onResponse(requestId, lhObj);

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
            public void onFailure(Call<RetroNotification> call, Throwable t) {
                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });

    }


}
