package il.ac.shenkar.friendlylivetranslator_5;

import il.ac.shenkar.library.UserFunctions;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class Settings extends Activity
{
	private Button btnLogout;
    private Button changepasMain;
    private Button contact;
    private Switch mySwitch;
    SharedPreferences prefs;

    /**
     * Called when the activity is first created
     **/
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        changepasMain = (Button) findViewById(R.id.btchangepassMain);
        btnLogout = (Button) findViewById(R.id.logout);
        contact = (Button) findViewById(R.id.contact);
        mySwitch = (Switch) findViewById(R.id.switch1);     
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() 
        {  
        	@Override
        	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
        	{
       
        		if(isChecked)
        		{
        			savePreferences("switch_value", mySwitch.isChecked());
        			SharedPreferences.Editor editor = prefs.edit();
        			editor.putString("sendStatus", "true");
        			editor.commit();
        		}
        		else
        		{
        			savePreferences("switch_value", mySwitch.isChecked());
        			SharedPreferences.Editor editor = prefs.edit();
        			editor.putString("sendStatus", "false");
        			editor.commit();
        		}
         }
        });
        
        /**
         * Change password activity started
         **/
        changepasMain.setOnClickListener(new View.OnClickListener()
        {
        	public void onClick(View arg0)
        	{
        		Intent chgpass = new Intent(getApplicationContext(), ChangePassword.class);
        		startActivity(chgpass);
        	}
        });

       /**
        * Logout from the user panel which clears the data in sqlite database
        **/
        btnLogout.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View arg0) 
            {
                UserFunctions logout = new UserFunctions();
                logout.logoutUser(getApplicationContext());
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(login);
                finish();
            }
        });
        
        /**
         * Contact us page
         **/
        contact.setOnClickListener(new View.OnClickListener()
        {
        	public void onClick(View arg0)
        	{
        		Intent con = new Intent(getApplicationContext(), ContactUs.class);
        		startActivity(con);
        	}
        });
        loadSavedPreferences();
    }
    
    private void loadSavedPreferences() 
    {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean switch_value = sharedPreferences.getBoolean("switch_value", true);
		if (switch_value) 
		{
			mySwitch.setChecked(true);
		} 
		else 
		{
			mySwitch.setChecked(false);
		}
	}
    
    private void savePreferences(String key, boolean value) 
    {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
}
