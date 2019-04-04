package com.dotcode.duoline.axdlockers.Network;


import com.dotcode.duoline.axdlockers.Models.RetroTokenList;
import com.dotcode.duoline.axdlockers.Models.User;
import com.dotcode.duoline.axdlockers.Utils.Helper;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetDataService {

    @GET("tokens?")
    Call<RetroTokenList> getToken(
            @Query("filter[email]") String email, @Query("filter[password]") String encryptedPassword);

    @GET("users/{id}")
    Call<User> checkUser(@Path("id") int userId, @Query("access-token") String token);
}
