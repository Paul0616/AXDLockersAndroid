package com.dotcode.duoline.axdlockers.Activities;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dotcode.duoline.axdlockers.Models.ButtonWithImageModel;
import com.dotcode.duoline.axdlockers.Models.RetroUser;
import com.dotcode.duoline.axdlockers.Models.RetroUserXRight;
import com.dotcode.duoline.axdlockers.Network.SetRequests;
import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.Helper;
import com.dotcode.duoline.axdlockers.Utils.SaveSharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class MainMenuActivity extends AppCompatActivity implements SetRequests.GetDataResponse {

    private ConstraintLayout addParcelButton, addLockerButton;
    private ImageView parcelImage, lockerImage, bLogOut;
    private TextView parcelText, lockerText;
    private List<ButtonWithImageModel> buttonList = new ArrayList<ButtonWithImageModel>();
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        parcelImage = (ImageView) findViewById(R.id.imageViewParcel);
        lockerImage = (ImageView) findViewById(R.id.imageViewLocker);
        parcelText = (TextView) findViewById(R.id.textViewParcel);
        lockerText = (TextView) findViewById(R.id.textViewLocker);
        addParcelButton = (ConstraintLayout) findViewById(R.id.addParcelButton);
        addLockerButton = (ConstraintLayout) findViewById(R.id.addLockerButton);
        bLogOut = (ImageView) findViewById(R.id.logOutImageView);

        ButtonWithImageModel parcel = new ButtonWithImageModel(addParcelButton, parcelImage, parcelText, getApplicationContext());
        buttonList.add(parcel);
        ButtonWithImageModel locker = new ButtonWithImageModel(addLockerButton, lockerImage, lockerText, getApplicationContext());
        buttonList.add(locker);
        buttonList.get(0).setButtonEnabled(false);
        buttonList.get(1).setButtonEnabled(false);



        bLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveSharedPreferences.logOutUser(getApplicationContext());
                startActivity(new Intent(MainMenuActivity.this, LoginActivity.class));
                finish();
            }
        });

        addParcelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, OCRResultActivity.class));
            }
        });
        addLockerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, QRScanActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Helper.isUserLogged(getApplicationContext())){
            isLoading = true;
            new SetRequests(getApplicationContext(), MainMenuActivity.this, Helper.REQUEST_CHECK_USER, null, null);
        } else {
            startActivity(new Intent(MainMenuActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onResponse(int currentRequestId, Object result) {
        if (currentRequestId == Helper.REQUEST_CHECK_USER && result instanceof RetroUser) {
            List<RetroUserXRight> userXRights = ((RetroUser) result).getUserXRights();
            boolean state = userHaveRight(userXRights, "READ_RESIDENT") && userHaveRight(userXRights, "READ_BUILDING");
            buttonList.get(0).setButtonEnabled(state);
            state = userHaveRight(userXRights, "CREATE_LOCKER");
            buttonList.get(1).setButtonEnabled(state);
        }
    }

    private boolean userHaveRight(List<RetroUserXRight> userXRights, String code) {
        for(RetroUserXRight item : userXRights){
            if(item.getRight().getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onFailed(int currentRequestId, boolean mustLogOut) {

    }
}
