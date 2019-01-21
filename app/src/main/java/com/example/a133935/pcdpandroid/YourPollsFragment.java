package com.example.a133935.pcdpandroid;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

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
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link YourPollsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link YourPollsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YourPollsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String TAG= YourPollsFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView.LayoutManager layoutManager;
    private SQLiteHandler sqLiteHandler;
    private LinearLayout linearLayout;
    private String[] strarry ;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView l1;

    private OnFragmentInteractionListener mListener;

    public YourPollsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment YourPollsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YourPollsFragment newInstance(String param1, String param2) {
        YourPollsFragment fragment = new YourPollsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_your_polls, container, false);
        layoutManager = new LinearLayoutManager(getContext());
        sqLiteHandler = new SQLiteHandler(getContext());
        linearLayout = (LinearLayout) v.findViewById(R.id.linearLayout);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.message, R.color.green, R.color.blue, R.color.yellow);
        l1 = (ListView) v.findViewById(R.id.l1);
        final HashMap<String,String> user = sqLiteHandler.getUserDetails();
        refreshPolls(user.get("emp_id"));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPolls(user.get("emp_id"));
            }
        });

        l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String s = strarry[i];

                Intent intent = new Intent(getActivity(),ViewPollActivity.class).putExtra("pollname",s);
                startActivity(intent);



            }
        });


        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void refreshPolls(final String emp_id){

        String tag_string_req = "getting_user_polls";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GETPOLLS, new Response.Listener<String>() {
            List<csData> ldata=new ArrayList<>();
            @Override
            public void onResponse(String response) {

                Log.d(TAG,response);
                try {
                    csData csData = null;
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("vals");
                    String[] arr = new String[jsonArray.length()+1];
                    strarry = new String[jsonArray.length()+1];
                    for(int i=0;i<jsonArray.length()+1;i++){
                        csData=new csData();
                        if(i==jsonArray.length()){
                            arr[i]=" ";
                            strarry[i] = " ";

                        }
                        else{
                            arr[i]=jsonArray.getString(jsonArray.length()-i-1);
                            strarry[i] = jsonArray.getString(jsonArray.length()-i-1);
                        }

                        csData.setName(arr[i]);
                        ldata.add(csData);
                        Log.d("Poll of User"+i+1,arr[i] );
                    }
                    adapter adp=new adapter(R.layout.poll_card,getContext(),ldata);
                    l1.setAdapter(adp);
                    swipeRefreshLayout.setRefreshing(false);

                } catch (JSONException e) {
                    e.printStackTrace();
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG,"Your Polls Error: "+error.getMessage().toString().trim());
                Snackbar.make(linearLayout,"Unknown Error Occured",2500).show();

            }
        }){
            @Override
            protected Map<String,String> getParams(){

                Map<String, String> params = new HashMap<String, String>();
                params.put("emp_id", emp_id);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);

    }

}
