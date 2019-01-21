package com.example.a133935.pcdpandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.a133935.pcdpandroid.app.AppConfig;
import com.example.a133935.pcdpandroid.app.AppController;
import com.example.a133935.pcdpandroid.helper.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PollActivity extends AppCompatActivity {

    private String TAG = PollActivity.class.getSimpleName();
    private SQLiteHandler db;
    private LinearLayout radioLinearLayout,textLinearLayout;
    private RadioGroup radioGroup;
    private Button voteButton;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView messageTextView,voteMessageText;
    private int lengthvalue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String pollname = intent.getStringExtra("pollname");
        setContentView(R.layout.activity_poll);
        collapsingToolbarLayout =  (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(pollname.replace("_","").replaceAll("[0-9]",""));
        pollname=pollname.replace("_","");
        Log.d(TAG, pollname);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioLinearLayout = (LinearLayout) findViewById(R.id.radioLinearLayout);
        textLinearLayout = (LinearLayout) findViewById(R.id.textLinearLayout);
        voteButton = (Button) findViewById(R.id.voteButton);
        messageTextView = (TextView) findViewById(R.id.messageTextView);
        voteMessageText = (TextView) findViewById(R.id.voteMessageText);
        db = new SQLiteHandler(getApplicationContext());
        String emp_id = db.getUserDetails().get("emp_id");
        checkpollstatus(pollname,emp_id);

        final String finalPollname = pollname;
        voteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int checkedid=radioGroup.getCheckedRadioButtonId();
                Log.d(TAG,"checked id "+checkedid);
                String query= "Insert into "+ finalPollname +"(emp_id";
                for(int i=0;i<lengthvalue-2;i++){

                    TextView textView = (TextView) findViewById(i+100);
                    Log.d(TAG,textView.getText().toString().trim());
                    query+=",";
                    query+=textView.getText().toString().trim();

                }
                query+=")values('";
                query+=db.getUserDetails().get("emp_id").toString().trim()+"',";
                for(int i=0;i<lengthvalue-2;i++){

                    query+=i==checkedid?"1":"0";
                    if(i!=lengthvalue-3){
                        query+=",";
                    }else{
                        query+=")";
                    }

                }
                Log.d(TAG,query);

                vote(query);
            }
        });


    }

    public void checkpollstatus(final String title, final String emp_id) {

        String tag_string_req = "checking poll status";
        Log.d(TAG,title+" "+emp_id);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,AppConfig.URL_CHECKSTATUS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d(TAG,response.toString().trim());
                    if(Integer.parseInt(jsonObject.get("status").toString().trim())==1){

                        Log.d(TAG,"poll active");

                        if(jsonObject.getBoolean("voted")){

                            //user has already voted
                            Log.d(TAG,"user has already voted");
                            voteMessageText.setText("Thanks! You have already voted...");
                            voteMessageText.setVisibility(View.VISIBLE);


                        }else{

                            //user has to vote

                            Log.d(TAG,"user has to vote");
                            voteMessageText.setText("Please select one of the below options:");
                            voteMessageText.setVisibility(View.VISIBLE);
                            JSONArray jsonArray = new JSONArray();
                            jsonArray=jsonObject.getJSONArray("vals");
                            Log.d(TAG,jsonArray.toString().trim());
                            lengthvalue=jsonArray.length()-1;
                            for(int i=2;i<jsonArray.length()-1;i++){
                                RadioButton radioButton = new RadioButton(getApplicationContext());
                                radioButton.setId(i-2);
                                radioButton.setHeight(85);
                                radioGroup.addView(radioButton);

                                TextView textView = new TextView(getApplicationContext());
                                textView.setText(jsonArray.getString(i));
                                textView.setId(i-2+100);
                                textView.setHeight(85);
                                textLinearLayout.addView(textView);
                            }
                            String querystring = "";
                            voteMessageText.setVisibility(View.VISIBLE);
                            radioLinearLayout.setVisibility(View.VISIBLE);
                            textLinearLayout.setVisibility(View.VISIBLE);
                            voteButton.setVisibility(View.VISIBLE);

                        }


                    }else{

                        //Poll closed

                        voteMessageText.setText("Sorry! The poll has already ended");
                        voteMessageText.setVisibility(View.VISIBLE);

                        Log.d(TAG,"Poll ended");

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
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("title", title);
                params.put("emp_id", emp_id);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest,tag_string_req);
    }

    public void vote(final String querystring){

        String reqst_tag = "casting user vote";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_VOTE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d(TAG,response.toString().trim());
                    if(!jsonObject.getBoolean("error")){

                        messageTextView.setText("thanks for voting!");
                        messageTextView.setVisibility(View.VISIBLE);
                        radioLinearLayout.setVisibility(View.GONE);
                        textLinearLayout.setVisibility(View.GONE);
                        voteButton.setVisibility(View.GONE);
                        voteMessageText.setVisibility(View.GONE);

                    }else{

                        messageTextView.setText("Unknown Error Occured. Please try again later!");
                        messageTextView.setVisibility(View.VISIBLE);
                        radioLinearLayout.setVisibility(View.GONE);
                        textLinearLayout.setVisibility(View.GONE);
                        voteButton.setVisibility(View.GONE);
                        voteMessageText.setVisibility(View.GONE);

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
        }){
            @Override
            protected Map<String,String> getParams(){

                Map<String, String> params = new HashMap<String, String>();
                params.put("querystring", querystring);
                return params;


            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest,reqst_tag);

    }

}
