package njurcak.studentevaltool;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class teacher extends FragmentActivity implements ActionBar.TabListener {

    ActionBar actionbar;
    String url;
    String schoolUrl;
    String responseText;
    Handler handler = new Handler();
    List<String> all_course_id = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_teacher);
        Intent indent = getIntent();
        url = indent.getStringExtra("url");
        schoolUrl = indent.getStringExtra("schoolUrl");
        actionbar = getActionBar();
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionbar.addTab(actionbar.newTab().setText("Profile").setTabListener(this));
        actionbar.addTab(actionbar.newTab().setText("Courses").setTabListener(this));
        actionbar.addTab(actionbar.newTab().setText("Edit").setTabListener(this));

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

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

        int position = tab.getPosition();

        switch(position){
            case 0:
                new HttpAsyncTask().execute(url);
                setContentView(R.layout.activity_view_teacher);
                break;
            case 1:
                setContentView(R.layout.fragment_teacher_course);
                new AllCoursesHttpAsyncTask().execute(schoolUrl + "/course");
                break;
            case 2:
                setContentView(R.layout.fragment_teacher_edit);
                break;
            default:
                break;
        }

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    public void saveEditTeacher(View view) {

        TextView fname = (TextView) findViewById(R.id.efName);
        TextView lname = (TextView) findViewById(R.id.elName);
        TextView username = (TextView) findViewById(R.id.eusername);
        TextView password = (TextView) findViewById(R.id.epassword);
        Spinner grade = (Spinner) findViewById(R.id.egrade);
        TextView status = (TextView) findViewById(R.id.estatus);

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

        new putAddTeacher().execute(fname.getText().toString(), lname.getText().toString(), username.getText().toString(), password.getText().toString());

        status.setText(responseText);
    }
    private class putAddTeacher extends AsyncTask<String, Integer, Double> {

        TextView status = (TextView) findViewById(R.id.estatus);
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
                HttpPut post = new HttpPut(url);

                List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(4);
                nameValuePair.add(new BasicNameValuePair("fname", params[0]));
                nameValuePair.add(new BasicNameValuePair("lname", params[1]));
                nameValuePair.add(new BasicNameValuePair("username", params[2]));
                nameValuePair.add(new BasicNameValuePair("password", params[3]));

                post.setEntity(new UrlEncodedFormEntity(nameValuePair));

                try {
                    HttpResponse response = client.execute(post);
                    Log.d("HTTP Post Response:", response.toString());
                    int code = response.getStatusLine().getStatusCode();
                    if(code == 200){
                        responseText = "Teacher Successfully Saved!!!";
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

    private class deleteAccount extends AsyncTask<String, Integer, Double> {
        TextView status = (TextView) findViewById(R.id.estatus);
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
                HttpDelete post = new HttpDelete(url);

                HttpResponse response = client.execute(post);
                Log.d("HTTP Post Response:", response.toString());
                int code = response.getStatusLine().getStatusCode();
                if (code == 200) {
                    responseText = "Profile Successfully Deleted!!!";
                } else if (code == 403) {
                    responseText = "You cannot delete profile.";
                } else {
                    responseText = "There is a problem with our severs, Please try again";
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void deleteTeacher(View view){

        new deleteAccount().execute(url);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
            }
        }, 5000);

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

    public void logout(View view){

        Intent intent = new Intent(getApplicationContext(), Home.class);
        startActivity(intent);

    }

    private class AllCoursesHttpAsyncTask extends AsyncTask<String, Void, String> {
        Spinner courseList = (Spinner) findViewById(R.id.teachCourse);
        Button removeCourse = (Button) findViewById(R.id.removeCourse);
        Button addQuestion = (Button) findViewById(R.id.addQuestion);
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        @Override
        protected void onPostExecute(String result){
            //Toast.makeText(getBaseContext(), "Recieved!", Toast.LENGTH_LONG).show();
            ArrayList<String> arrayList = new ArrayList<String>();
            try {
                JSONArray json = new JSONArray(result);
                for (int i = 0; i < json.length(); i++) {
                    arrayList.add(json.getJSONObject(i).getString("name"));
                    all_course_id.add(json.getJSONObject(i).getString("key"));
                    addQuestion.setVisibility(View.VISIBLE);
                    removeCourse.setVisibility(View.VISIBLE);
                }
                ArrayAdapter<String> adp = new ArrayAdapter<String> (getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,arrayList);
                courseList.setAdapter(adp);
            } catch (JSONException e){
                Log.e("JSON", "Invalid JSON string: " + result, e);
            }
        }
    }

    public void addCourse (View view){

        TextView courseName = (TextView) findViewById(R.id.courseName);
        new postAddCourse().execute(courseName.getText().toString());
        new AllCoursesHttpAsyncTask().execute(schoolUrl + "/course");

    }

    private class postAddCourse extends AsyncTask<String, Integer, Double> {

        TextView status = (TextView) findViewById(R.id.status);
        @Override
        protected Double doInBackground(String... params) {
            Double resultMessage = postData(params);
            return resultMessage;
        }

        protected void onPostExecute(Double result) {
            status.setText(responseText);
        }

        String url = schoolUrl + "/course";

        protected void onProgressUpdate(Integer... progress){
            status.setText(responseText);
        }

        public Double postData(String[] params) {

            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(url);

                List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
                nameValuePair.add(new BasicNameValuePair("name", params[0]));

                post.setEntity(new UrlEncodedFormEntity(nameValuePair));

                try {
                    HttpResponse response = client.execute(post);
                    Log.d("HTTP Post Response:", response.toString());
                    int code = response.getStatusLine().getStatusCode();
                    if(code == 200){
                        responseText = "Course Successfully Saved!!!";
                    }
                    else if(code==403){
                        responseText = "Course name already taken, Please try again.";
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

    public void addQuestion (View view){

        TextView question = (TextView) findViewById(R.id.question);
        TextView answer = (TextView) findViewById(R.id.answer);
        new postAddQuestion().execute(question.getText().toString(), answer.getText().toString());



    }

    private class postAddQuestion extends AsyncTask<String, Integer, Double> {
        Spinner courseList = (Spinner) findViewById(R.id.teachCourse);
        TextView status = (TextView) findViewById(R.id.status);
        @Override
        protected Double doInBackground(String... params) {
            Double resultMessage = postData(params);
            return resultMessage;
        }

        protected void onPostExecute(Double result) {
            status.setText(responseText);
        }

        String url = schoolUrl + "/course/";

        protected void onProgressUpdate(Integer... progress){
            status.setText(responseText);
        }

        public Double postData(String[] params) {

            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(url+all_course_id.get(courseList.getSelectedItemPosition())+"/question");

                List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
                nameValuePair.add(new BasicNameValuePair("body", params[0]));
                nameValuePair.add(new BasicNameValuePair("answer", params[1]));

                post.setEntity(new UrlEncodedFormEntity(nameValuePair));

                try {
                    HttpResponse response = client.execute(post);
                    Log.d("HTTP Post Response:", response.toString());
                    int code = response.getStatusLine().getStatusCode();
                    if(code == 200){
                        responseText = "Question Successfully Saved!!!";
                    }
                    else if(code==403){
                        responseText = "Question already exists, Please try again.";
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

    public void deleteCourse (View view){

        new teachDeleteCourse().execute();
        new AllCoursesHttpAsyncTask().execute(schoolUrl + "/course");

    }

    private class teachDeleteCourse extends AsyncTask<String, Integer, Double> {
        Spinner courseList = (Spinner) findViewById(R.id.teachCourse);
        TextView status = (TextView) findViewById(R.id.status);
        @Override
        protected Double doInBackground(String... params) {
            Double resultMessage = postData();
            return resultMessage;
        }

        protected void onPostExecute(Double result) {
            status.setText(responseText);
        }

        String url = schoolUrl + "/course/";

        protected void onProgressUpdate(Integer... progress){
            status.setText(responseText);
        }

        public Double postData() {

                HttpClient client = new DefaultHttpClient();
                HttpDelete post = new HttpDelete(url+all_course_id.get(courseList.getSelectedItemPosition()));

                try {
                    HttpResponse response = client.execute(post);
                    Log.d("HTTP Post Response:", response.toString());
                    int code = response.getStatusLine().getStatusCode();
                    if(code == 200){
                        responseText = "Course Successfully Deleted!!!";
                    }
                    else if(code==403){
                        responseText = "Course was not deleted, Please try again.";
                    }
                    else {
                        responseText = "There is a problem with our severs, Please try again";
                    }
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            return null;
            };

        }
}

