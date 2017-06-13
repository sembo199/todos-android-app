package nl.semekkelboom.todoimtpmd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import nl.semekkelboom.todoimtpmd.models.Todo;

public class UserAreaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        TextView tv = (TextView) findViewById(R.id.textView2);
        ListView lv = (ListView) findViewById(R.id.LVTODOS);

        Bundle extras = getIntent().getExtras();
        String email = extras.getString("email");
        final String authtoken = extras.getString("authtoken");
        final ArrayList<Todo> todos = new ArrayList<Todo>();

        final BaseAdapter la = new ArrayAdapter<Todo>(this, android.R.layout.simple_list_item_1, todos);

        lv.setAdapter(la);

        tv.setText("Welcome " + email);

        Log.d("OnLoad:", "Ophalen van todos -------------");
        final String URL = "https://peaceful-scrubland-20759.herokuapp.com/todos";

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,URL,
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response", response.toString());
                try {
                    JSONArray ja = response.getJSONArray("todos");
                    if(ja.length() == 0) {

                    } else {
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject object = ja.getJSONObject(i);
                            String text = object.getString("text");
                            System.out.println("######## " + text + " ######");
                            if (object.getBoolean("completed")) {
                                Todo td = new Todo(object.getString("_id"), object.getString("text"), object.getString("_creator"), object.getBoolean("completed"), object.getLong("completedAt"));
                                todos.add(td);
                            } else {
                                Todo td = new Todo(object.getString("_id"), object.getString("text"), object.getString("_creator"), object.getBoolean("completed"));
                                todos.add(td);
                            }
                            Log.d("TodoArray", todos.toString());
                        }
                        la.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Something wrong", "Error: " + error.getMessage());
                Log.e("Something wrong again", "Site Info Error: " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("x-auth", authtoken);
                return headers;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(req);
    }
}
