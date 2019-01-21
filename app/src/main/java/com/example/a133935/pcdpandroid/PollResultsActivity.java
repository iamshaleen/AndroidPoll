package com.example.a133935.pcdpandroid;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.a133935.pcdpandroid.app.AppConfig;
import com.example.a133935.pcdpandroid.app.AppController;
import com.example.a133935.pcdpandroid.helper.SQLiteHandler;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PollResultsActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Button button;
    private ProgressDialog pd;
    private SQLiteHandler db;
    private static int TIME_OUT_ACT = 3000;
    private BarChart barChart;
    private PieChart pieChart;
    private CoordinatorLayout clayout;
    private String TAG = PollResultsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String pollname = getIntent().getExtras().getString("pollname");
        setContentView(R.layout.activity_poll_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        clayout = (CoordinatorLayout) findViewById(R.id.clayout);

        button = (Button) findViewById(R.id.savebutton);
        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Loading Results...");
        pd.setCancelable(false);
        pd.show();
        barChart = (BarChart) findViewById(R.id.chart1);
        pieChart = (PieChart) findViewById(R.id.chart);
        pieChart.setCenterText("Votes Per Option");
        pieChart.setCenterTextSize(10);

        pieChart.invalidate();
        barChart.invalidate();
        final ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        final ArrayList<String> labels = new ArrayList<String>();
        final ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();


        db = new SQLiteHandler(getApplicationContext());
        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
        String emp_id = user.get("emp_id");
        final String title = pollname.replace(" ","")+emp_id;

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(pollname);

        getPollResults(title,entries,labels,entries1);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                pd.setMessage("Saving Images");
                pd.setCanceledOnTouchOutside(false);
                pd.show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        pieChart.saveToGallery(pollname+".jpg", 100);
                        barChart.saveToGallery(pollname+"1.jpg",100);
                        pd.dismiss();
                        Snackbar.make(view, "Images Saved To Gallery", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }, TIME_OUT_ACT);

            }
        });


    }

    public void getPollResults(final String title,final ArrayList<PieEntry> entries, final ArrayList<String> labels, final ArrayList<BarEntry> entries1){


        String tag_string_req = "req_checking";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GETPOLLRESULTS, new Response.Listener<String>()
        {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Checking Response: " + response.toString());

                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject = (JSONObject) jObj.get("vals");
                        JSONArray vals= jsonObject.getJSONArray("votes");
                        JSONArray columns= jsonObject.getJSONArray("columns");

                        Log.d(TAG, jsonObject.toString().trim());

                        int z=0;
                        for(int i=0;i<vals.length();i++){

                            labels.add(columns.getString(i));
                            String s= ""+Integer.parseInt(vals.getString(i));
                            Log.d(TAG,"val ki value integer "+s);
                            float y= (float)Integer.parseInt(vals.getString(i));
                            entries.add(new PieEntry(y, columns.getString(i)));
                            entries1.add(new BarEntry(z, y));
                            z++;

                        }

                        for(int i=0;i<entries.size();i++){
                            Log.d("Entries ki value",entries.toString());
                            Log.d("Entries1 ki value",entries1.toString());
                            Log.d("labels ki values", labels.toString());
                        }

                        PieDataSet dataset = new PieDataSet(entries, "");
                        dataset.setSliceSpace(0);
                        dataset.setColors(new int[] {R.color.colorPrimary, R.color.yellow, R.color.purple,R.color.green,R.color.bg_main,R.color.message,R.color.pink,R.color.skyblue,R.color.lightgreen,R.color.peach,R.color.patanahi,R.color.verylight}, getApplication());
                        BarDataSet barDataSet = new BarDataSet(entries1," ");
                        barDataSet.setColors(new int[] {R.color.colorPrimary, R.color.yellow, R.color.purple,R.color.green,R.color.bg_main,R.color.message,R.color.pink,R.color.skyblue,R.color.lightgreen,R.color.peach,R.color.patanahi,R.color.verylight}, getApplication());
                        PieData data = new PieData(dataset);
                        data.setValueTextSize(10);
                        pieChart.setData(data);
                        BarData data1 = new BarData(barDataSet);
                        data1.setValueTextSize(10);
                        barChart.setData(data1);
                        pd.dismiss();
                        pieChart.notifyDataSetChanged();
                        barChart.notifyDataSetChanged();
                        pieChart.animateY(2000);
                        barChart.animateY(2000);
                        Description description = new Description();
                        description.setText(" ");
                        pieChart.setDescription(description);
                        barChart.setDescription(description);

                    } else {
                        pd.dismiss();
                        String errorMsg = jObj.getString("error_msg");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Checking Error: " + error.getMessage());
                pd.dismiss();
                if(true){
                    Snackbar.make(clayout, "No Internet Access!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else{
                    Snackbar.make(clayout, "Unknown Error Occured. Please Try Again!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("pollname", title);
                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }
}
