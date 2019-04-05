package com.dotcode.duoline.axdlockers.Network;


import com.dotcode.duoline.axdlockers.Models.RetroAddress;
import com.dotcode.duoline.axdlockers.Models.RetroAddressList;
import com.dotcode.duoline.axdlockers.Models.RetroLocker;
import com.dotcode.duoline.axdlockers.Models.RetroLockerList;
import com.dotcode.duoline.axdlockers.Models.RetroTokenList;
import com.dotcode.duoline.axdlockers.Models.RetroUser;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetDataService {

    @GET("tokens?")
    Call<RetroTokenList> getToken(
            @Query("filter[email]") String email, @Query("filter[password]") String encryptedPassword);

    @GET("users/{id}")
    Call<RetroUser> checkUser(@Path("id") int userId, @Query("access-token") String token);

    @GET("lockers")
    Call<RetroLockerList> getLockers(@Query("filter[qrCode]") String qrCode, @Query("access-token") String token);

    @GET("addresses")
    Call<RetroAddressList> getAddresses(@Query("sort") String sort, @Query("expand") String expand, @Query("per-page") int perPage,
                                        @Query("page") int page, @Query("access-token") String token);
}
