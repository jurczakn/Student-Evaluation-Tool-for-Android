package njurcak.studentevaltool;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class viewTeacher extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_teacher);

        Intent indent = getIntent();
        String url = indent.getStringExtra("url");

        TextView status = (TextView) findViewById(R.id.status);

        if(isConnected()){

        }
        else{
            status.setText("You are NOT connected");
        }

        new HttpAsyncTask().execute(url);

    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try{
            HttpClient httpclient = new DefaultHttpClient();

            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e){
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        @Override
        protected void onPostExecute(String result){
            //Toast.makeText(getBaseContext(), "Recieved!", Toast.LENGTH_LONG).show();
            try {
                JSONObject teacher = new JSONObject(result);
                TextView fname = (TextView) findViewById(R.id.fName);
                TextView lname = (TextView) findViewById(R.id.lName);
                TextView username = (TextView) findViewById(R.id.username);
                TextView password = (TextView) findViewById(R.id.password);
                TextView key = (TextView) findViewById(R.id.key);
                TextView status = (TextView) findViewById(R.id.status);
                fname.setText("First Name: "+teacher.getString("fname"));
                lname.setText("Last Name: "+teacher.getString("lname"));
                username.setText("Username: "+teacher.getString("username"));
                password.setText("Password: "+teacher.getString("password"));
                key.setText("Key: "+teacher.getString("key"));
            } catch (JSONException e){
                Log.e("JSON", "Invalid JSON string: " + result, e);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_teacher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
