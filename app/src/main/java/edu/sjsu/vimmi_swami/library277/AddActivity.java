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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

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
                    String url = API.UpdateBooks +
                            "?book=" + book;
                    Log.d("URL", url);
                    volleyStringRequest(url);
                }else {
                    // TODO GET LIBRARIAN ID FROM SESSION!!!!!!!!
                    String url = API.PostBooks +
                            "?author=" + sauthor
                            + "&title=" + stitle
                            + "&callNumber=" + scallnumber
                            + "&publisher=" + spublisher
                            + "&year=" + syear
                            + "&location=" + slocation
                            + "&copies=" + scopies
                            + "&status=" + sstatus
                            + "&keywords=" + skeywords
                            + "&enteredby=1"
                            + "&image=" + simage;
                    Log.d("URL", url);
                    volleyStringRequest(url);
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
    public void volleyStringRequest(String url){

        String  REQUEST_TAG = "volley";
        JSONObject result = new JSONObject();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("VolleyREsponse", response);
                        Toast.makeText(getApplicationContext(),"Book Added!",Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley ERROR", error);
                Log.getStackTraceString(error);
                Log.d("EROR",error.toString());
                Log.d("EROR", String.valueOf(error.networkResponse));
            }
        })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("xyz",	"xys");
                return params;
            }
        };

        // Adding JsonObject request to request queue
        //AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);
        Log.d("before VOLLEY","q");

        queue.add(stringRequest);
    }
}
