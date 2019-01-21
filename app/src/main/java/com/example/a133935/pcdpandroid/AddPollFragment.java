package com.example.a133935.pcdpandroid;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.a133935.pcdpandroid.app.AppConfig;
import com.example.a133935.pcdpandroid.app.AppController;
import com.example.a133935.pcdpandroid.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddPollFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddPollFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPollFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static String TAG = AddPollFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int fields;
    private LinearLayout linearLayout,linearLayout1,linearLayout2,linearLayout3,linearLayoutinvisible;
    private ScrollView scrollView;
    private EditText pollname,numberoffields;
    private Button nextbutton1,nextbutton2,submitbutton;
    private SQLiteHandler db;

    private OnFragmentInteractionListener mListener;

    public AddPollFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddPollFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddPollFragment newInstance(String param1, String param2) {
        AddPollFragment fragment = new AddPollFragment();
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
        final View v = inflater.inflate(R.layout.fragment_add_poll, container, false);

        linearLayout = (LinearLayout) v.findViewById(R.id.linearLayout);
        linearLayout1 = (LinearLayout) v.findViewById(R.id.linearLayout1);
        linearLayout2 = (LinearLayout) v.findViewById(R.id.linearLayout2);
        linearLayout3 = (LinearLayout) v.findViewById(R.id.linearLayout3);

        db = new SQLiteHandler(getContext());
        linearLayoutinvisible = (LinearLayout) v.findViewById(R.id.invisiblelinearlayout);
        scrollView =(ScrollView) v.findViewById(R.id.scrollView);
        nextbutton1 = (Button) v.findViewById(R.id.nextbutton1);
        nextbutton2 = (Button) v.findViewById(R.id.nextbutton2);
        pollname = (EditText) v.findViewById(R.id.pollname);
        numberoffields = (EditText) v.findViewById(R.id.pollfields);
        final ArrayList<EditText> arrayList = new ArrayList<EditText>();
        final HashMap<String,String> user = db.getUserDetails();

        nextbutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View focusview = null;
                Boolean cancel=false;

                if(pollname.getText().toString().trim().isEmpty()){

                    pollname.setError("Poll name cannot be empty");
                    focusview=pollname;
                    cancel=true;
                }

                else if(numberoffields.getText().toString().trim().isEmpty()||(Integer.parseInt(numberoffields.getText().toString().trim())<2)){
                    numberoffields.setError("Require atleast 2 fields");
                    focusview=numberoffields;
                    cancel=true;
                }
                if(cancel){

                    focusview.requestFocus();
                    cancel=false;

                }
                else{

                    linearLayout1.setVisibility(View.GONE);
                    fields = Integer.parseInt(numberoffields.getText().toString());

                    linearLayout2.removeAllViews();



                    for(int i=0;i<fields;i++){

                        EditText editText = new EditText(getContext());
                        editText.setId(i+1);
                        editText.setHint("Enter field " +(i+1));
                        editText.setSingleLine(true);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0,10,0,0);
                        editText.setLayoutParams(params);
                        linearLayout2.addView(editText);
                        arrayList.add(editText);



                    }


                    linearLayout2.setVisibility(View.VISIBLE);
                    linearLayout3.setVisibility(View.VISIBLE);
                    linearLayoutinvisible.setVisibility(View.INVISIBLE);


                }


            }
        });

        nextbutton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG,"inside next button 2 click listener");

                View focusview = null;
                Boolean cancel=false;
                String createst;
                String title;



                for(int i =0;i<fields;i++){

                    if(arrayList.get(i).getText().toString().trim().isEmpty()){

                        cancel= true;
                        arrayList.get(i).setError("the field cannot be blank");
                        focusview=arrayList.get(i);

                    }
                    if (cancel){
                        focusview.requestFocus();

                    }
                }
                Log.d(TAG,""+cancel);
                if(!cancel){

                    title=pollname.getText().toString().trim().replace(" ","").concat(user.get("emp_id").toString().trim());
                    createst="Create table "+title+"(\n";
                    createst+="id int identity(1,1) primary key,\n";
                    createst+="emp_id varchar(20) unique not null,\n";
                    for(int i=0;i<arrayList.size();){

                        createst= createst+ arrayList.get(i).getText().toString().trim() + " INT";

                        i++;
                        if(i==arrayList.size()){
                            createst+= ",\ncreated_at datetime default getdate())";
                            Log.d(TAG,"final create table string is\n"+createst);
                        }
                        else{
                            createst= createst+ ",\n";
                        }


                    }

                    addPoll(title,createst,user.get("emp_id"),pollname.getText().toString().trim());



                }

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

    public void addPoll(final String title, final String createTableString, final String emp_id, final String poll_name ){

        String tag_string_req = "adding_poll";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_CREATE_POLL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "AddPoll Response: " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.get("error").toString().trim().toLowerCase().equals("false")){

                        Snackbar.make(linearLayout,"Poll Created Successfully",3000).show();
                        linearLayout1.setVisibility(View.VISIBLE);
                        pollname.setText("");
                        numberoffields.setText("");
                        linearLayout2.removeAllViews();
                        linearLayout2.setVisibility(View.GONE);
                        linearLayout3.setVisibility(View.GONE);
                        linearLayoutinvisible.setVisibility(View.GONE);
                        pollname.requestFocus();


                    }else {
                        Snackbar.make(linearLayout,"An Error Occured. Please Try Again Later",3000).show();
                        linearLayout1.setVisibility(View.VISIBLE);
                        pollname.setText("");
                        numberoffields.setText("");
                        linearLayout2.removeAllViews();
                        linearLayout2.setVisibility(View.GONE);
                        linearLayout3.setVisibility(View.GONE);
                        linearLayoutinvisible.setVisibility(View.GONE);
                        pollname.requestFocus();
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                    Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }){

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("title",title);
                params.put("createst",createTableString);
                params.put("emp_id", emp_id);
                params.put("pollname", poll_name);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);


    }



}
