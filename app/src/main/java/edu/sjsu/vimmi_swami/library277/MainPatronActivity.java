package edu.sjsu.vimmi_swami.library277;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainPatronActivity extends Activity {
    private EditText searchPatronTextView;
    private Button searchSubmitBtn;
    private ListView searchPatronBookList;
    private Button cancelPatronBtn;
    //private ArrayAdapter<String> adapter;
    //private ArrayList<JSONObject> arrayList;
    static ArrayList<BookModel> dataModels;
    private static CustomAdapterPatron adapter;
    RequestQueue queue;
    private String bookTitleSearched=null;
    private SessionManagement session;
    private Button patronCheckOutBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patron_main);
//       if (Build.VERSION.SDK_INT >= 23)
//           if ((checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) || (checkSelfPermission(android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) || (checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)) {
//              Log.v(TAG,"Permission not granted");
//             missingPermissions();
//       }
        session = new SessionManagement(getApplicationContext());
        session.loginValidation();

        searchPatronTextView = (EditText) findViewById(R.id.patronSearchTextView);
        searchSubmitBtn = (Button) findViewById(R.id.patronSearchBtn);
        searchPatronBookList = (ListView) findViewById(R.id.patronSearchBookList);
        cancelPatronBtn = (Button) findViewById(R.id.patronCancelBtn);
        cancelPatronBtn.setVisibility(View.GONE);
        //registerForContextMenu(searchPatronBookList);
        dataModels= new ArrayList<>();
        queue = Volley.newRequestQueue(getApplicationContext());
        adapter= new CustomAdapterPatron(dataModels,getApplicationContext());
        searchPatronBookList.setAdapter(adapter);


        searchSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookTitleSearched = searchPatronTextView.getText().toString();
                // TODO GET LIBRARIAN ID FROM SESSION!!!!!!!!
                String url = API.GetBooks+"?"+"title="+bookTitleSearched;
                if(bookTitleSearched.equals(""))
                    url = API.GetBooks;
                Log.d("URL", url);
                volleyJsonArrayRequest(url);
                // this line adds the data of your EditText and puts in your array
                //arrayList.add();
                // next thing you have to do is check if your adapter has changed
                //adapter.notifyDataSetChanged();
            }
        });


        patronCheckOutBtn = (Button) findViewById(R.id.patronCheckOutBtn);
        patronCheckOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<BookModel> bookSelectedList = new ArrayList<BookModel>();
                for (int i = 0; i < adapter.getCount(); i++)
                {
                    BookModel book = adapter.getItem(i);
                    if (book.getIsSelected())
                    {
                        bookSelectedList.add(book);
                    }
                }
                if(bookSelectedList.size() > 3){
                    Toast.makeText(getApplicationContext(),
                            "You select more than 3 books",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(bookSelectedList.size() == 0 ){
                    Toast.makeText(getApplicationContext(),
                            "Please select at least one book",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                JSONObject payload = new JSONObject();
                try {
                    payload.put("patronId",session.getSessionDetails().get(SessionManagement.KEY_USER_ID));
                    JSONArray bookList = new JSONArray();
                    for(int i = 0 ; i < bookSelectedList.size() ; i++){
                        JSONObject obj = new JSONObject();
                        obj.put("bookId",bookSelectedList.get(i).id);
                        bookList.put(obj);
                    }
                    payload.put("books",bookList);
                    //call a network
                    volleyStringRequest(API.CheckoutBooks,payload);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.patron_menu_main, menu);
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
            case R.id.patronViewBorrowedBooks:
                Intent aboutIntent = new Intent(MainPatronActivity.this, PatronBorrowedBooksActivity.class);
                startActivity(aboutIntent);
                break;
            case R.id.patronLogout:
                session.logoutSession();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        if (v.getId()==R.id.list) {
//            MenuInflater inflater = getMenuInflater();
//            inflater.inflate(R.menu.menu_list, menu);
//        }
//    }

//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        switch(item.getItemId()) {
//            case R.id.edit:
//                int pos = ((AdapterView.AdapterContextMenuInfo)info).position;
//                Log.d("Position", String.valueOf(pos));
//                BookModel tempedit=dataModels.get(pos);
//                Intent editIntent = new Intent(MainPatronActivity.this, AddActivity.class);
//                editIntent.putExtra("Objectpos", pos);
//                editIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(editIntent);
//                return true;
//            case R.id.delete:
//                int position = ((AdapterView.AdapterContextMenuInfo)info).position;
//                Log.d("Position", String.valueOf(position));
//                BookModel temp=dataModels.get(position);
//                String url = API.DeleteBooks;
//                JSONObject payload = new JSONObject();
//                try {
//                    payload.put("bookId", temp.getId());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                volleyStringRequest(url, payload);
//                return true;
//            default:
//                return super.onContextItemSelected(item);
//        }
//    }


    //Volley to Get Books data
    public void volleyJsonArrayRequest(String url){
        dataModels.clear();
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

//    Volley to Check out data
    public void volleyStringRequest(String url,JSONObject payload){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, payload , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("Response", response.toString());
                    if(response.getBoolean("success")){
                        Toast.makeText(getApplicationContext(),response.getString("msg"),Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), jsonObj.getString("msg"), Toast.LENGTH_LONG).show();
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
                headers.put("x-access-token", session.getSessionDetails().get(SessionManagement.KEY_TOKEN));
                headers.put("Content-Type","application/json");
                return headers;
            }
        };
        AppSingleton.get(getApplicationContext()).addRequest(jsonObjectRequest, "Checkout Books");

    }

}

