package edu.sjsu.vimmi_swami.library277;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
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

public class LoginActivity extends Activity {

    private TextInputLayout txtInputLayoutEmail;
    private TextInputEditText txtInputEmail;

    private TextInputLayout txtInputLayoutPassword;
    private TextInputEditText txtInputPassword;

    private AppCompatButton btnLogin;

    private AppCompatTextView register;

    private InputUtilities inputUtils;

    private SessionManagement session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionManagement(getApplicationContext());

        inputUtils = new InputUtilities(this);

        txtInputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        txtInputEmail = (TextInputEditText) findViewById(R.id.textInputEditTextEmail);

        txtInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        txtInputPassword = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);

        btnLogin = (AppCompatButton) findViewById(R.id.appCompatButtonLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //verify email and password
                if(inputUtils.isTxtBoxEmpty(txtInputEmail,txtInputLayoutEmail,"Email cannot be empty") ||
                        inputUtils.isTxtBoxEmpty(txtInputPassword,txtInputLayoutPassword, "Password cannot be empty")){
                    return;
                }else if(!inputUtils.isEmail(txtInputEmail,txtInputLayoutEmail,"Email is not the right format")){
                    return;
                }else{
                    loginUser(txtInputEmail.getText().toString(), txtInputPassword.getText().toString());
                }

            }
        });

        register = (AppCompatTextView) findViewById(R.id.textViewLinkRegister);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to RegisterActivity
                Intent intentRegister = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intentRegister);
            }
        });
    }

    private void loginUser( final String email, final String password) {
        JSONObject payload = new JSONObject();
        try {
            payload.put("email", email);
            payload.put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                API.logInURL(), payload,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object = new JSONObject(response.toString());
                            boolean success = object.getBoolean(("success"));
                            if(success){
                                String email = object.getString("email");
                                String token = object.getString("token");
                                String userID = object.getString("userID");
                                session.saveLoginSession(email, token,userID);
                                Intent i = new Intent(getApplicationContext(), PatronActivity.class);
                                startActivity(i);
                                finish();
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
                                Toast.makeText(LoginActivity.this, jsonObj.getString("msg"), Toast.LENGTH_LONG).show();
                                if(jsonObj.getString("type").equals("not-verified")){
                                    Intent intentConfirmation = new Intent(getApplicationContext(), ConfirmationActivity.class);
                                    intentConfirmation.putExtra(ConfirmationActivity.EXTRA_CONFIRM_EMAIL , txtInputEmail.getText().toString());
                                    startActivity(intentConfirmation);
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            Toast.makeText(LoginActivity.this, "There is an error. Please contact admin for more info", Toast.LENGTH_LONG).show();

                        }
                    }
                });
        AppSingleton.get(getApplicationContext()).addRequest(jsonObjectRequest, "City View Header Current Date");

        // Tag used to cancel the request
        //String cancel_req_tag = "login";
       // progressDialog.setMessage("Logging you in...");
        //showDialog();
//        StringRequest strReq = new StringRequest(Request.Method.POST,
//                URL_FOR_LOGIN, new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                Log.d(TAG, "Register Response: " + response.toString());
//                //hideDialog();
//                try {
//                    JSONObject jObj = new JSONObject(response);
//
//                    boolean error = jObj.getBoolean("error");
//
//                    if (!error) {
//                        String user = jObj.getJSONObject("user").getString("name");
//                        // Launch User activity
//                        Intent intent = new Intent(
//                                LoginActivity.this,
//                                UserActivity.class);
//                        intent.putExtra("username", user);
//                        startActivity(intent);
//                        finish();
//                    } else {
//
//                        String errorMsg = jObj.getString("error_msg");
//                        Toast.makeText(getApplicationContext(),
//                                errorMsg, Toast.LENGTH_LONG).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "Login Error: " + error.getMessage());
//                Toast.makeText(getApplicationContext(),
//                        error.getMessage(), Toast.LENGTH_LONG).show();
//                //hideDialog();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                // Posting params to login url
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("email", email);
//                params.put("password", password);
//                return params;
//            }
//        };
//        // Adding request to request queue
//        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq,cancel_req_tag);




    }

}
