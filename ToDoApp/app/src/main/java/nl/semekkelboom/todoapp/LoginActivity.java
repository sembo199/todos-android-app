package nl.semekkelboom.todoapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import nl.semekkelboom.todoapp.models.User;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final Button bLogin = (Button) findViewById(R.id.bLogin);
        final TextView tv = (TextView) findViewById(R.id.textView);
        final Button bRegister = (Button) findViewById(R.id.bRegister);

        etEmail.requestFocus();

        String email;
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            email= extras.getString("email");
            etEmail.setText(email);
        }

        bLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (etEmail.getText().toString().equals("") || etPassword.getText().toString().equals("")) {
                    Context context = getApplicationContext();
                    CharSequence text = "You need to fill in all the fields!";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {
                    Log.d("Onclick:", "Inloggen -------------");
                    final String URL = "https://peaceful-scrubland-20759.herokuapp.com/users/login";
                    final ProgressDialog progress = new ProgressDialog(LoginActivity.this);
                    progress.setTitle("Loading");
                    progress.setMessage("Wait while loading...");
                    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progress.show();
                    HashMap<String, String> params = new HashMap<String, String>();
//                    params.put("email", etEmail.getText().toString());
//                    params.put("password", etPassword.getText().toString());
                    params.put("email", "bvbovene@gmail.com");
                    params.put("password", "12345678");

                    MetaRequest request_json = new MetaRequest(URL, new JSONObject(params),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    progress.dismiss();
                                    try {
                                        JSONObject headers = response.getJSONObject("headers");
                                        String authtoken = headers.getString("X-Auth");
                                        String id = response.getString("_id"),
                                                email = response.getString("email");
                                        System.out.println(response);
                                        System.out.println("Id: " + id + "\nEmail: " + email + "\nToken: " + authtoken);
                                        Intent i = new Intent(LoginActivity.this, TodoMainActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("user",new User(id, email, authtoken));
                                        i.putExtras(bundle);
                                        LoginActivity.this.startActivity(i);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Context context = getApplicationContext();
                                        CharSequence text = "Oops, something went wrong while getting your data!";
                                        int duration = Toast.LENGTH_LONG;

                                        Toast toast = Toast.makeText(context, text, duration);
                                        toast.show();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Context context = getApplicationContext();
                            CharSequence text = "Wrong Email or password!";
                            int duration = Toast.LENGTH_LONG;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                            VolleyLog.e("Error: ", error.getMessage());
                        }
                    });

                    Volley.newRequestQueue(getApplicationContext()).add(request_json);
                }
            }
        });

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });
    }
}
