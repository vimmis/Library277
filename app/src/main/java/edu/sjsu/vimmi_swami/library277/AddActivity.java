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
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
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
    ImageView image;
    private Button submit;
    private Button cancel;
    private Button upload;
    RequestQueue queue;
    String stitle=null;
    String sauthor=null;
    String spublisher=null;
    String syear=null;
    String scopies=null;
    String scallnumber=null;
    String sstatus=null;
    String slocation=null;
    String skeywords=null;
    String simage=null;
    BookModel book = null;
    public static final int RESULT_GALLERY = 0;
    SessionManagement session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        session = new SessionManagement(getApplicationContext());
        session.loginValidation();
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
        //Let by default be ""

        title.setText("");
        author.setText("");
        publisher.setText("");
        year.setText("");
        copies.setText("");
        callnumber.setText("");
        status.setText("");
        location.setText("");
        keywords.setText("");

        Intent i = getIntent();
        book = (BookModel)i.getSerializableExtra("Bookobject");
        if (book != null){
            title.setText(book.getTitle());
            author.setText(book.getAuthor());
            publisher.setText(book.getPublisher());
            year.setText(book.getYear());
            copies.setText(book.getCopies());
            callnumber.setText(book.getCallnumber());
            status.setText(book.getStatus());
            location.setText(book.getLocation());
            keywords.setText(book.getKeywords());
            submit.setText("Edit");
        }
        queue = Volley.newRequestQueue(getApplicationContext());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!submit.getText().equals("Add")){
                    book.setTitle(title.getText().toString());
                    book.setTitle(author.getText().toString());
                    book.setTitle(publisher.getText().toString());
                    book.setTitle(year.getText().toString());
                    book.setTitle(copies.getText().toString());
                    book.setTitle(callnumber.getText().toString());
                    book.setTitle(status.getText().toString());
                    book.setTitle(location.getText().toString());
                    book.setTitle(keywords.getText().toString());
                }else {
                    stitle = title.getText().toString();
                    sauthor = author.getText().toString();
                    spublisher = publisher.getText().toString();
                    syear = year.getText().toString();
                    scopies = copies.getText().toString();
                    scallnumber = callnumber.getText().toString();
                    sstatus = status.getText().toString();
                    slocation = location.getText().toString();
                    skeywords = keywords.getText().toString();
                }
                if(!submit.getText().equals("Add")){
                    // TODO GET LIBRARIAN ID FROM SESSION!!!!!!!!
                    String url = API.UpdateBooks ;
                    JSONObject payload = new JSONObject();
                    try {
                        payload.put("author", book.author);
                        payload.put("title", book.title);
                        payload.put("callNumber", book.callnumber);
                        payload.put("publisher", book.publisher);
                        payload.put("year", book.year);
                        payload.put("location", book.location);
                        payload.put("copies", book.copies);
                        payload.put("status", book.status);
                        payload.put("keywords", book.keywords);
                        payload.put("enteredby", session.getSessionDetails().get(SessionManagement.KEY_USER_ID));
                        payload.put("image", simage);
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("URL", url);
                    volleyStringRequest(url,payload,false);
                }else {
                    // TODO GET LIBRARIAN ID FROM SESSION!!!!!!!!
                    String url = API.PostBooks ;
                    JSONObject payload = new JSONObject();
                    try {
                        payload.put("author",sauthor);
                        payload.put("title",stitle);
                        payload.put("callNumber",scallnumber);
                        payload.put("publisher",spublisher);
                        payload.put("year",syear);
                        payload.put("location",slocation);
                        payload.put("copies",scopies);
                        payload.put("status",sstatus);
                        payload.put("keywords",skeywords);
                        payload.put("enteredby",session.getSessionDetails().get(SessionManagement.KEY_USER_ID));
                        payload.put("image",simage);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d("URL", url);
                    volleyStringRequest(url,payload,true);
                }
                Log.d("AFTER VOLLEY","k");

                //clear text later
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
                    //simage = Base64.encodeToString(b, Base64.NO_WRAP | Base64.URL_SAFE);
                    simage="";
                    Log.d("Stringimage", simage);
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
                                Toast.makeText(getApplicationContext(),response.getString("msg"),Toast.LENGTH_SHORT).show();

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
                                Toast.makeText(getApplicationContext(),jsonObj.getString("msg"),Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "There is an error. Please contact admin for more info", Toast.LENGTH_LONG).show();

                        }
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
        AppSingleton.get(getApplicationContext()).addRequest(jsonObjectRequest, "Add Book");

    }
}
