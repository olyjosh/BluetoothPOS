package com.forestsoftware.pos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.forestsoftware.pos.R;
import com.forestsoftware.pos.model.GeneralResponse;
import com.forestsoftware.pos.model.User;
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
    Snackbar snackbar;

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
        avLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.loadingAnimation);
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
                username.setText("ma@mai.com");
                password.setText("1");

                if (!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {


                   loginButton.setClickable(false);
                    avLoadingIndicatorView.setVisibility(View.VISIBLE);
                    doLogin(user, pass);



                } else {
                    loginButton.setClickable(true);
                    snackbar = Snackbar.make(v, "Username and Password can not be empty !", Snackbar.LENGTH_LONG).setAction("Action", null);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    snackbar.show();
                }
                break;
        }
    }


    public void doLogin(String username, String password) {

        ApiInterface service = ApiClient.getClient().create(ApiInterface.class);
        Call<GeneralResponse> userCall = service.userLogIn(new User(username, password));
        userCall.enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {

                Log.d("onResponse", "" + response);


                if (response.isSuccessful()) {
                    GeneralResponse generalResponse = response.body();
                    String token = generalResponse.getToken();
                    SessionManager.setTOKEN(token);
                     if(token != null && token != "" )
                     {

                         Log.wtf("&&&&&&&&&",""+token);
                         JWT parsedJWT = new JWT(token);
                         Claim subscriptionMetaData = parsedJWT.getClaim("vendorId");
                         String vendorId = subscriptionMetaData.asString();

                         Intent i = new Intent(MainActivity.this, SceneTwo.class);
                         i.putExtra("VENDOR_ID", vendorId);
                         startActivity(i);
                         finish();

                         Toast.makeText(MainActivity.this, "Vendor id: " + vendorId, Toast.LENGTH_SHORT).show();

                         Log.wtf("Get Default Message: ", "" + response.code() + " And the vendorId is: " + vendorId);
                     }
                     else
                         {
                             avLoadingIndicatorView.setVisibility(View.INVISIBLE);
                             loginButton.setClickable(true);
                             Toast.makeText(MainActivity.this, "Invalid Credential", Toast.LENGTH_SHORT).show();
                         }



                } else {
                    if (response.code() == 401)
                    {
                        avLoadingIndicatorView.setVisibility(View.INVISIBLE);


                        Toast.makeText(MainActivity.this, "Unauthorized", Toast.LENGTH_SHORT).show();
                    } else {
                        avLoadingIndicatorView.setVisibility(View.INVISIBLE);

                        Toast.makeText(MainActivity.this, "there is another error", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                Log.wtf("onFailure", t.toString());
                Toast.makeText(MainActivity.this, "There is an error", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
