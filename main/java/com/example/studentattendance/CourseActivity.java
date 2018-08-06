package com.example.studentattendance;




import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;




import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class CourseActivity extends AppCompatActivity {

    RecyclerView courseRV;
    CourseAdapter cAdapter;
    String[] grades;
    private ProgressDialog pDialog;

    TextView courseDetailsTextView;
    public static final String TAG = "CourseActivity";
    public static final String urlString = "http://msitis-iiith.appspot.com/api/course/ag5ifm1zaXRpcy1paWl0aHIUCxIHU3R1ZGVudBiAgIDAyN6VCgw";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        courseDetailsTextView = findViewById(R.id.courseDetailsView);
        courseRV = (RecyclerView) findViewById(R.id.course_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        courseRV.setLayoutManager(layoutManager);
        getJSON();
    }

    public void getJSON() {
        URL courseURL = NetworkUtils.buildUrl(urlString);
        Log.i(TAG, courseURL.toString());
        new getCourseDetailsJSON().execute(courseURL);
    }


    public class getCourseDetailsJSON extends AsyncTask<URL, Void, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(CourseActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(URL... urls) {
            URL url = urls[0];
            String res;
            try {
                res = NetworkUtils.getResponseFromHttpUrl(url);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            double numerator=0,cgpa=0;

            if (pDialog.isShowing())
                pDialog.dismiss();

            try {
                JSONObject obj = new JSONObject(s);
                JSONArray dataArray = obj.getJSONArray("data");
                String[] courseID = new String[dataArray.length()];
                String[] courseName = new String[dataArray.length()];
                String[] grade = new String[dataArray.length()];
                String[] mentorName = new String[dataArray.length()];
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject arrayObj1 = dataArray.getJSONObject(i);
                    if(arrayObj1.getString("status").equals("Pass")) {
                        courseID[i] = arrayObj1.getString("course_id");
                        courseName[i] = arrayObj1.getString("course_name");
                        grade[i] = arrayObj1.getString("grade");
                        mentorName[i] = arrayObj1.getString("mentor_name");
                        numerator += calculateCGPA(grade[i], courseName[i]);
                        System.out.println(numerator);
                    }
                }
                System.out.println(Arrays.toString(courseID));
                System.out.println(Arrays.toString(courseName));
                System.out.println(Arrays.toString(grade));
                System.out.println(Arrays.toString(mentorName));
                cAdapter = new CourseAdapter(courseID,courseName,grade,mentorName);
                courseRV.setAdapter(cAdapter);
                cgpa = numerator/29;
                String cgp=cgpa+"";
                courseDetailsTextView.append("CGPA: "+cgp);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public double calculateCGPA(String grade, String course_name){
        double total=0;
        double grade_points=0;
        if(grade.equals("Ex"))
            grade_points=10;
        else if(grade.equals("A+"))
            grade_points=9.5;
        else if(grade.equals("A"))
            grade_points=9;
        else if(grade.equals("B+"))
            grade_points=8.5;
        else if(grade.equals("B"))
            grade_points=8;
        else if(grade.equals("C"))
            grade_points=7;
        else
            grade_points=0;

        double credits=0;
        switch (course_name){
            case "Digital Literacy":
                credits=1;
                break;
            case "Computational Thinking":
                credits=2;
                break;
            case "CSPP-1":
                credits=4;
                break;
            case "CSPP-2":
                credits=4;
                break;
            case "ADS-1":
                credits=4;
                break;
            case "ADS-2":
                credits=4;
                break;
            case "DBMS":
                credits=2;
                break;
            case "Computer Network Foundation":
                credits=2;
                break;
            case "Cyber Security":
                credits=2;
                break;
            case "Introduction to Computer Systems":
                credits=4;
                break;
            case "Software Engineering Foundations":
                credits=4;
                break;
            case "Web Programming":
                credits=3;
                break;
            default:
                credits=0;
                break;
        }
        total = grade_points * credits;
        return total;
    }
}

