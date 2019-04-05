package com.dotcode.duoline.axdlockers.Activities;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
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
import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.SaveSharedPreferences;

public class AddLockerActivity extends AppCompatActivity {

    private Menu menu;
    private AutoCompleteTextView lockerNumber;
    private TextInputEditText lockerSize;
    private TextView street, zipCode, city;
    private ImageView bAddAddress;

    private RetroAddress address = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_locker);
        setTitle("Add locker");
        lockerNumber = (AutoCompleteTextView) findViewById(R.id.lockerNumber);
        lockerSize = (TextInputEditText) findViewById(R.id.lockerSize);
        street = (TextView) findViewById(R.id.streetTextView);
        zipCode = (TextView) findViewById(R.id.zipCodeTextView);
        city = (TextView) findViewById(R.id.cityTextView);
        bAddAddress = (ImageView) findViewById(R.id.bAddAddress);

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
//        lockerSize.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
//                    setupMenu();
//                    return true;
//                }
//                return false;
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        address = SaveSharedPreferences.getAddress(getApplicationContext());
        if (address != null) {
            street.setText(address.getStreetName());
            zipCode.setText(address.getZipCode());
            city.setText(address.getCity().getName() +", " + address.getCity().getState().getName());
        }

    }

    private void setupMenu(){
        if (!lockerNumber.getText().toString().equals("") && !lockerSize.getText().toString().equals("") && address != null)
            menu.getItem(0).setEnabled(true);
        else
            menu.getItem(0).setEnabled(false);
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
//            startActivity(new Intent(HiringPacksActivity.this, MainMenuActivity.class));
//            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
