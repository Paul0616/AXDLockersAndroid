package com.dotcode.duoline.axdlockers.Network;


import com.dotcode.duoline.axdlockers.Models.RetroAddress;
import com.dotcode.duoline.axdlockers.Models.RetroAddressList;
import com.dotcode.duoline.axdlockers.Models.RetroLocker;
import com.dotcode.duoline.axdlockers.Models.RetroLockerList;
import com.dotcode.duoline.axdlockers.Models.RetroTokenList;
import com.dotcode.duoline.axdlockers.Models.RetroUser;


import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
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
}
