package njurcak.studentevaltool;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.FragmentActivity;
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

public class student extends FragmentActivity implements ActionBar.TabListener{
    Handler handler = new Handler();
ActionBar actionbar;
    JSONObject teacher;
    String url;
    String responseText = "";
    List<String> all_course_id = new ArrayList<String>();
    List<String> my_course_id = new ArrayList<String>();
    List<String> my_ordered_id = new ArrayList<String>();
    String schoolUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        Intent indent = getIntent();
        url = indent.getStringExtra("url");
        schoolUrl = indent.getStringExtra("schoolUrl");
        actionbar = getActionBar();
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionbar.addTab(actionbar.newTab().setText("Profile").setTabListener(this));
        actionbar.addTab(actionbar.newTab().setText("Courses").setTabListener(this));
        actionbar.addTab(actionbar.newTab().setText("Edit").setTabListener(this));

        TextView status = (TextView) findViewById(R.id.sstatus);

        if(isConnected()){

        }
        else{
            status.setText("You are NOT connected");
        }

        new ProfileHttpAsyncTask().execute(url);
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

    public static String PUT(String url){
        InputStream inputStream = null;
        String result = "";
        try{
            HttpClient httpclient = new DefaultHttpClient();

            HttpResponse httpResponse = httpclient.execute(new HttpPut(url));

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

    private class ProfileHttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        @Override
        protected void onPostExecute(String result){
            //Toast.makeText(getBaseContext(), "Recieved!", Toast.LENGTH_LONG).show();
            try {
                teacher = new JSONObject(result);
                JSONArray courses;
                TextView fname = (TextView) findViewById(R.id.sfName);
                TextView lname = (TextView) findViewById(R.id.slName);
                TextView username = (TextView) findViewById(R.id.susername);
                TextView password = (TextView) findViewById(R.id.spassword);
                TextView grade = (TextView) findViewById(R.id.grade);
                TextView key = (TextView) findViewById(R.id.skey);
                TextView status = (TextView) findViewById(R.id.sstatus);
                fname.setText("First Name: "+teacher.getString("fname"));
                lname.setText("Last Name: "+teacher.getString("lname"));
                username.setText("Username: "+teacher.getString("username"));
                password.setText("Password: "+teacher.getString("password"));
                grade.setText("Grade: "+teacher.getString("grade"));
                courses = teacher.getJSONArray("courses");
                key.setText("Key: "+teacher.getString("key"));
                for (int i = 0; i < courses.length(); i++){
                    my_course_id.add(courses.getString(i));
                }
            } catch (JSONException e){
                Log.e("JSON", "Invalid JSON string: " + result, e);
            }
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

        int position = tab.getPosition();

        switch(position){
            case 0:
                new ProfileHttpAsyncTask().execute(url);
                setContentView(R.layout.fragment_student_profile);
                break;
            case 1:
                setContentView(R.layout.fragment_student_courses);
                new AllCoursesHttpAsyncTask().execute(schoolUrl+"/course");
                break;
            case 2:
                setContentView(R.layout.fragment_student_edit);
                Spinner grade = (Spinner) findViewById(R.id.egrade);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.grade_levels, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                grade.setAdapter(adapter);
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

    public void saveEdit(View view) {

        TextView fname = (TextView) findViewById(R.id.efName);
        TextView lname = (TextView) findViewById(R.id.elName);
        TextView username = (TextView) findViewById(R.id.eusername);
        TextView password = (TextView) findViewById(R.id.epassword);
        Spinner grade = (Spinner) findViewById(R.id.egrade);
        TextView status = (TextView) findViewById(R.id.estatus);
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

        new putAddStudent().execute(fname.getText().toString(), lname.getText().toString(), username.getText().toString(), password.getText().toString(), gradeLevel[gLevel]);

        status.setText(responseText);
    }
    private class putAddStudent extends AsyncTask<String, Integer, Double> {

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

    private class AllCoursesHttpAsyncTask extends AsyncTask<String, Void, String> {
        Spinner courseList = (Spinner) findViewById(R.id.allCourses);
        Spinner myCourseList = (Spinner) findViewById(R.id.myCourses);
        Button startCourse = (Button) findViewById(R.id.button6);
        Button addCourse = (Button) findViewById(R.id.button5);
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        @Override
        protected void onPostExecute(String result){
            //Toast.makeText(getBaseContext(), "Recieved!", Toast.LENGTH_LONG).show();
            ArrayList<String> arrayList = new ArrayList<String>();
            ArrayList<String> myArrayList = new ArrayList<String>();
            try {
                JSONArray json = new JSONArray(result);
                for (int i = 0; i < json.length(); i++) {
                    if (my_course_id.contains(json.getJSONObject(i).getString("key"))){
                        my_ordered_id.add((json.getJSONObject(i).getString("key")));
                        myArrayList.add(json.getJSONObject(i).getString("name"));
                        startCourse.setVisibility(View.VISIBLE);

                    }
                    else {
                        arrayList.add(json.getJSONObject(i).getString("name"));
                        all_course_id.add(json.getJSONObject(i).getString("key"));
                        addCourse.setVisibility(View.VISIBLE);
                    }
                }
                ArrayAdapter<String> adp = new ArrayAdapter<String> (getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,arrayList);
                courseList.setAdapter(adp);
                ArrayAdapter<String> adp2 = new ArrayAdapter<String> (getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, myArrayList);
                myCourseList.setAdapter(adp2);
            } catch (JSONException e){
                Log.e("JSON", "Invalid JSON string: " + result, e);
            }
        }
    }

    private class addCourseStudent extends AsyncTask<String, Integer, Double> {
        Spinner courseList = (Spinner) findViewById(R.id.allCourses);
        TextView status = (TextView) findViewById(R.id.cstatus);
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
                HttpPut post = new HttpPut(url + "/course/" + all_course_id.get(courseList.getSelectedItemPosition()));

                    HttpResponse response = client.execute(post);
                    Log.d("HTTP Post Response:", response.toString());
                    int code = response.getStatusLine().getStatusCode();
                    if (code == 200) {
                        responseText = "Course Successfully Added!!!";
                        my_course_id.add(all_course_id.get(courseList.getSelectedItemPosition()));
                    } else if (code == 403) {
                        responseText = "Username already taken, Please try again.";
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

    public void addCourse (View view){

        new addCourseStudent().execute(url);
       // setContentView(R.layout.fragment_student_profile);
        //new ProfileHttpAsyncTask().execute(url);
        //setContentView(R.layout.fragment_student_courses);
        new AllCoursesHttpAsyncTask().execute(url);

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

    public void delete(View view){

        new deleteAccount().execute(url);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
            }
        }, 5000);

    }

    public void logout(View view){

        Intent intent = new Intent(getApplicationContext(), Home.class);
        startActivity(intent);

    }

    public void viewCourse(View view){

        Spinner mycourseList = (Spinner) findViewById(R.id.myCourses);
        Intent intent = new Intent(getApplicationContext(), viewCourse.class);
        intent.putExtra("url",schoolUrl+"/course/"+ my_ordered_id.get(mycourseList.getSelectedItemPosition()));
        startActivity(intent);

    }

}
