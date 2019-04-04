package com.dotcode.duoline.axdlockers.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.dotcode.duoline.axdlockers.Models.RetroToken;
import com.dotcode.duoline.axdlockers.Models.RetroTokenList;
import com.dotcode.duoline.axdlockers.Network.GetDataService;
import com.dotcode.duoline.axdlockers.Network.RetrofitClientInstance;
import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.Helper;
import com.dotcode.duoline.axdlockers.Utils.SaveSharedPreferences;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private AutoCompleteTextView mUserEmail;
    private TextInputEditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextInputLayout textInputLayoutEmail, textInputLayoutPassword;
    private CardView bLogIn;
    private TextView mPasswordreset;
    GetDataService service;
   // private String resetEmail;

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
     //   showProgress(true);
        mUserEmail.setText(SaveSharedPreferences.getEmail(getApplicationContext()));

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
//                LayoutInflater li = LayoutInflater.from(getApplicationContext());
//                View promptView = li.inflate(R.layout.view_email_input, null);
//                alertDialogInputEmail(LoginActivity.this, "Resetare Parola", getString(R.string.message_reset_password), promptView);
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
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    try {
                        SaveSharedPreferences.logOutUser(getApplicationContext());
                        String url = response.raw().request().url().toString();

                        String responseBody = "";
                        if (response.errorBody() != null) {
                            responseBody= response.errorBody().string();
                        }
                        Toast.makeText(LoginActivity.this, response.code() + " " + responseBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RetroTokenList> call, Throwable t) {
              showProgress(false);

                Toast.makeText(LoginActivity.this, "Something went wrong...Internet appear to be offline!", Toast.LENGTH_SHORT).show();
            }
        });
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
}
