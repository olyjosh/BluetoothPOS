package com.forestsoftware.pos.activities;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {
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
        loginButton = (Button)findViewById(R.id.login);



//        toolbar = (Toolbar)findViewById(R.id.toolbar);
//
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
      //  init();
        final String user = username.getText().toString();
        final String pass = password.getText().toString();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
        //        avLoadingIndicatorView.setVisibility(View.VISIBLE);
                loginButton.setEnabled(false);
                doLogin(username.getText().toString(), password.getText().toString());

            }
        });
    }

    public void init() {
        avLoadingIndicatorView = new AVLoadingIndicatorView(MainActivity.this);
        username = (EditText) findViewById(R.id.user_name);
        password = (EditText) findViewById(R.id.password);
        loginButton = (Button)findViewById(R.id.login);


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
                // Userresponse userresponse = response;


                if (response.isSuccessful())
                {
//                    avLoadingIndicatorView.setVisibility(View.INVISIBLE);
                    UserResponse userResponse = response.body();
                    String token = userResponse.getToken();
                    SessionManager.setTOKEN(token);


                    JWT parsedJWT = new JWT(token);
                    Claim subscriptionMetaData = parsedJWT.getClaim("vendorId");
                    String vendorId = subscriptionMetaData.asString();

                    Intent i = new Intent(MainActivity.this, SceneTwo.class);
                    i.putExtra("VENDOR_ID",vendorId);
                    startActivity(i);

                    Toast.makeText(MainActivity.this, "Vendor id: "+vendorId, Toast.LENGTH_SHORT).show();

                    Log.wtf("Get Default Message: ", "" + response.code() + " And the vendorId is: "+vendorId);

                }
                else {
                    if (response.code() == 401)
                    {

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
