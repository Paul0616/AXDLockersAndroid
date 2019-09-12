package com.dotcode.duoline.axdlockers.Network;


import com.dotcode.duoline.axdlockers.Models.RetroAddress;
import com.dotcode.duoline.axdlockers.Models.RetroAddressList;
import com.dotcode.duoline.axdlockers.Models.RetroBuildingList;
import com.dotcode.duoline.axdlockers.Models.RetroCityList;
import com.dotcode.duoline.axdlockers.Models.RetroEmail;
import com.dotcode.duoline.axdlockers.Models.RetroFilteredResidentsList;
import com.dotcode.duoline.axdlockers.Models.RetroFullName;
import com.dotcode.duoline.axdlockers.Models.RetroLocker;
import com.dotcode.duoline.axdlockers.Models.RetroParcel;
import com.dotcode.duoline.axdlockers.Models.RetroParcelID;
import com.dotcode.duoline.axdlockers.Models.RetroParcelsList;
import com.dotcode.duoline.axdlockers.Models.RetroLockerHistory;
import com.dotcode.duoline.axdlockers.Models.RetroLockerHistoryList;
import com.dotcode.duoline.axdlockers.Models.RetroLockerList;
import com.dotcode.duoline.axdlockers.Models.RetroNotification;
import com.dotcode.duoline.axdlockers.Models.RetroOrphanParcel;
import com.dotcode.duoline.axdlockers.Models.RetroSecurityCode;
import com.dotcode.duoline.axdlockers.Models.RetroTokenList;
import com.dotcode.duoline.axdlockers.Models.RetroUser;
import com.dotcode.duoline.axdlockers.Models.RetroVirtualParcel;
import com.dotcode.duoline.axdlockers.Models.RetroVirtualParcelID;


import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface GetDataService {

    @GET("tokens?")
    Call<RetroTokenList> getToken(
            @Query("filter[email]") String email, @Query("filter[password]") String encryptedPassword);

    @GET("users/{id}")
    Call<RetroUser> checkUser(@Path("id") int userId, @Query("access-token") String token, @Query("expand") String roleString);

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

//    @GET("locker-histories")
//    Call<RetroLockerHistoryList> getHistories(@QueryMap Map<String, String> options, @Query("access-token") String token, @Query("filter[buildingUniqueNumber][in][]") List<String> buildingUniqueNumberList);

//    @GET("building-residents/get-filtered-residents")
//    Call<RetroFilteredResidentsList> getFilteredResidents(@QueryMap Map<String, String> options, @Query("access-token") String token);

    @POST("residents/get-by-full-name-and-unit-number")
    Call<RetroFilteredResidentsList> getResidentByFullNameOrUnit(@Body RetroFullName fullName, @Query("access-token") String token, @QueryMap Map<String, String> options);

//    @GET("buildings")
//    Call<RetroBuildingList> getBuildings(@QueryMap Map<String, String> options, @Query("access-token") String token, @Query("filter[buildingUniqueNumber][in][]") List<String> buildingUniqueNumberList);

    @DELETE("locker-building-residents/{id}")
    Call<Void> deleteParcel(@Path("id") int id, @Query("access-token") String token);

    @DELETE("virtual-parcels/{id}")
    Call<Void> deleteLVirtualParcel(@Path("id") int id, @Query("access-token") String token);

    @DELETE("locker-histories/{id}")
    Call<Void> deleteLockerHistory(@Path("id") int id, @Query("access-token") String token);

//    @GET("locker-building-residents")
//    Call<RetroParcelsList> getLBRs(@QueryMap Map<String, String> options, @Query("access-token") String token);

    @POST("locker-building-residents")
    Call<RetroParcel> createParcel(@Body RetroParcel lbr, @Query("access-token") String token);

    @POST("locker-histories")
    Call<RetroLockerHistory> createLHs(@Body RetroLockerHistory lbr, @Query("access-token") String token);

    @POST("notifications/send-notification-to-resident")
    Call<RetroNotification> createNotification(@Body RetroParcelID lbr, @Query("access-token") String token);

    @PUT("users/reset-password")
    Call<Void> resetPassword(@Body RetroEmail email);

    @POST("orphan-parcels")
    Call<RetroOrphanParcel> createOrphanParcel(@Body RetroOrphanParcel orphanParcel, @Query("access-token") String token);

    @GET("residents/get-new-security-code")
    Call<RetroSecurityCode> getSecurityCode(@Query("access-token") String token);

    @POST("virtual-parcels")
    Call<RetroVirtualParcel> createVirtualParcel(@Body RetroVirtualParcel parcel, @Query("access-token") String token);

    @POST("notifications/send-notification-to-resident-for-virtual-parcel")
    Call<RetroNotification> createVirtualNotification(@Body RetroVirtualParcelID parcelID, @Query("access-token") String token);

    @GET("lockers/search-by-number-and-address")
    Call<RetroLockerList> getLockerByNumberAndAddress(@QueryMap Map<String, String> options, @Query("access-token") String token);
}
