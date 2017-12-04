package edu.sjsu.vimmi_swami.library277;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by t0u000c on 12/4/17.
 */

public class VolleyUtility {
    public static void addRequest(String url, JSONObject payload, final Context context){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, payload, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {


                        } catch (Exception ex) {
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        JSONObject jsonObj = null;

                        NetworkResponse networkResponse = error.networkResponse;
                        if(networkResponse != null) {
                            try {
                                jsonObj = new JSONObject(new String(networkResponse.data));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            Toast.makeText(context.getApplicationContext(), "There is an error. Please contact admin for more info", Toast.LENGTH_LONG).show();

                        }
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        //headers.put("Content-Type", "application/json");
                        headers.put("key", "Value");
                        return headers;
                    }
        };
        AppSingleton.get(context.getApplicationContext()).addRequest(jsonObjectRequest, "City View Header Current Date");

    }
}
