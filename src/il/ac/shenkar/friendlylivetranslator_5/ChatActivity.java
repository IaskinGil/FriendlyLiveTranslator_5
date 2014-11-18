package il.ac.shenkar.friendlylivetranslator_5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.events.ChatEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LobbyData;
import com.shephertz.app42.gaming.multiplayer.client.events.MoveEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.UpdateEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.NotifyListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;

public class ChatActivity extends Activity implements RoomRequestListener, NotifyListener, TextToSpeech.OnInitListener
{	
	private ProgressDialog progressDialog;
	private WarpClient theClient;
	private TextView outputView;
	private EditText inputEditText;
	private ScrollView outputScrollView;
	private Spinner onlineUsers;
	private String roomId="";
	private ArrayList<String> onlineUserList = new ArrayList<String>();
	private Button sendBtn;
	private Spinner lang_send;   //sending language selection
	private Spinner lang_rec;   //receiving language selection
	private String original=null;   //original string from editext
	private TextToSpeech tts_rec;
	private String langSendSelected;
    private String langRecSelected;
    private String translatedText = null;
    private String currUser;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		lang_send = (Spinner) findViewById(R.id.selectLanguage1);
		lang_rec = (Spinner) findViewById(R.id.selectLanguage2);
		outputView = (TextView) findViewById(R.id.outputTextView);
		inputEditText = (EditText) findViewById(R.id.textChat);
		outputScrollView = (ScrollView) findViewById(R.id.outputScrollView);
		sendBtn = (Button) findViewById(R.id.btnSend);
		sendBtn.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View view) 
            {
            	outputScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            	original=inputEditText.getText().toString();
            	outputView.append("\n"+currUser+": "+original);
            	Toast.makeText(ChatActivity.this,"Translating & Sending",Toast.LENGTH_SHORT).show();
            	new MyAsyncTask().execute();
            }
        });	
		onlineUsers = (Spinner) findViewById(R.id.onlineUserSpinner);	
			
		try
		{
			theClient = WarpClient.getInstance();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		roomId = getIntent().getStringExtra("roomId");
		theClient.addRoomRequestListener(this);
		theClient.subscribeRoom(roomId);
		theClient.addNotificationListener(this);
		theClient.getLiveRoomInfo(roomId);
		progressDialog = ProgressDialog.show(this, "", "Please wait..");
		currUser=Utils.USER_NAME.toString();
		tts_rec = new TextToSpeech(this, this);		
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		if (theClient!=null)
		{
			theClient.removeRoomRequestListener(this);
			theClient.unsubscribeRoom(roomId);
			theClient.removeNotificationListener(this);
		}
	}

	class MyAsyncTask extends AsyncTask<Void, Integer, Boolean> 
	{
        @Override
        protected Boolean doInBackground(Void... arg0) 
        {	               
            Translate.setClientId("FriendlyLiveTranslator");
            Translate.setClientSecret("friendlylivetranslator#6266");
            langSendSelected=String.valueOf(lang_send.getSelectedItem());
            Log.i("Language selected......Async",langSendSelected);
            try
            {	
            	if(langSendSelected.equalsIgnoreCase("ENGLISH"))
                {
            		translatedText = Translate.execute(original, Language.ENGLISH);                                              
                }
                else if(langSendSelected.equalsIgnoreCase("FRENCH"))
                {
                    translatedText = Translate.execute(original, Language.FRENCH);                                              
                }
                else if(langSendSelected.equalsIgnoreCase("GERMAN"))
                {
                    translatedText = Translate.execute(original, Language.GERMAN);                                
                }
                else if(langSendSelected.equalsIgnoreCase("ITALIAN"))
                {
                    translatedText = Translate.execute(original, Language.ITALIAN);                                       
                }                                       
            }
            catch(Exception e)
            {
            	Log.i("Error in translation.........",e.toString());
            }
            return true;
        }	
        
        protected void onPostExecute(Boolean result) 
        {
        	theClient.sendChat(translatedText);
        	inputEditText.setText("");
        }
    }
	
	@Override
	public void onGetLiveRoomInfoDone(final LiveRoomInfoEvent event) 
	{
		progressDialog.dismiss();
		if(event.getResult()==0)
		{
			if(event.getJoinedUsers().length>1)
			{// if more than one user is online
				final String onlineUser[] = Utils.removeUsernameFromArray(event.getJoinedUsers());
				for(int i=0;i<onlineUser.length;i++){
					onlineUserList.add(onlineUser[i].toString());
				}
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						fillDataInSpinner(event.getData().getName());
					}
				});
			}
			else
			{ // Alert for no online user found
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						Utils.showToast(ChatActivity.this, "No online user found");
					}
				});
				Log.d("No online user found", "No online user found");
			}
		}
		else
		{
			runOnUiThread(new Runnable() 
			{
				@Override
				public void run() 
				{
					Utils.showToast(ChatActivity.this, "Error in fetching data. Please try later");
				}
			});			
		}
	}
	
	private void fillDataInSpinner(String name)
	{
		if (name!=null && name.length()>0)
		{
			onlineUsers.setPrompt(name);// room name
		}
		String onlineUserArray[] = new String[onlineUserList.size()];
		for (int i=0;i<onlineUserArray.length;i++)
		{
			onlineUserArray[i] = onlineUserList.get(i).toString();
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, onlineUserArray);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    onlineUsers.setAdapter(adapter);
	}
	
	@Override
	public void onJoinRoomDone(RoomEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLeaveRoomDone(RoomEvent arg0) {
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
	public void onUpdatePropertyDone(LiveRoomInfoEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onChatReceived(final ChatEvent event) 
	{
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{
				//this is the receiver - the other side
				if(currUser.equals(event.getSender())==false)
				{
					outputView.append("\n"+event.getSender()+": "+event.getMessage());
					langRecSelected=String.valueOf(lang_rec.getSelectedItem());
					speakOut(event.getMessage(),langRecSelected);
				}
			}
		});
	}
	
	@Override
	public void onPrivateChatReceived(final String userName, final String message) {
		
	}
	@Override
	public void onRoomCreated(RoomData arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onRoomDestroyed(RoomData arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUpdatePeersReceived(UpdateEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onUserJoinedLobby(LobbyData arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUserJoinedRoom(final RoomData roomData, final String userName) 
	{
		if(userName.equals(Utils.USER_NAME)==false)
		{
			onlineUserList.add(userName);
			runOnUiThread(new Runnable() 
			{
				@Override
				public void run() 
				{
					fillDataInSpinner(null);
				}
			});
		}
	}
	
	@Override
	public void onUserLeftLobby(LobbyData arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUserLeftRoom(final RoomData roomData, final String userName) 
	{
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{
				onlineUserList.remove(userName);
				fillDataInSpinner(null);
			}
		});
		
	}
	
	@Override
	public void onMoveCompleted(MoveEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onLockPropertiesDone(byte arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUnlockPropertiesDone(byte arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onUserPaused(String arg0, boolean arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUserResumed(String arg0, boolean arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onGameStarted(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onGameStopped(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUserChangeRoomProperty(RoomData arg0, String arg1,
			HashMap<String, Object> arg2, HashMap<String, String> arg3) {
		// TODO Auto-generated method stub
		
	}

	private void speakOut(String text,String lang) 
	{	
        Log.i("Language selected......speakRec",langRecSelected);
        if(lang.equalsIgnoreCase("ENGLISH"))
        {
        	tts_rec.setLanguage(Locale.US);                                              
        }
        else if(lang.equalsIgnoreCase("FRENCH"))
        {
        	tts_rec.setLanguage(Locale.FRENCH);                                              
        }
        else if(lang.equalsIgnoreCase("GERMAN"))
        {
        	tts_rec.setLanguage(Locale.GERMAN);                               
        }
        else if(lang.equalsIgnoreCase("ITALIAN"))
        {
        	tts_rec.setLanguage(Locale.ITALY);                                       
        }
        tts_rec.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}
	
	@Override
	public void onInit(int status) 
	{
		// TODO Auto-generated method stub
		if (status == TextToSpeech.SUCCESS) 
	    {         
	        int result_rec = tts_rec.setLanguage(Locale.GERMAN);
	        if (result_rec == TextToSpeech.LANG_MISSING_DATA || result_rec == TextToSpeech.LANG_NOT_SUPPORTED) 
	        {
	            Log.e("TTS_Receive", "This Language is not supported");
	        }
	    } 
	    else if (status == TextToSpeech.ERROR) 
	    {
            Toast.makeText(ChatActivity.this,"Error occurred while initializing engine", Toast.LENGTH_SHORT).show();	        
	    }
	}
}
