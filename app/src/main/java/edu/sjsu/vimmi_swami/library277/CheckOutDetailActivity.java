package edu.sjsu.vimmi_swami.library277;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.text.DateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class CheckOutDetailActivity extends Activity {
    private ListView listview;
    private EditText editText;
    private Button checkout;
    private SessionManagement session;
    private CheckBox checkBox;
    private Button submit;
    //private static CustomAdapter adapter;
    static ArrayList<BookModel>dataModels;
    private static CustomAdapter adapter;
    private boolean checked = false;
    ArrayList<BookModel> chosenDataModels;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out_detail);
        submit = (Button) findViewById(R.id.submit);
        session = new SessionManagement(getApplicationContext());
        session.loginValidation();
        checkBox = (CheckBox) findViewById(R.id.checkbox);
        //JSONArray bookList = reponse.getJSONArray("books");
        listview = (ListView) findViewById(R.id.list1);
        //textView1 = (TextView) findViewById(R.id.textview1);
        chosenDataModels = new ArrayList<BookModel>();
        for (int i = 0; i < PatronActivity.select.size(); i++) {
            int position = PatronActivity.select.get(i);
            chosenDataModels.add(PatronActivity.dataModels.get(position));
        }
        adapter = new CustomAdapter(chosenDataModels, getApplicationContext());
        listview.setAdapter(adapter);
//        listview.setAdapter(adapter);
//        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
//        textView1.setText("Date borrow: " + currentDateTimeString);
        RequestQueue queue;
        queue = Volley.newRequestQueue(getApplicationContext());
//        adapter= new CustomAdapter(dataModels,getApplicationContext());
//        String url = API.GetBooks + "enteredBy=" + session.getSessionDetails().get(SessionManagement.KEY_USER_ID);
//        Log.d("URL", url);
//        volleyJsonArrayRequest(url);
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    checked = true;

                } else {
                    checked = false;
                }

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }

        });
    }


    public void volleyJsonArrayRequest(String url){
        //dataModels.clear();
        adapter.notifyDataSetChanged();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("Response", response.toString());
                    JSONArray bookList = response.getJSONArray("books");
                    try {
                        for(int i = 0; i < bookList.length(); i++) {
                            JSONObject objects = bookList.getJSONObject(i);
                            dataModels.add(new BookModel(objects.getString("_id"),
                                    objects.getString("author"),
                                    objects.getString("title"),
                                    objects.getString("callNumber"),
                                    objects.getString("publisher"),
                                    objects.getString("year"),
                                    objects.getString("location"),
                                    objects.getString("copies"),
                                    objects.getString("status"),
                                    objects.getString("keywords"),
                                    objects.getString("image")));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception ex) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                JSONObject jsonObj = null;

                NetworkResponse networkResponse = error.networkResponse;
                if(networkResponse != null) {
                    try {
                        jsonObj = new JSONObject(new String(networkResponse.data));
                        Toast.makeText(getApplicationContext(),jsonObj.getString("msg"),Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "There is an error. Please contact admin for more info", Toast.LENGTH_LONG).show();
                    Log.e("Error",error.getMessage());
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                String token = session.getSessionDetails().get(SessionManagement.KEY_TOKEN);
                headers.put("x-access-token", session.getSessionDetails().get(SessionManagement.KEY_TOKEN));
                headers.put("Content-Type","application/json");
                return headers;
            }
        };
        AppSingleton.get(getApplicationContext()).addRequest(jsonObjectRequest, "Get Books");

    }
}
