package com.dotcode.duoline.axdlockers.Network;


import com.dotcode.duoline.axdlockers.Models.RetroAddress;
import com.dotcode.duoline.axdlockers.Models.RetroAddressList;
import com.dotcode.duoline.axdlockers.Models.RetroBuilding;
import com.dotcode.duoline.axdlockers.Models.RetroBuildingList;
import com.dotcode.duoline.axdlockers.Models.RetroCity;
import com.dotcode.duoline.axdlockers.Models.RetroCityList;
import com.dotcode.duoline.axdlockers.Models.RetroFilteredResident;
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


import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface GetDataService {

    @GET("tokens?")
    Call<RetroTokenList> getToken(
            @Query("filter[email]") String email, @Query("filter[password]") String encryptedPassword);

    @GET("users/{id}")
    Call<RetroUser> checkUser(@Path("id") int userId, @Query("access-token") String token);

    @GET("lockers")
    Call<RetroLockerList> getLockers(@QueryMap Map<String, String> options, @Query("access-token") String token);

    @GET("addresses")
    Call<RetroAddressList> getAddresses(@QueryMap Map<String, String> options, @Query("access-token") String token);

    @GET("cities")
    Call<RetroCityList> getCities(@QueryMap Map<String, String> options, @Query("access-token") String token);

    @POST("addresses")
    Call<RetroAddress> createAddress(@Body RetroAddress address, @Query("access-token") String token);

    @POST("lockers")
    Call<RetroLocker> createLocker(@Body RetroLocker locker, @Query("access-token") String token);

    @GET("locker-histories")
    Call<RetroLockerHistoryList> getHistories(@QueryMap Map<String, String> options, @Query("access-token") String token);

    @GET("building-residents/get-filtered-residents")
    Call<RetroFilteredResidentsList> getFilteredResidents(@QueryMap Map<String, String> options, @Query("access-token") String token);

    @GET("buildings")
    Call<RetroBuildingList> getBuildings(@QueryMap Map<String, String> options, @Query("access-token") String token);

    @DELETE("locker-building-residents/{id}")
    Call<Void> deleteLBR(@Path("id") int id, @Query("access-token") String token);

    @DELETE("locker-histories/{id}")
    Call<Void> deleteLH(@Path("id") int id, @Query("access-token") String token);

    @GET("locker-building-residents")
    Call<RetroLockerBuildingResidentsList> getLBRs(@QueryMap Map<String, String> options, @Query("access-token") String token);

    @POST("locker-building-residents")
    Call<RetroLockerBuildingResident> createLBRs(@Body RetroLockerBuildingResident lbr, @Query("access-token") String token);

    @POST("locker-histories")
    Call<RetroLockerHistory> createLHs(@Body RetroLockerHistory lbr, @Query("access-token") String token);

    @POST("notifications/send-notification-to-resident")
    Call<RetroNotification> createNotification(@Body RetroLockerBuildingResidentID lbr, @Query("access-token") String token);


}
