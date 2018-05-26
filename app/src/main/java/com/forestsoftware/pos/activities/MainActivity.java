package com.forestsoftware.pos.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.forestsoftware.pos.R;
import com.forestsoftware.pos.model.User;
import com.forestsoftware.pos.model.UserResponse;
import com.forestsoftware.pos.rest.ApiClient;
import com.forestsoftware.pos.rest.ApiInterface;
import com.forestsoftware.pos.util.SessionManager;
import com.wang.avi.AVLoadingIndicatorView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{
    private Toolbar toolbar;
    private EditText username, password;
    private AVLoadingIndicatorView avLoadingIndicatorView;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText) findViewById(R.id.user_name);
        password = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(this);
        init();

    }

    public void init() {
        avLoadingIndicatorView = new AVLoadingIndicatorView(MainActivity.this);
        avLoadingIndicatorView = (com.wang.avi.AVLoadingIndicatorView) findViewById(R.id.loadingAnimation);
        avLoadingIndicatorView.setIndicatorColor(R.color.button_color);
        username = (EditText) findViewById(R.id.user_name);
        password = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.login);


    }



    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.login:

                final String user = username.getText().toString();
                final String pass = password.getText().toString();
                if (!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {


//                    Toast.makeText(MainActivity.this,"Clicked",Toast.LENGTH_SHORT).show();
                    loginButton.setClickable(false);
                    avLoadingIndicatorView.setVisibility(View.VISIBLE);
                    doLogin(user, pass);


                } else {
                    loginButton.setClickable(true);
                    Snackbar snackbar = Snackbar.make(v, "Username and Password can not be empty !", Snackbar.LENGTH_LONG).setAction("Action", null);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.button_color));
                    snackbar.show();
                }
                break;
        }
    }


    public void doLogin(String username, String password) {

        ApiInterface service = ApiClient.getClient().create(ApiInterface.class);
        Call<UserResponse> userCall = service.userLogIn(new User(username, password));
        userCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                //  hidepDialog();
                //onSignupSuccess();
                Log.d("onResponse", "" + response);


                if (response.isSuccessful()) {
//                    avLoadingIndicatorView.setVisibility(View.INVISIBLE);
                    UserResponse userResponse = response.body();
                    String token = userResponse.getToken();
                    SessionManager.setTOKEN(token);


                    JWT parsedJWT = new JWT(token);
                    Claim subscriptionMetaData = parsedJWT.getClaim("vendorId");
                    String vendorId = subscriptionMetaData.asString();

                    Intent i = new Intent(MainActivity.this, SceneTwo.class);
                    i.putExtra("VENDOR_ID", vendorId);
                    startActivity(i);
                    finish();

                    Toast.makeText(MainActivity.this, "Vendor id: " + vendorId, Toast.LENGTH_SHORT).show();

                    Log.wtf("Get Default Message: ", "" + response.code() + " And the vendorId is: " + vendorId);

                } else {
                    if (response.code() == 401) {

                        Toast.makeText(MainActivity.this, "401", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(MainActivity.this, "there is another error", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                // hidepDialog();
                Log.wtf("onFailure", t.toString());
                Toast.makeText(MainActivity.this, "there is another error", Toast.LENGTH_SHORT).show();


            }
        });
    }
}
