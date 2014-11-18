package il.ac.shenkar.friendlylivetranslator_5;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.os.AsyncTask;

public class SplashActivity extends ActionBarActivity 
{
    private static final int SPLASH_SHOW_TIME = 5000;
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new BackgroundSplashTask().execute();
    }

    private class BackgroundSplashTask extends AsyncTask<Void, Void, Void> 
    {
        @Override
        protected Void doInBackground(Void... voids) 
        {
            try 
            {
                Thread.sleep(SPLASH_SHOW_TIME);
            } 
            catch (InterruptedException e) 
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() 
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) 
        {
            super.onPostExecute(result);
            Intent i = new Intent(SplashActivity.this,LoginActivity.class);
            i.putExtra("loaded_info", " ");
            startActivity(i);
            finish();
        }
    }

    /**
     * Inflates the menu. This adds items to the action bar if it is present
     **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        
        getMenuInflater().inflate(R.menu.splash, menu);
        return true;
    }

    /**
     * Handles action bar item clicks here
     **/ 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {     
        int id = item.getItemId();
        if (id == R.id.action_settings) 
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}