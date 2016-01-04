package njurcak.studentevaltool;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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


public class viewCourse extends Activity {

    String url;
    int questionNumber;
    Handler handler = new Handler();
    JSONArray questions;
    TextView body;
    EditText answer;
    TextView status;
    int correct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_course);
        body = (TextView) findViewById(R.id.body);
        answer = (EditText) findViewById(R.id.answer);
        status = (TextView) findViewById(R.id.status);
        questionNumber = 0;
        correct = 0;
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        new getCourse().execute(url);
        new getQuestions().execute(url+"/question");
        /*try {
            body.setText(questions.getJSONObject(questionNumber).getString("body"));
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        //body.setText(questions.toString());
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

    private class getCourse extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        @Override
        protected void onPostExecute(String result){
            //Toast.makeText(getBaseContext(), "Recieved!", Toast.LENGTH_LONG).show();
            try {
                JSONObject course = new JSONObject(result);
                TextView courseName = (TextView) findViewById(R.id.courseName);
                courseName.setText( course.getString("name"));
            } catch (JSONException e){
                Log.e("JSON", "Invalid JSON string: " + result, e);
            }
        }
    }

    private class getQuestions extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        @Override
        protected void onPostExecute(String result){
            //Toast.makeText(getBaseContext(), "Recieved!", Toast.LENGTH_LONG).show();
            try {
                questions = new JSONArray(result);
                body.setText(questions.getJSONObject(questionNumber).getString("body"));
            } catch (JSONException e){
                Log.e("JSON", "Invalid JSON string: " + result, e);
            }
        }
    }

    public void onNext(View view){

        try {
            if (answer.getText().toString().equals(questions.getJSONObject(questionNumber).getString("answer"))){

                status.setText("Good work. That was the correct answer.");
                correct++;

            }

            else {

                status.setText("Sorry, the correct answer was: "+questions.getJSONObject(questionNumber).getString("answer"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                questionNumber++;

                if(questionNumber>=questions.length()){

                    status.setText("Course is completed.You answered "+correct+" questions out of "+questions.length()+" correctly.");
                    Button next = (Button) findViewById(R.id.next);
                    next.setEnabled(false);

                }

                else{

                    status.setText("");
                    answer.setText("");
                    try {
                        body.setText(questions.getJSONObject(questionNumber).getString("body"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        }, 5000);

    }

}
