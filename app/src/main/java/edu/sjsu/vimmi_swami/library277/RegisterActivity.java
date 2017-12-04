package edu.sjsu.vimmi_swami.library277;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
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

public class RegisterActivity extends Activity {

    private TextInputLayout nameTxtInputLayout;
    private TextInputEditText nameTxtInputEditText;

    private TextInputLayout studentIDTxtInputLayout;
    private TextInputEditText studentIDTxtInputEditText;

    private TextInputLayout emailTxtInputLayout;
    private TextInputEditText emailTxtInputEditText;

    private TextInputLayout pwdTxtInputLayout;
    private TextInputEditText pwdTxtInputEditText;

    private TextInputLayout confPwdTxtInputLayout;
    private TextInputEditText confPwdTxtInputEditText;

    private AppCompatButton registerBtn;

    private AppCompatTextView logInTxt;

    private InputUtilities inputUtilities;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputUtilities = new InputUtilities(this);


        nameTxtInputLayout = (TextInputLayout) findViewById(R.id.textInputLayoutName);
        nameTxtInputEditText = (TextInputEditText) findViewById(R.id.textInputEditTextName);

        studentIDTxtInputLayout = (TextInputLayout) findViewById(R.id.textInputLayoutStudentId);
        studentIDTxtInputEditText = (TextInputEditText) findViewById(R.id.textInputEditTextStudentId);

        emailTxtInputLayout = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        emailTxtInputEditText = (TextInputEditText) findViewById(R.id.textInputEditTextEmail);

        pwdTxtInputLayout = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        pwdTxtInputEditText = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);

        confPwdTxtInputLayout = (TextInputLayout) findViewById(R.id.textInputLayoutConfirmPassword);
        confPwdTxtInputEditText = (TextInputEditText) findViewById(R.id.textInputEditTextConfirmPassword);

        registerBtn = (AppCompatButton) findViewById(R.id.appCompatButtonRegister);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(inputUtilities.isTxtBoxEmpty(nameTxtInputEditText,nameTxtInputLayout,"Name cannot be empty")){
                    return;
                }
                if(inputUtilities.isTxtBoxEmpty(studentIDTxtInputEditText,studentIDTxtInputLayout,"StudentID cannot be empty")){
                    return;
                }
                if(inputUtilities.isTxtBoxEmpty(emailTxtInputEditText,emailTxtInputLayout,"Email cannot be empty")){
                    return;
                }
                if(!inputUtilities.isEmail(emailTxtInputEditText,emailTxtInputLayout,"Email is not right format")){
                    return;
                }
                if(inputUtilities.isTxtBoxEmpty(pwdTxtInputEditText,pwdTxtInputLayout,"Password cannot be empty")){
                    return;
                }
                if(!inputUtilities.isTxtMatchedFrom(pwdTxtInputEditText,confPwdTxtInputEditText,confPwdTxtInputLayout,"Password is not matching")){
                    return;
                }
                String test = API.registerURL();
                JSONObject payload = new JSONObject();
                try {
                    payload.put("name" , nameTxtInputEditText.getText().toString());
                    payload.put("email" , emailTxtInputEditText.getText().toString());
                    payload.put("password" , pwdTxtInputEditText.getText().toString());
                    payload.put("studentID",studentIDTxtInputEditText.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                        API.registerURL(), payload,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject object = new JSONObject(response.toString());
                                    boolean success = object.getBoolean(("success"));
                                    if(success){
                                        //Send to confirmation page
                                        Toast.makeText(getApplicationContext(), "Account is created successfully. Please check your email for confirmation token", Toast.LENGTH_LONG).show();
                                        Intent intentConfirmation = new Intent(getApplicationContext(), ConfirmationActivity.class);
                                        intentConfirmation.putExtra(ConfirmationActivity.EXTRA_CONFIRM_EMAIL , emailTxtInputEditText.getText().toString());
                                        startActivity(intentConfirmation);
                                    }else{
                                        Toast.makeText(getApplicationContext(),object.getString("msg"), Toast.LENGTH_LONG).show();
                                    }

                                } catch (Exception ex) {
                                    Log.e("err:", ex.getMessage());
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
                                        Toast.makeText(RegisterActivity.this, jsonObj.getString("msg"), Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    Toast.makeText(RegisterActivity.this, "There is an error. Please contact admin for more info", Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                AppSingleton.get(getApplicationContext()).addRequest(jsonObjectRequest, "Sign up");


            }
        });

        logInTxt = (AppCompatTextView) findViewById(R.id.appCompatTextViewLoginLink);
        logInTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });

    }

}
