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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {
    private EditText book;
    private Button submit;
    private ListView listView;
    //private ArrayAdapter<String> adapter;
    //private ArrayList<JSONObject> arrayList;
    ArrayList<BookModel> dataModels;
    private static CustomAdapter adapter;
    RequestQueue queue;
    private String bookTitleSearched=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23)
            if ((checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) || (checkSelfPermission(android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) || (checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)) {
                Log.v(TAG,"Permission not granted");
                missingPermissions();

            }

        book = (EditText) findViewById(R.id.textview);
        submit = (Button) findViewById(R.id.submit);
        listView = (ListView) findViewById(R.id.list);
        registerForContextMenu(listView);
        dataModels= new ArrayList<>();
        queue = Volley.newRequestQueue(getApplicationContext());
        adapter= new CustomAdapter(dataModels,getApplicationContext());
        listView.setAdapter(adapter);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookTitleSearched = book.getText().toString();
                // TODO GET LIBRARIAN ID FROM SESSION!!!!!!!!
                String url = API.GetBooks+"?"+"title="+bookTitleSearched+"&enteredby=1";
                Log.d("URL", url);
                volleyJsonArrayRequest(url);
                // this line adds the data of your EditText and puts in your array
                //arrayList.add();
                // next thing you have to do is check if your adapter has changed
                //adapter.notifyDataSetChanged();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    private void missingPermissions(){
        Toast toast = Toast.makeText(getApplicationContext(), "Some permissions are missing, please enable them first, exiting app!", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();


        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                Intent aboutIntent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(aboutIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.list) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.edit:
                int pos = ((AdapterView.AdapterContextMenuInfo)info).position;
                Log.d("Position", String.valueOf(pos));
                BookModel tempedit=dataModels.get(pos);
                Intent editIntent = new Intent(MainActivity.this, AddActivity.class);
                // editIntent.putExtra("Bookobject", tempedit);
                startActivity(editIntent);
                return true;
            case R.id.delete:
                int position = ((AdapterView.AdapterContextMenuInfo)info).position;
                Log.d("Position", String.valueOf(position));
                BookModel temp=dataModels.get(position);
                String url = API.DeleteBooks+"?bookId="+temp.getId();
                volleyStringRequest(url);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    //Volley to Get Books data
    public void volleyJsonArrayRequest(String url){
        dataModels.clear();
        String  REQUEST_TAG = "volley";
        JSONObject result = new JSONObject();
        JsonArrayRequest  jsonObjectReq = new JsonArrayRequest(Request.Method.GET,url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("VolleyREsponse", response.toString());
                        try {
                            for(int i = 0; i < response.length(); i++) {
                                JSONObject objects = response.getJSONObject(i);
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

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley ERROR", error);
                Log.getStackTraceString(error);
                Log.d("EROR",error.toString());
                Log.d("EROR", String.valueOf(error.networkResponse));
            }
        });

        // Adding JsonObject request to request queue
        //AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);
        queue.add(jsonObjectReq);
    }

    //Volley to Delete Book data
    public void volleyStringRequest(String url){

        String  REQUEST_TAG = "volley";
        JSONObject result = new JSONObject();
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("VolleyREsponse", response);
                        if(response.equals("Deleted Book!")){
                            Toast.makeText(getApplicationContext(),"Book Deleted!",Toast.LENGTH_SHORT).show();
                            String url = API.GetBooks+"?"+"title="+bookTitleSearched+"&enteredby=1";
                            volleyJsonArrayRequest(url);
                        } else {
                            Toast.makeText(getApplicationContext(),"Book Not Deleted!",Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley ERROR", error);
                Log.getStackTraceString(error);
                Log.d("EROR",error.toString());
                Log.d("EROR", String.valueOf(error.networkResponse));
            }
        });

        // Adding JsonObject request to request queue
        //AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);
        queue.add(stringRequest);
    }

}

