package com.dotcode.duoline.axdlockers.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dotcode.duoline.axdlockers.Models.RetroBuildingXUser;
import com.dotcode.duoline.axdlockers.Models.RetroEmail;
import com.dotcode.duoline.axdlockers.Models.RetroRole;
import com.dotcode.duoline.axdlockers.Models.RetroToken;
import com.dotcode.duoline.axdlockers.Models.RetroTokenList;
import com.dotcode.duoline.axdlockers.Models.RetroUser;
import com.dotcode.duoline.axdlockers.Network.GetDataService;
import com.dotcode.duoline.axdlockers.Network.RetrofitClientInstance;
import com.dotcode.duoline.axdlockers.Network.SetRequests;
import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.Helper;
import com.dotcode.duoline.axdlockers.Utils.SaveSharedPreferences;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements SetRequests.GetDataResponse{
    private AutoCompleteTextView mUserEmail;
    private TextInputEditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextInputLayout textInputLayoutEmail, textInputLayoutPassword;
    private CardView bLogIn;
    private TextView mPasswordreset;
    GetDataService service;
    private String resetEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        // Set up the login form.
        mUserEmail = (AutoCompleteTextView) findViewById(R.id.email);
        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.textinputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        mPasswordView = (TextInputEditText) findViewById(R.id.retypePassword);
        bLogIn = (CardView) findViewById(R.id.bLoginCardView);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mPasswordreset = findViewById(R.id.tvResetPassword);
        mUserEmail.setText(SaveSharedPreferences.getEmail(getApplicationContext()));
        mLoginFormView.setVerticalScrollBarEnabled(false);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        bLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mPasswordreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(getApplicationContext());
                View promptView = li.inflate(R.layout.view_email_input, null);
                alertDialogInputEmail(LoginActivity.this, getString(R.string.change_password), getString(R.string.input_email), promptView);
            }
        });
    }


    private void alertDialogInputEmail(Context ctx, String title, String msg, View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setView(v);
        final TextView mResetPasswordMessage = (TextView) v.findViewById(R.id.tvPaswordResetMsg);
        final EditText email = (EditText) v.findViewById(R.id.etEmail);
        mResetPasswordMessage.setText(msg);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = builder.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetEmail = email.getText().toString();
                if (!TextUtils.isEmpty(resetEmail)){// && Patterns.EMAIL_ADDRESS.matcher(resetEmail).matches()
                    showProgress(true);
                    RetroEmail retroEmail = new RetroEmail(resetEmail);
                    attemptResetPassword(retroEmail);
                    dialog.dismiss();
                } else {
                    email.setError(getString(R.string.error_invalid_email));
                }
            }
        });
    }

    public void attemptResetPassword(RetroEmail email){
        Call<Void> call = service.resetPassword(email);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                showProgress(false);
                if (response.isSuccessful()) {
                    showAlert(LoginActivity.this, getString(R.string.change_password), getString(R.string.check_email));
                } else {
                    try {
                        String url = response.raw().request().url().toString();

                        String responseBody = "";
                        if (response.errorBody() != null) {
                            responseBody = response.errorBody().string();
                        }
                        if (response.code() != 404) {
                            Toast.makeText(LoginActivity.this, response.code() + " " + responseBody, Toast.LENGTH_LONG).show();
                        } else {
                            showAlert(LoginActivity.this, getString(R.string.error), getString(R.string.this_email_does_not_exist));
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showProgress(false);
                Toast.makeText(LoginActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void tokenRequest() {
        Call<RetroTokenList> call = service.getToken(SaveSharedPreferences.getEmail(getApplicationContext()), SaveSharedPreferences.getEncryptedPassword(getApplicationContext()));
        call.enqueue(new Callback<RetroTokenList>() {
            @Override
            public void onResponse(Call<RetroTokenList> call, Response<RetroTokenList> response) {
                showProgress(false);

                if (response.isSuccessful()) {
                    RetroToken token = response.body().getToken();
                    SaveSharedPreferences.setUserId(getApplicationContext(), token.getUserId());
                    SaveSharedPreferences.setTokenExpireAt(getApplicationContext(), token.getTokenExpiresAt());
                    SaveSharedPreferences.setIsAdmin(getApplicationContext(), token.isSuperAdmin());
                    SaveSharedPreferences.setAccesToken(getApplicationContext(), token.getAccessToken());
                    new SetRequests(getApplicationContext(), LoginActivity.this, Helper.REQUEST_CHECK_USER, null, null);
                } else {
                    try {
                        SaveSharedPreferences.logOutUser(getApplicationContext());
                        String url = response.raw().request().url().toString();

                        String responseBody = "";
                        if (response.errorBody() != null) {
                            responseBody= response.errorBody().string();
                        }
                        if (response.code() == 404) {
                            showAlert(LoginActivity.this, getString(R.string.login_error_title), getString(R.string.user_not_exist));
                        } else{
                            Toast.makeText(LoginActivity.this, response.code() + " " + responseBody, Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RetroTokenList> call, Throwable t) {
              showProgress(false);

                Toast.makeText(LoginActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResponse(int currentRequestId, Object result) {
        if (currentRequestId == Helper.REQUEST_CHECK_USER && result instanceof RetroUser) {
            RetroRole role = ((RetroUser) result).getRole();
            if(role.getHasRelatedBuildings()){
                List<RetroBuildingXUser> buildingXUsers = ((RetroUser) result).getBuildingXUsers();

                if(buildingXUsers.size() == 0){
                    showAlert(LoginActivity.this, getString(R.string.no_user_building_title), getString(R.string.no_user_building_message));
                } else {
                    startActivity(new Intent(LoginActivity.this, MainMenuActivity.class));
                }

            } else {
                startActivity(new Intent(LoginActivity.this, MainMenuActivity.class));
            }
        }
    }

    @Override
    public void onFailed(int currentRequestId, boolean mustLogOut) {

    }

    private void attemptLogin() {
        // Reset errors.
        mUserEmail.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUserEmail.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
//        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
//            mPasswordView.setError(getString(R.string.error_incorrect_password));
//            focusView = mPasswordView;
//            cancel = true;
//        }
//
//        // Check for a valid email address.
//        if (TextUtils.isEmpty(username)) {
//            mUserEmail.setError(getString(R.string.error_email_required));
//            focusView = mUserEmail;
//            cancel = true;
//        } else if (!isEmailValid(username)) {
//            mUserEmail.setError(getString(R.string.error_invalid_email));
//            focusView = mUserEmail;
//            cancel = true;
//        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            String pass = Helper.getEncyptedPassword(mPasswordView.getText().toString());
            SaveSharedPreferences.setEncryptedPassword(getApplicationContext(), pass);
            SaveSharedPreferences.setEmail(getApplicationContext(), mUserEmail.getText().toString());
            InputMethodManager imm = (InputMethodManager) getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            tokenRequest();
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 3;
    }

    private boolean isEmailValid(String email) {
        //return true;
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
    /**
     * show or not progress bar to indicate loading activity
     * @param show
     */
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

           // mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

           // mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void showAlert(Context ctx, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        builder.show();
    }
}
