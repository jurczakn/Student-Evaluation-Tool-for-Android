package njurcak.studentevaltool;


import android.app.DownloadManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Button;
import android.graphics.Color;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.content.Intent;


/**
 * A login screen that offers login via email/password and via Google+ sign in.
 * <p/>
 * ************ IMPORTANT SETUP NOTES: ************
 * In order for Google+ sign in to work with your app, you must first go to:
 * https://developers.google.com/+/mobile/android/getting-started#step_1_enable_the_google_api
 * and follow the steps in "Step 1" to create an OAuth 2.0 client for your package.
 */

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
public class LoginActivity extends Activity{

        int loginType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginType = 0;
    }

        public void loginTypeTeacher(View view){

            loginType = 1;

        }

        public void loginTypeStudent(View view){

            loginType = 0;

        }

        public void login(View view){

            Intent indent = getIntent();
            String id = indent.getStringExtra(Home.EXTRA_MESSAGE);
            TextView status = (TextView) findViewById(R.id.textView2);

            if(isConnected()){

            }
            else{

                status.setText("Not connected to Internet");
                return;

            }

            if (loginType == 1) {
                new HttpAsyncTask().execute("http://cs-496-assignment-3.appspot.com/school/" + id + "/teacher");
            }

            else{
                new HttpAsyncTask().execute("http://cs-496-assignment-3.appspot.com/school/" + id + "/student");
                //status.setText("Student login selected.");
            }

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

        private static String convertInputStreamToString(InputStream inputStream) throws IOException{
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
                    EditText usernameField = (EditText) findViewById(R.id.editText);
                    EditText passwordField = (EditText) findViewById(R.id.editText2);
                    TextView status = (TextView) findViewById(R.id.textView2);
                    JSONArray json = new JSONArray(result);
                    for (int i = 0; i < json.length(); i++)
                        if (usernameField.getText().toString().equals(json.getJSONObject(i).getString("username"))) {
                            if (passwordField.getText().toString().equals(json.getJSONObject(i).getString("password"))) {
                                status.setText("Login Successful");
                                Intent intent;
                                if (loginType == 1) {
                                    intent = new Intent(getApplicationContext(), teacher.class);
                                    Intent indent = getIntent();
                                    String id = indent.getStringExtra(Home.EXTRA_MESSAGE);
                                    intent.putExtra("url", "http://cs-496-assignment-3.appspot.com/school/" + id + "/teacher/" + json.getJSONObject(i).getString("key"));
                                    intent.putExtra("schoolUrl", "http://cs-496-assignment-3.appspot.com/school/" + id);
                                }
                                else{
                                    intent = new Intent(getApplicationContext(), student.class);
                                    Intent indent = getIntent();
                                    String id = indent.getStringExtra(Home.EXTRA_MESSAGE);
                                    intent.putExtra("url", "http://cs-496-assignment-3.appspot.com/school/" + id + "/student/" + json.getJSONObject(i).getString("key"));
                                    intent.putExtra("schoolUrl", "http://cs-496-assignment-3.appspot.com/school/" + id);
                                }
                                startActivity(intent);
                                return;
                            } else {
                                status.setText("Incorrect Password");
                                return;
                            }
                        }
                    status.setText("Username does not exist");
                } catch (JSONException e){
                    Log.e("JSON", "Invalid JSON string: " + result, e);
                }
            }

        }

        public void create_login(View view){

            Intent intent;

            if (loginType == 1) {
                intent = new Intent(getApplicationContext(), createLogin.class);
                Intent indent = getIntent();
                String id = indent.getStringExtra(Home.EXTRA_MESSAGE);
                intent.putExtra("url", "http://cs-496-assignment-3.appspot.com/school/" + id + "/teacher/");
            }
            else {
                intent = new Intent(getApplicationContext(), createStudentLogin.class);
                Intent indent = getIntent();
                String id = indent.getStringExtra(Home.EXTRA_MESSAGE);
                intent.putExtra("url", "http://cs-496-assignment-3.appspot.com/school/" + id + "/student/");
            }
            startActivity(intent);

        }

}

