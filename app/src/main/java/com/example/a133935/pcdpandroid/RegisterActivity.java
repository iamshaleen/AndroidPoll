package com.example.a133935.pcdpandroid;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.a133935.pcdpandroid.app.AppConfig;
import com.example.a133935.pcdpandroid.app.AppController;
import com.example.a133935.pcdpandroid.helper.SQLiteHandler;
import com.example.a133935.pcdpandroid.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {

    private Button registerButton, loginButtonLink;
    private EditText empName, empId, empEmail, empPassword;
    private LinearLayout linearLayout;
    private SessionManager session;
    private SQLiteHandler db;
    private static final String TAG = RegisterActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerButton = (Button) findViewById(R.id.btnRegister);
        loginButtonLink = (Button) findViewById(R.id.btnLinkToLoginScreen);
        empName = (EditText) findViewById(R.id.name);
        empId = (EditText) findViewById(R.id.emp_id);
        empPassword = (EditText) findViewById(R.id.password);
        empEmail = (EditText) findViewById(R.id.email);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());



        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            if(empName.getText().toString().trim().isEmpty()){

                Snackbar.make(linearLayout,"Error",1500).show();

            }
            else if (empId.getText().toString().trim().isEmpty()){

                Snackbar.make(linearLayout,"Error",1500).show();

            }
            else if(empEmail.getText().toString().trim().isEmpty()){

                Snackbar.make(linearLayout,"Error",1500).show();

            }
            else  if(empPassword.getText().toString().trim().isEmpty()){

                Snackbar.make(linearLayout,"Error",1500).show();

            }
            else{

                Snackbar.make(linearLayout,"Thanks",1500).show();
                RegisterUser(empName.getText().toString().trim(),empId.getText().toString().trim(),empEmail.getText().toString().trim(),empPassword.getText().toString().trim());

            }

            }

        });

        loginButtonLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

    private void RegisterUser(final String fullname, final String emp_id, final String email, final String password){

        String tag_string_req = "req_register";

        //pDialog.setMessage("Registering ...");
        //showDialog();
        //db.deleteUsers();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                //hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MSSQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String fullname = user.getString("fullname");
                        String email = user.getString("email");
                        String emp_id = user.getString("emp_id");
                        String created_at = user
                                .getString("created_at");

                        // Inserting row in users table
                        db.addUser(fullname, emp_id, uid, created_at);

                        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                RegisterActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                //hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("fullname", fullname);
                params.put("email", email);
                params.put("emp_id", emp_id);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }


}
