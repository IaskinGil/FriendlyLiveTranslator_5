package il.ac.shenkar.friendlylivetranslator_5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import il.ac.shenkar.library.DatabaseHandler;

import java.util.HashMap;

public class SignedUp extends Activity 
{
    /**
     * Called when the activity is first created
     **/
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signedup);

        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        HashMap<String,String> user = new HashMap<String, String>();
        user = db.getUserDetails();

        /**
         * Displays the registration details in textview
         **/
        final TextView fname = (TextView)findViewById(R.id.fname);
        final TextView lname = (TextView)findViewById(R.id.lname);
        final TextView uname = (TextView)findViewById(R.id.uname);
        final TextView email = (TextView)findViewById(R.id.email);
        final TextView created_at = (TextView)findViewById(R.id.regat);
        fname.setText(user.get("fname"));
        lname.setText(user.get("lname"));
        uname.setText(user.get("uname"));
        email.setText(user.get("email"));
        created_at.setText(user.get("created_at"));

        Button login = (Button) findViewById(R.id.done);
        login.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View view) 
            {
                Intent myIntent = new Intent(view.getContext(), LoginActivity.class);
                startActivityForResult(myIntent, 0);
                finish();
            }
        });
    }
}