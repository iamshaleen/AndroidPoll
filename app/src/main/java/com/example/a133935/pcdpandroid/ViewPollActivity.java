package com.example.a133935.pcdpandroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.a133935.pcdpandroid.app.AppConfig;
import com.example.a133935.pcdpandroid.app.AppController;
import com.example.a133935.pcdpandroid.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ViewPollActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView totalvalue;
    private TextView statusvalue;
    private TextView link;
    private Button endpoll;
    private Button result;
    private LinearLayout linearLayout;
    private SQLiteHandler db;
    private CoordinatorLayout clayout;
    private ProgressDialog pd;
    private static int TIME_OUT_ACT = 3000;
    private String TAG = ViewPollActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String pollname = getIntent().getExtras().getString("pollname");
        setContentView(R.layout.activity_view_poll);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        totalvalue = (TextView) findViewById(R.id.totalvalue);
        statusvalue = (TextView) findViewById(R.id.statusvalue);
        link = (TextView) findViewById(R.id.link);
        link.setVisibility(View.VISIBLE);
        endpoll = (Button) findViewById(R.id.endpoll);
        result = (Button) findViewById(R.id.result);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(pollname);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        linearLayout.setVisibility(View.GONE);
        clayout = (CoordinatorLayout) findViewById(R.id.clayout);
        setSupportActionBar(toolbar);
        db = new SQLiteHandler(getApplicationContext());
        pd = new ProgressDialog(this);
        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
        final String name = user.get("name");
        String emp_id = user.get("emp_id");
        getPollDetails(pollname);


        endpoll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                    //Log.d(TAG,"inside endpoll onclick");
                    pd.setMessage("Ending Poll");
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();
                    link.setVisibility(View.GONE);
                    //share.setVisibility(View.GONE);
                    enduserpoll(pollname);

            }
        });

        result.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                pd.setMessage("Please Wait..");
                pd.setCanceledOnTouchOutside(false);
                pd.show();
                Intent intent = new Intent(ViewPollActivity.this,PollResultsActivity.class).putExtra("pollname",pollname);
                startActivity(intent);
                pd.dismiss();


            }
        });



    }

    public void getPollDetails(final String pollname) {


        pd.setMessage("Please Wait");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                linearLayout.setVisibility(View.VISIBLE);
                pd.dismiss();

            }
        }, TIME_OUT_ACT);

        String tag_string_req = "req_checking";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GETPOLLDETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG,response.toString().trim());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    totalvalue.setText(""+Integer.parseInt(jsonObject.get("count").toString().trim()));
                    statusvalue.setText(Integer.parseInt(jsonObject.get("status").toString().trim())==1?"Active":"Ended");
                    link.setText("https://iamshaleen.github.io/pollname/"+pollname.replace(" ","_")+db.getUserDetails().get("emp_id"));
                    if(Integer.parseInt(jsonObject.get("status").toString().trim())==1){
                        endpoll.setVisibility(View.VISIBLE);
                    }else{
                        result.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG,error.getMessage().toString().trim());

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("pollname", pollname);
                return params;

            }


        };
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);
    }


    public void enduserpoll(final String pollname){

        //Log.d(TAG,"inside end user poll");

        String tag_string_req = "req_checking";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_ENDPOLL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Log.d(TAG,"inside onResponse");
                Log.d(TAG,response.toString().trim());
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    statusvalue.setText(Integer.parseInt(jsonObject.get("status").toString().trim())==1?"Active":"Ended");
                    endpoll.setVisibility(View.GONE);
                    result.setVisibility(View.VISIBLE);
                    pd.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Log.d(TAG,"inside onErrorResponse");
                Log.e(TAG,error.getMessage().toString().trim());
                pd.dismiss();

            }
        }){

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("pollname", pollname);
                return params;

            }

        };
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
}
