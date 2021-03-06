package com.dotcode.duoline.axdlockers.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dotcode.duoline.axdlockers.Models.RetroAddress;
import com.dotcode.duoline.axdlockers.Models.RetroLocker;
import com.dotcode.duoline.axdlockers.Network.SetRequests;
import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.Helper;
import com.dotcode.duoline.axdlockers.Utils.SaveSharedPreferences;

public class AddLockerActivity extends AppCompatActivity implements SetRequests.GetDataResponse {

    private Menu menu;
    private AutoCompleteTextView lockerNumber;
    private TextInputEditText lockerSize;
    private TextView street, zipCode, city;
    private ImageView bAddAddress;
    private String qrCode;

    private RetroAddress address = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_locker);
        setTitle(getString(R.string.add_locker));
        lockerNumber = (AutoCompleteTextView) findViewById(R.id.lockerNumber);
        lockerSize = (TextInputEditText) findViewById(R.id.lockerSize);
        street = (TextView) findViewById(R.id.streetTextView);
        zipCode = (TextView) findViewById(R.id.zipCodeTextView);
        city = (TextView) findViewById(R.id.cityTextView);
        bAddAddress = (ImageView) findViewById(R.id.bAddAddress);

        qrCode = getIntent().getStringExtra("qrCode");
        bAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddLockerActivity.this, ChooseAddressActivity.class));
            }
        });

        lockerNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setupMenu();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        lockerSize.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setupMenu();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        SaveSharedPreferences.setAddressNull(getApplicationContext());

    }

    @Override
    protected void onResume() {
        super.onResume();
        address = SaveSharedPreferences.getAddress(getApplicationContext());
        if (address != null) {
            street.setText(address.getStreetName());
            zipCode.setText(address.getZipCode());
            city.setText(address.getCity().getName() +", " + address.getCity().getState().getName());
            setupMenu();
        }

    }

    @Override
    public void onResponse(int currentRequestId, Object result) {
        if (currentRequestId == Helper.REQUEST_INSERT_LOCKER){
            showAlert(AddLockerActivity.this, getString(R.string.locker_added), getString(R.string.locker_number) + lockerNumber.getText().toString()+
                    getString(R.string.locker_added_message));
        }
    }

    @Override
    public void onFailed(int currentRequestId, boolean mustLogOut) {

    }

    private void setupMenu(){
        if (!lockerNumber.getText().toString().equals("") && !lockerSize.getText().toString().equals("") && address != null)
            menu.getItem(0).setEnabled(true);
        else
            menu.getItem(0).setEnabled(false);
    }

    private void showAlert(Context ctx, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);

        
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(AddLockerActivity.this, AddResidentActivity.class);
                finish();  //Kill the activity from which you will go to next activity
                startActivity(i);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(AddLockerActivity.this, MainActivity.class);
                finish();  //Kill the activity from which you will go to next activity
                startActivity(i);
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        this.menu = menu;
        menu.getItem(0).setEnabled(false);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            RetroLocker locker = new RetroLocker(0, qrCode, lockerNumber.getText().toString(),
                    lockerSize.getText().toString(), address.getId(), address);
            new SetRequests(getApplicationContext(), AddLockerActivity.this, Helper.REQUEST_INSERT_LOCKER, null, locker);
//            startActivity(new Intent(HiringPacksActivity.this, MainMenuActivity.class));
//            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
