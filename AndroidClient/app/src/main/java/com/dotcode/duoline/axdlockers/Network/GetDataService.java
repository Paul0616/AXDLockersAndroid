package com.dotcode.duoline.axdlockers.Network;


import com.dotcode.duoline.axdlockers.Models.RetroTokenList;
import com.dotcode.duoline.axdlockers.Utils.Helper;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetDataService {

    @GET("tokens?")
    Call<RetroTokenList> getToken(
            @Query("filter[password]") String email, @Query("filter[password]") String encryptedPassword);
}
