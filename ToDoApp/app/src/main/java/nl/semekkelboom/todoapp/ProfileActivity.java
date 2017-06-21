package nl.semekkelboom.todoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import nl.semekkelboom.todoapp.models.User;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView tvEmail = (TextView) findViewById(R.id.tvEmail);
        TextView tvId = (TextView) findViewById(R.id.tvId)        ;

        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null)
        {
            User user = (User) bd.getSerializable("user");
            tvEmail.setText("Email: " + user.getEmail());
            tvId.setText("User ID: " + user.getId());
        }
    }
}
