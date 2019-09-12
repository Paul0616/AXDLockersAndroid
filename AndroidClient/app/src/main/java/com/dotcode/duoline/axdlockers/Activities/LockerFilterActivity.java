package com.dotcode.duoline.axdlockers.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;

import com.dotcode.duoline.axdlockers.R;
import com.google.android.material.textfield.TextInputEditText;

public class LockerFilterActivity extends AppCompatActivity {
    private Menu menu;
    private AutoCompleteTextView lockerNumber;
    private TextInputEditText lockerZip, lockerStreet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locker_filter);
        lockerNumber = (AutoCompleteTextView) findViewById(R.id.lockerNumber);
        lockerStreet = (TextInputEditText) findViewById(R.id.lockerStreet);
        lockerZip = (TextInputEditText) findViewById(R.id.lockerZip);

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


        lockerStreet.addTextChangedListener(new TextWatcher() {
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

        lockerZip.addTextChangedListener(new TextWatcher() {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_next, menu);
        this.menu = menu;
        setupMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_next) {
            Intent i = new Intent(LockerFilterActivity.this, LockersListActivity.class);
            if(!lockerNumber.getText().toString().equals(""))
                i.putExtra("lockerNumber", lockerNumber.getText().toString());
            if(!lockerStreet.getText().toString().equals(""))
                i.putExtra("lockerStreet", lockerStreet.getText().toString());
            if(!lockerZip.getText().toString().equals(""))
                i.putExtra("lockerZip", lockerZip.getText().toString());
            startActivity(i);

        }
        return super.onOptionsItemSelected(item);
    }

    private void setupMenu(){
        if (menu != null) {
            if (!lockerNumber.getText().toString().equals("") || !lockerStreet.getText().toString().equals("") || !lockerZip.getText().toString().equals(""))
                menu.getItem(0).setEnabled(true);
            else
                menu.getItem(0).setEnabled(false);
        }
    }
}
