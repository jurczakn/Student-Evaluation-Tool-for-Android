package njurcak.studentevaltool;

import android.app.DownloadManager;
import android.os.Looper;
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
import java.lang.String;
import java.net.*;
import java.io.*;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

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
import android.app.Activity;

public class createStudentLogin extends ActionBarActivity {

    String responseText = "";
    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_student_login);
        Spinner grade = (Spinner) findViewById(R.id.grade);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.grade_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        grade.setAdapter(adapter);
    }

    public void save(View view) {

        TextView fname = (TextView) findViewById(R.id.fName);
        TextView lname = (TextView) findViewById(R.id.lName);
        TextView username = (TextView) findViewById(R.id.username);
        TextView password = (TextView) findViewById(R.id.password);
        Spinner grade = (Spinner) findViewById(R.id.grade);
        status = (TextView) findViewById(R.id.status);
        int gLevel = grade.getSelectedItemPosition();
        String[] gradeLevel = getResources().getStringArray(R.array.grade_levels);

        List<String> params = new ArrayList<String>();

        if (fname.getText().length() < 1) {
            status.setText("First Name required!!!");
            Log.i("Empty", "First Name Empty");
            return;
        }

        else params.add(fname.getText().toString());

        if (lname.getText().length() < 1) {
            status.setText("Last Name required!!!");
            return;
        }

        else params.add(lname.getText().toString());

        if (username.getText().length() < 1) {
            status.setText("Username required!!!");
            return;
        }

        else params.add(username.getText().toString());

        if (password.getText().length() < 1) {
            status.setText("Password required!!!");
            return;
        } else params.add(password.getText().toString());


        status.setText("Sending request.");

        new postAddStudent().execute(fname.getText().toString(), lname.getText().toString(), username.getText().toString(), password.getText().toString(), gradeLevel[gLevel]);

        status.setText(responseText);
    }
    private class postAddStudent extends AsyncTask<String, Integer, Double> {

        TextView status = (TextView) findViewById(R.id.status);
        @Override
        protected Double doInBackground(String... params) {
            Double resultMessage = postData(params);
            return resultMessage;
        }

        protected void onPostExecute(Double result) {
            status.setText(responseText);
        }

        Intent indent = getIntent();
        String url = indent.getStringExtra("url");

        protected void onProgressUpdate(Integer... progress){
            status.setText(responseText);
        }

        public Double postData(String[] params) {

            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(url);

                List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);
                nameValuePair.add(new BasicNameValuePair("fname", params[0]));
                nameValuePair.add(new BasicNameValuePair("lname", params[1]));
                nameValuePair.add(new BasicNameValuePair("username", params[2]));
                nameValuePair.add(new BasicNameValuePair("password", params[3]));
                nameValuePair.add(new BasicNameValuePair("grade", params[4]));

                post.setEntity(new UrlEncodedFormEntity(nameValuePair));

                try {
                    HttpResponse response = client.execute(post);
                    Log.d("HTTP Post Response:", response.toString());
                    int code = response.getStatusLine().getStatusCode();
                    if(code == 200){
                        responseText = "Student Successfully Saved!!!";
                    }
                    else if(code==403){
                        responseText = "Username already taken, Please try again.";
                    }
                    else {
                        responseText = "There is a problem with our severs, Please try again";
                    }
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            };
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_login, menu);
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

