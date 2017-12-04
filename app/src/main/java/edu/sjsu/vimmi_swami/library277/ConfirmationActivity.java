package edu.sjsu.vimmi_swami.library277;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by t0u000c on 12/2/17.
 */

public class ConfirmationActivity extends Activity {


    public static final String EXTRA_CONFIRM_EMAIL =
            "confirm_email";

    private InputUtilities inputUtilities;

    private TextInputLayout confirmEmailTextInputLayout;
    private TextInputEditText confirmEmailTextInputEditText;

    private TextInputLayout tokenTextInputLayout;
    private TextInputEditText tokenTextInputEditText;

    private AppCompatButton confirmBtn;

    private AppCompatTextView textViewLinkResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);


        inputUtilities = new InputUtilities(this);

        confirmEmailTextInputLayout = (TextInputLayout) findViewById(R.id.textInputLayoutConfirmEmail);
        confirmEmailTextInputEditText = (TextInputEditText) findViewById(R.id.textInputEditTextConfirmEmail);
        if(getIntent().getStringExtra(EXTRA_CONFIRM_EMAIL)!=null){
            confirmEmailTextInputEditText.setText(getIntent().getStringExtra(EXTRA_CONFIRM_EMAIL));
        }
        tokenTextInputLayout = (TextInputLayout) findViewById(R.id.textInputLayoutToken);
        tokenTextInputEditText = (TextInputEditText) findViewById(R.id.textInputEditTextToken);

        confirmBtn = (AppCompatButton) findViewById(R.id.appCompatButtonTokenConfirm);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(inputUtilities.isTxtBoxEmpty(confirmEmailTextInputEditText,confirmEmailTextInputLayout,"Email cannot be empty")){
                    return;
                }
                if(!inputUtilities.isEmail(confirmEmailTextInputEditText,confirmEmailTextInputLayout,"Email is not the right format")){
                    return;
                }
                if(inputUtilities.isTxtBoxEmpty(tokenTextInputEditText,tokenTextInputLayout,"Token cannot be empty")){
                    return;
                }
                //send a request to confirm account
                JSONObject payload = new JSONObject();
                try {
                    payload.put("email" , confirmEmailTextInputEditText.getText().toString());
                    payload.put("token", tokenTextInputEditText.getText().toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                        API.confirmURL(), payload,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject object = new JSONObject(response.toString());
                                    boolean success = object.getBoolean(("success"));
                                    if(success){
                                        //Send to confirmation page
                                        Toast.makeText(getApplicationContext(), "Account is confirmed successfully.", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(getApplicationContext(),object.getString("msg"), Toast.LENGTH_LONG).show();
                                    }

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
                                        Toast.makeText(ConfirmationActivity.this, jsonObj.getString("msg"), Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    Toast.makeText(ConfirmationActivity.this, "There is an error. Please contact admin for more info", Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                AppSingleton.get(getApplicationContext()).addRequest(jsonObjectRequest, "Account Confirmation");

            }
        });


        textViewLinkResendToken = (AppCompatTextView) findViewById(R.id.textViewLinkResendToken);
        textViewLinkResendToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //checking to see if email and token is not empty
                if(inputUtilities.isTxtBoxEmpty(confirmEmailTextInputEditText,confirmEmailTextInputLayout,"Email cannot be empty")){
                    return;
                }

                if(!inputUtilities.isEmail(confirmEmailTextInputEditText,confirmEmailTextInputLayout,"Email is not right format")){
                    return;
                }


                //send a request to confirm account
                JSONObject payload = new JSONObject();
                try {
                    payload.put("email" , confirmEmailTextInputEditText.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                        API.resendTokenURL(), payload,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject object = new JSONObject(response.toString());
                                    Toast.makeText(getApplicationContext(),object.getString("msg"), Toast.LENGTH_LONG).show();

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
                                        Toast.makeText(ConfirmationActivity.this, jsonObj.getString("msg"), Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    Toast.makeText(ConfirmationActivity.this, "There is an error. Please contact admin for more info", Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                AppSingleton.get(getApplicationContext()).addRequest(jsonObjectRequest, "Resend Token");
            }
        });
    }
}