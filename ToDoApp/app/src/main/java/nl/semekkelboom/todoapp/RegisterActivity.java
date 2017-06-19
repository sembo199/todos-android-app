package nl.semekkelboom.todoapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final Button bRegister = (Button) findViewById(R.id.bRegister);

        bRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("Onclick:", "Register -------------");
                final ProgressDialog progress = new ProgressDialog(RegisterActivity.this);
                progress.setTitle("Loading");
                progress.setMessage("Trying to register, waiting for redirect...");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();
                final String URL = "https://peaceful-scrubland-20759.herokuapp.com/users";
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("email", etEmail.getText().toString());
                params.put("password", etPassword.getText().toString());

                if (etPassword.getText().length() < 8) {
                    progress.dismiss();
                    Context context = getApplicationContext();
                    CharSequence text = "Password should at least be 8 characters long!";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {
                    JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(params),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    progress.dismiss();
                                    try {
                                        String id = response.getString("_id"),
                                                email = response.getString("email");
                                        System.out.println("Id: "+id+"\nEmail: "+email);
                                        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                                        i.putExtra("email", email);
                                        RegisterActivity.this.startActivity(i);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.e("Error: ", error.getMessage());
                            progress.dismiss();
                            Context context = getApplicationContext();
                            CharSequence text = "Something went wrong...\nIs your email correct?";
                            int duration = Toast.LENGTH_LONG;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    });

                    Volley.newRequestQueue(getApplicationContext()).add(request_json);
                }
            }
        });
    }
}
