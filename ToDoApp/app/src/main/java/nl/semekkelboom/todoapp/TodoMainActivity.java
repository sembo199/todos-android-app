package nl.semekkelboom.todoapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.semekkelboom.todoapp.models.Todo;
import nl.semekkelboom.todoapp.models.User;

public class TodoMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView tv = (TextView) findViewById(R.id.textView2);
        ListView lv = (ListView) findViewById(R.id.LVTODOS);

        Bundle extras = getIntent().getExtras();
        final User user = (User) extras.getSerializable("user");

        final ArrayList<Todo> todos = new ArrayList<Todo>();
        final BaseAdapter la = new ArrayAdapter<Todo>(this, android.R.layout.simple_list_item_1, todos);

        lv.setAdapter(la);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(todos.get(position).toString());
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(TodoMainActivity.this);

                alert.setTitle("Add new Todo");
                alert.setMessage("Give the text of your new todo:");

                // Set an EditText view to get user input
                final EditText input = new EditText(TodoMainActivity.this);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Send request to API
                        Log.d("Onclick:", "Add Todo -------------");
                        final String URL = "https://peaceful-scrubland-20759.herokuapp.com/todos";
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("text", input.getText().toString());

                        MetaRequest request_json = new MetaRequest(URL, new JSONObject(params),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("OnLoad:", "Ophalen van todos -------------");
                                        todos.clear();
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
                                                headers.put("x-auth", user.getAuthtoken());
                                                return headers;
                                            }
                                        };

                                        Volley.newRequestQueue(getApplicationContext()).add(req);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Context context = getApplicationContext();
                                CharSequence text = "Something went wrong!";
                                int duration = Toast.LENGTH_LONG;

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                                VolleyLog.e("Error: ", error.getMessage());
                            }
                        }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                HashMap<String, String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json");
                                headers.put("x-auth", user.getAuthtoken());
                                return headers;
                            }
                        };

                        Volley.newRequestQueue(getApplicationContext()).add(request_json);
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }
        });

        View headerView = navigationView.getHeaderView(0);
        TextView navdrawerTitle = (TextView) headerView.findViewById(R.id.navdrawerTitle);
        navdrawerTitle.setText(user.getEmail());


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Todo td = todos.get(position);
                System.out.println(td.toString());

                AlertDialog.Builder builder = new AlertDialog.Builder(TodoMainActivity.this);

                builder.setTitle("Update Todo");
                builder.setMessage("Change your todo: " + td.toString() + " or complete it?");

                LinearLayout ll = new LinearLayout(TodoMainActivity.this);
                ll.setLayoutParams( new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
                ll.setOrientation(LinearLayout.VERTICAL);

                final CheckBox cb = new CheckBox(TodoMainActivity.this);
                cb.setText("Completed: " + td.getText());
                cb.setChecked(td.isCompleted());

                final EditText et = new EditText(TodoMainActivity.this);
                et.setText(td.getText());
                et.setFocusable(!td.isCompleted());
                // Todo: Melding als todo al gecomplete is dus tekst niet gewijzigd kan worden, bvb na onclick op editText

                ll.addView(et);
                ll.addView(cb);

                builder.setView(ll);

                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // update todo
                        Log.d("Onclick:", "Update Todo -------------");
                        final String URL = "https://peaceful-scrubland-20759.herokuapp.com/todos/" + td.get_id();
                        System.out.println(td.get_id()+ "\n" + URL);
                        JSONObject obj = new JSONObject();

                        if (td.getText().equals(et.getText().toString()) && td.isCompleted() == cb.isChecked()) {
                            System.out.println("nothing changed");
                            return;
                        } else if (!td.getText().equals(et.getText().toString()) && td.isCompleted() != cb.isChecked()) {
                            System.out.println("both changed");

                            Log.d("checked value", String.valueOf(cb.isChecked()));

                            try {
                                obj.put("completed", cb.isChecked());
                                obj.put("text", et.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (!td.getText().equals(et.getText().toString())) {
                            System.out.println("text changed");
                            try {
                                obj.put("text", et.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else if (td.isCompleted() != cb.isChecked()) {
                            System.out.println("completed changed");
                            try {
                                obj.put("completed", cb.isChecked());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        JsonObjectRequest req = new JsonObjectRequest(Request.Method.PATCH,URL,
                                obj, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("Response", response.toString());
                                try {
                                    JSONObject updatedTodo = response.getJSONObject("todo");
                                    if (updatedTodo.getBoolean("completed")) {
                                        todos.set(position, new Todo(updatedTodo.getString("_id"), updatedTodo.getString("text"), updatedTodo.getString("_creator"), updatedTodo.getBoolean("completed"), updatedTodo.getLong("completedAt")));
                                    } else {
                                        todos.set(position, new Todo(updatedTodo.getString("_id"), updatedTodo.getString("text"), updatedTodo.getString("_creator"), updatedTodo.getBoolean("completed")));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                la.notifyDataSetChanged();
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
                                headers.put("x-auth", user.getAuthtoken());
                                return headers;
                            }
                        };

                        Volley.newRequestQueue(getApplicationContext()).add(req);

                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do not update Todo
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });


        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final Todo td = todos.get(position);
                System.out.println( td.toString());
                AlertDialog.Builder builder = new AlertDialog.Builder(TodoMainActivity.this);

                builder.setTitle("Delete Todo?");
                builder.setMessage("Are you sure you want to delete the following todo: " + td.toString() + "?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Remove the Todo
                        final String URL = "https://peaceful-scrubland-20759.herokuapp.com/todos/" + td.get_id();

                        JsonObjectRequest req = new JsonObjectRequest(Request.Method.DELETE,URL,
                                null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("Response", response.toString());
                                try {
                                    JSONObject todoback = response.getJSONObject("todo");
                                    System.out.println(todoback);
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
                                headers.put("x-auth", user.getAuthtoken());
                                return headers;
                            }
                        };

                        Volley.newRequestQueue(getApplicationContext()).add(req);
                        todos.remove(position);
                        la.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do not remove Todo
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        });

        tv.setText("Welcome " + user.getEmail());

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
                headers.put("x-auth", user.getAuthtoken());
                return headers;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(req);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.todo_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        }  else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
