package com.dotcode.duoline.axdlockers.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dotcode.duoline.axdlockers.Models.RetroAddress;
import com.dotcode.duoline.axdlockers.Models.RetroLocker;
import com.dotcode.duoline.axdlockers.Network.SetRequests;
import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.Helper;
import com.dotcode.duoline.axdlockers.Utils.SaveSharedPreferences;
import com.google.android.material.textfield.TextInputLayout;

public class AddLockerActivity extends AppCompatActivity implements SetRequests.GetDataResponse {

    private Menu menu;
    private AutoCompleteTextView lockerNumber;
    private TextInputEditText lockerSize;
    private TextView street, zipCode, city, optionalLabel;
    private EditText addressDetail;
    private TextInputLayout optional;
    private ImageView bAddAddress;
    private String qrCode;
    private boolean virtualLocker = false;
    private boolean addLockerOnly;


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
        optional = (TextInputLayout) findViewById(R.id.textInputLayoutOptional);
        addressDetail = (EditText) findViewById(R.id.optionalDetailEditText);
        optionalLabel = (TextView) findViewById(R.id.optionalLabel);
        virtualLocker = getIntent().getBooleanExtra("virtualLocker", false);
        addLockerOnly = SaveSharedPreferences.getAddLockerOnly(getApplicationContext());
        setTitle(virtualLocker ? "Temporary Locker" : "Add Locker");
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

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setupMenu();
            }
        });


        lockerSize.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setupMenu();
            }
        });
        SaveSharedPreferences.setAddressNull(getApplicationContext());
        optionalLabel.setVisibility(virtualLocker?View.VISIBLE:View.GONE);
        optional.setVisibility(virtualLocker?View.VISIBLE:View.GONE);
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
            if (!addLockerOnly) {
                showAlert(AddLockerActivity.this, getString(R.string.locker_added), getString(R.string.locker_number) + lockerNumber.getText().toString() +
                        getString(R.string.locker_added_message), addLockerOnly);
            } else {
                showAlert(AddLockerActivity.this, getString(R.string.locker_added), getString(R.string.locker_number) + lockerNumber.getText().toString() +
                        getString(R.string.locker_added_message1), addLockerOnly);
            }
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

    private void showAlert(Context ctx, String title, String msg, final boolean addLockerOnly) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);

        
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!addLockerOnly) {
                    Intent i = new Intent(AddLockerActivity.this, SecurityCodeActivity.class);
                    finish();  //Kill the activity from which you will go to next activity
                    startActivity(i);
                } else {
                    Intent i = new Intent(AddLockerActivity.this, QRScanActivity.class);
                    finish();  //Kill the activity from which you will go to next activity
                    startActivity(i);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!addLockerOnly) {
                    Intent i = new Intent(AddLockerActivity.this, QRScanActivity.class);
                    finish();  //Kill the activity from which you will go to next activity
                    startActivity(i);
                } else {
                    Intent i = new Intent(AddLockerActivity.this, MainMenuActivity.class);
                    finish();  //Kill the activity from which you will go to next activity
                    startActivity(i);
                }
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
            if(!virtualLocker) {
                new SetRequests(getApplicationContext(), AddLockerActivity.this, Helper.REQUEST_INSERT_LOCKER, null, locker);
//            startActivity(new Intent(HiringPacksActivity.this, MainMenuActivity.class));
//            return true;
            } else {
                if(!addressDetail.getText().toString().equals(""))
                    locker.setAddressDetail(addressDetail.getText().toString());
                SaveSharedPreferences.setLocker(getApplicationContext(), locker);
                Intent i = new Intent(AddLockerActivity.this, SecurityCodeActivity.class);
                finish();  //Kill the activity from which you will go to next activity
                startActivity(i);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
