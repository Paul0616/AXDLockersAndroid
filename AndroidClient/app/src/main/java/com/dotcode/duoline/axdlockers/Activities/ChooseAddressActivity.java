package com.dotcode.duoline.axdlockers.Activities;

import android.content.ContentValues;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dotcode.duoline.axdlockers.Network.GetDataService;
import com.dotcode.duoline.axdlockers.Network.SetRequests;
import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.Helper;

import java.util.List;

public class ChooseAddressActivity extends AppCompatActivity implements SetRequests.GetDataResponse {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_address);
        setTitle("Choose Address");
        ContentValues param = new ContentValues();
        param.put("expand", "city.state");
        param.put("sort", "streetName");
        param.put("per-page", 20);
        param.put("page", 2);
        new SetRequests(getApplicationContext(), ChooseAddressActivity.this, Helper.REQUEST_ADDRESSES, param)
    }

    @Override
    public void onResponse(int currentRequestId, Object result) {

    }

    @Override
    public void onFailed(int currentRequestId, boolean mustLogOut) {

    }
}
