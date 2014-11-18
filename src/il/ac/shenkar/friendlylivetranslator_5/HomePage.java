package il.ac.shenkar.friendlylivetranslator_5;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import il.ac.shenkar.library.DatabaseHandler;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;

public class HomePage extends Activity implements ConnectionRequestListener, RoomRequestListener
{
	private ImageView settings;
	private WarpClient theClient;
	private ProgressDialog progressDialog;
	DatabaseHandler db;
	HashMap<String,String> user;
	private CheckBox cb3;
	private TextView resultTextView;
	private Button enterChat;
	HashMap<String, Object> propertiesToMatch ;
	private String roomIdJoined = "";
	private long timeCounter = 0;
	private long startTime = 0;
	private String flt = "FriendlyLiveTranslator";
	private Button btnSummon;
	private EditText editSummon;
	SharedPreferences prefs;

    /**
     * Called when the activity is first created
     **/
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

		cb3 = (CheckBox)findViewById(R.id.checkBox3);
		enterChat = (Button)findViewById(R.id.enter);
		enterChat.setEnabled(false);
		resultTextView = (TextView)findViewById(R.id.resultTextView);
        settings = (ImageView) findViewById(R.id.settings);
        btnSummon = (Button)findViewById(R.id.btnSummon);
        btnSummon.setEnabled(false);
		editSummon = (EditText)findViewById(R.id.editSummon);
        db = new DatabaseHandler(getApplicationContext());

        /**
         * Hashmap to load data from the Sqlite database
         **/
         user = new HashMap<String, String>();
         user = db.getUserDetails();
        
		/**
		 * Sets user first name and last name in text view
		 **/
        final TextView login = (TextView) findViewById(R.id.textwelcome);
        login.setText("Welcome  "+user.get("uname"));
        
        /**
         * Activates settings page
         **/
        settings.setOnClickListener(new View.OnClickListener()
        {
        	public void onClick(View arg0)
        	{
        		Intent set = new Intent(getApplicationContext(), Settings.class);
        		startActivity(set);
        	}
        });
        btnSummon.setOnClickListener(new View.OnClickListener()
        {
        	public void onClick(View arg0)
        	{
        		String temp = editSummon.getText().toString();
        		Log.i("btnSummon",temp);
        		if (temp.length()>0)
        		{
	        		//Toast.makeText(MainActivity.this, "One Moment", Toast.LENGTH_SHORT).show();
	        		Intent set = new Intent(getApplicationContext(), App42PhonegapPush.class);
	        		set.putExtra("pushUser", temp);
	        		startActivity(set);
	        		editSummon.setText("One Moment");
        		}
        		else Toast.makeText(HomePage.this, "Enter wanted user", Toast.LENGTH_SHORT).show();
        		editSummon.setText("");
        	}
        });
        enterChat.setOnClickListener(new View.OnClickListener()
        {
        	public void onClick(View arg0)
        	{		
        		Log.i("inside enterChat", "before getDefault");
        		prefs = PreferenceManager.getDefaultSharedPreferences(HomePage.this);
            	String data = prefs.getString("sendStatus", "none");
            	Log.i("inside enterChat", data);
            	if (data.equals("none") || data.equals("true"))
            	{
            		withSend();
            	}
            	else
            	{
            		withoutSend();
            	}
        	}
        });
		init();
        onConnectClicked(0);
    }

    private void init()
    {
		WarpClient.initialize(Config.apiKey, Config.secretKey);
        try 
        {
            theClient = WarpClient.getInstance();
            WarpClient.enableTrace(true);
        } 
        catch (Exception ex) 
        {
        	ex.printStackTrace();
            Toast.makeText(this, "Exception in Initilization", Toast.LENGTH_LONG).show();
        }
        theClient.addConnectionRequestListener(this);
	}
    
    public void onConnectClicked(int zero)
    {
    	String userName = user.get("uname").toString();
		if(userName.length()>zero)
		{
			Utils.USER_NAME = userName;
			progressDialog = ProgressDialog.show(this, "", "Please wait...");
			progressDialog.setCancelable(true);
			theClient.addConnectionRequestListener(this); 
			theClient.connectWithUserName(userName);
		}
	}
    
    @Override
	public void onConnectDone(final ConnectEvent event) 
	{
		Log.d("OnConnectDone", ""+event.getResult());
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{
				if(progressDialog!=null)
				{
					progressDialog.dismiss();
					progressDialog=null;
				}
				if(event.getResult()==WarpResponseResultCode.SUCCESS)
				{
					matchMaking();
				}
				else
				{
					Toast.makeText(HomePage.this, "Connection Failed - "+event.getResult(), Toast.LENGTH_SHORT).show(); 
					resultTextView.setText("\nRoom join failed \nTime Taken: "+timeCounter+"(ms)\nResult code: "+event.getResult());
				}
			}
		});
	}
    
    public void matchMaking()
    {
    	if (propertiesToMatch==null)
		{
			propertiesToMatch = new HashMap<String, Object>();
		}
		else
		{
			propertiesToMatch.clear();
		}
		propertiesToMatch.put("topic", flt);
		timeCounter = 0;
		startTime = System.currentTimeMillis();	
		theClient.joinRoomWithProperties(propertiesToMatch);
    	cb3.setChecked(false);
    	roomIdJoined = "";
    	theClient.addRoomRequestListener(this); 
    }

    public void withSend()
    {
    	Intent intent1 = new Intent(HomePage.this, ChatActivity.class);
		intent1.putExtra("roomId", roomIdJoined);
		startActivity(intent1);
    }
    
    public void withoutSend()
    {
    	Intent intent2 = new Intent(this, NoSendButton.class);
		intent2.putExtra("roomId", roomIdJoined);
		startActivity(intent2);
    }
	
	@Override
	public void onDisconnectDone(ConnectEvent event) 
	{
		// TODO Auto-generated method stub	
	}

	@Override
	public void onInitUDPDone(byte arg0) 
	{
		// TODO Auto-generated method stub		
	}
	
	@Override
	public void onBackPressed(){}

	@Override
	public void onGetLiveRoomInfoDone(final LiveRoomInfoEvent event){}
	

	@Override
	public void onJoinRoomDone(final RoomEvent event) 
	{
		timeCounter = System.currentTimeMillis()-startTime;
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{
				cb3.setChecked(true);
				if (event.getResult()==WarpResponseResultCode.SUCCESS)
				{
					enterChat.setEnabled(true);
					btnSummon.setEnabled(true);
					roomIdJoined = event.getData().getId();
					resultTextView.setText("\nTime Taken: "+timeCounter+"(ms)\nResult Status: Success\nRoomID: "+roomIdJoined );
				}
				else
				{
					resultTextView.setText("\nRoom join failed \nTime Taken: "+timeCounter+"(ms)\nResult code: "+event.getResult());
				}
			}
		});
	}

	@Override
	public void onLeaveRoomDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLockPropertiesDone(byte arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSetCustomRoomDataDone(LiveRoomInfoEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSubscribeRoomDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUnSubscribeRoomDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUnlockPropertiesDone(byte arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdatePropertyDone(LiveRoomInfoEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void update()
	{
		timeCounter++;
	}
 }