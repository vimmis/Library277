package edu.sjsu.vimmi_swami.library277;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AddActivity extends Activity implements Serializable{
    EditText title;
    EditText author;
    EditText publisher;
    EditText year;
    EditText copies;
    EditText callnumber;
    EditText status;
    EditText location;
    EditText keywords;
    TextView heading;
    ImageView image;
    private Button submit;
    private Button cancel;
    private Button upload;
    RequestQueue queue;

    String simage=null;
    int bookpos = 96321456;
    public static final int RESULT_GALLERY = 0;
    SessionManagement session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        session = new SessionManagement(getApplicationContext());
        session.loginValidation();

        heading = (TextView) findViewById(R.id.heading);
        title = (EditText) findViewById(R.id.title);
        author = (EditText) findViewById(R.id.author);
        publisher = (EditText) findViewById(R.id.publish);
        year = (EditText) findViewById(R.id.year);
        copies = (EditText) findViewById(R.id.copies);
        callnumber = (EditText) findViewById(R.id.callnumber);
        status = (EditText) findViewById(R.id.status);
        location = (EditText) findViewById(R.id.location);
        keywords = (EditText) findViewById(R.id.keywords);
        submit = (Button) findViewById(R.id.add);
        cancel = (Button) findViewById(R.id.cancel);
        upload = (Button) findViewById(R.id.upload);
        image = (ImageView) findViewById(R.id.book_cover);

        clearinput();

        Intent i = getIntent();
        bookpos = i.getIntExtra("Objectpos",96321456);
        Bundle temp = i.getExtras();
        if (temp != null && bookpos!= 96321456 ){

            title.setText(MainActivity.dataModels.get(bookpos).getTitle());
            author.setText(MainActivity.dataModels.get(bookpos).getAuthor());
            publisher.setText(MainActivity.dataModels.get(bookpos).getPublisher());
            year.setText(MainActivity.dataModels.get(bookpos).getYear());
            copies.setText(MainActivity.dataModels.get(bookpos).getCopies());
            callnumber.setText(MainActivity.dataModels.get(bookpos).getCallnumber());
            status.setText(MainActivity.dataModels.get(bookpos).getStatus());
            location.setText(MainActivity.dataModels.get(bookpos).getLocation());
            keywords.setText(MainActivity.dataModels.get(bookpos).getKeywords());
            byte[] decodedString = Base64.decode(MainActivity.dataModels.get(bookpos).getImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            image.setImageBitmap(decodedByte);

            submit.setText("Edit");
            heading.setText("Edit Book");
        }
        queue = Volley.newRequestQueue(getApplicationContext());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JSONObject payload = new JSONObject();
                try {
                    payload.put("author", author.getText());
                    payload.put("title", title.getText());
                    payload.put("callNumber", callnumber.getText());
                    payload.put("publisher", publisher.getText());
                    payload.put("year", year.getText());
                    payload.put("location", location.getText());
                    payload.put("copies", copies.getText());
                    payload.put("status", status.getText());
                    payload.put("keywords", keywords.getText());
                    payload.put("enteredby", session.getSessionDetails().get(SessionManagement.KEY_USER_ID));
                    //Bitmap bm =((BitmapDrawable) image.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    //bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray();
                    payload.put("image",Base64.encodeToString(b, Base64.NO_WRAP));
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                if(!submit.getText().equals("Add")){
                    // TODO GET LIBRARIAN ID FROM SESSION!!!!!!!!
                    String url = API.UpdateBooks ;

                    try {
                        payload.put("oauthor", MainActivity.dataModels.get(bookpos).getAuthor());
                        payload.put("otitle", MainActivity.dataModels.get(bookpos).getTitle());

                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("URL", url);
                    volleyStringRequest(url,payload,false);
                }else {
                    // TODO GET LIBRARIAN ID FROM SESSION!!!!!!!!
                    String url = API.PostBooks ;
                    Log.d("URL", url);
                    volleyStringRequest(url,payload,true);
                }
                Log.d("AFTER VOLLEY","k");

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(AddActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(mainIntent);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent , RESULT_GALLERY );
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AddActivity.RESULT_GALLERY :
                if (null != data && resultCode==RESULT_OK) {
                    Uri imageUri = data.getData();
                    Log.d("URI", String.valueOf(imageUri));
                    image.setImageURI(imageUri);
                    //Bitmap bm = BitmapFactory.decodeFile(imageUri.getPath());
                    Bitmap bm =((BitmapDrawable) image.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray();
                    simage = Base64.encodeToString(b, Base64.NO_WRAP);
                    //simage="";

                    Log.d("Stringimage", simage);
                    byte[] decodedString = Base64.decode(simage, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    image.setImageBitmap(decodedByte);

                }
                break;
            default:
                break;
        }
    }



    //Volley to Add Book data
    public void volleyStringRequest(String url,JSONObject payload, boolean isAdd){
        Log.d("before VOLLEY","q");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(isAdd ? Request.Method.POST : Request.Method.PUT,
                url, payload, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d("VolleyREsponse", response.toString());
                                Toast.makeText(getApplicationContext(),response.getString("msg"),Toast.LENGTH_LONG).show();

                            } catch (Exception ex) {
                            }
                            clearinput();
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

                        }
                        clearinput();
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        //headers.put("Content-Type", "application/json");
                        headers.put("x-access-token", session.getSessionDetails().get(SessionManagement.KEY_TOKEN));
                        headers.put("Content-Type","application/json");
                        return headers;
                    }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        AppSingleton.get(getApplicationContext()).addRequest(jsonObjectRequest, "Add Book");

    }

    private void clearinput(){

        title.setText("");
        title.setText("");
        author.setText("");
        publisher.setText("");
        year.setText("");
        copies.setText("");
        callnumber.setText("");
        status.setText("");
        location.setText("");
        keywords.setText("");
        image.setImageDrawable(null);
    }
}
