package com.example.studentattendance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MainActivity extends AppCompatActivity {
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 1;
    LocationTrack locationTrack;
    public String latitude;
    public String longitude;
    Attendance attendanceContract;
    Handler mHandler;
    RecyclerView rv;
    private ProgressDialog pDialog;
    ProfileAdapter rvAdapter;
    String[] profileDataArray;
    private static final String TAG = "MainActivity";
    private static final String FINAL_URL = "http://msitis-iiith.appspot.com/api/profile/ag5ifm1zaXRpcy1paWl0aHIUCxIHU3R1ZGVudBiAgIDAyN6VCgw";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        attendanceContract = new Attendance(getApplicationContext());
        // notificationHelper = new NotificationHelper(MainActivity.this);

        rv = findViewById(R.id.profile_rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager rvManager = new LinearLayoutManager(this);
        rv.setLayoutManager(rvManager);

        TextClock simpleDigitalClock = (TextClock) findViewById(R.id.simpleTextClock);

        getJson();
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


        locationTrack = new LocationTrack(MainActivity.this);


        if (locationTrack.canGetLocation()) {
            longitude = locationTrack.getLongitude()+"";
            latitude = locationTrack.getLatitude()+"";
        } else {
            locationTrack.showSettingsAlert();
        }


        String date = new SimpleDateFormat("dd-MM-yyyy", new Locale("en", "IN")).format(Calendar.getInstance().getTime());
        TextView d=(TextView) findViewById(R.id.date);
        d.setText(date);

        /*
        Button bb=(Button) findViewById(R.id.mark);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        Date now = calendar.getTime();
        SimpleDateFormat dater = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        final String currentTime = dater.format(now);
        bb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int flag=0;
                if (currentTime.compareTo("08:50:00")>0 && currentTime.compareTo("09:10:00")<0){
                    attendanceContract.update(1,latitude,longitude);
                    flag=1;
                }
                else if (currentTime.compareTo("10:50:00")>0 && currentTime.compareTo("11:10:00")<0){
                    attendanceContract.update(2,latitude,longitude);
                    flag=1;
                }
                else if (currentTime.compareTo("13:50:00")>0 && currentTime.compareTo("14:10:00")<0){

                    attendanceContract.update(3,latitude,longitude);
                    flag=1;
                }
                String message;
                if(flag==1) {
                    message="Checked in Successfully at "+currentTime;
                    //Toast.makeText(getApplicationContext(), "Checked in successfully at " + currentTime, Toast.LENGTH_SHORT).show();
                }
                else {
                    message="Check in not available at "+currentTime;
                    // Toast.makeText(getApplicationContext(), "Check in not available at " + currentTime, Toast.LENGTH_SHORT).show();
                }
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setTitle("Attendance Status");
                // set dialog message
                alertDialogBuilder
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();

                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });
        */
        this.mHandler = new Handler();
        this.mHandler.postDelayed(m_Runnable,5000);

    }



    public void getJson() {
        URL myProfileUrl = NetworkUtils.buildUrl(FINAL_URL);
        new getResults().execute(myProfileUrl);
    }
    void startTimer(String time1, String time2) {
        try{
            SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            Date d1 = date.parse(time2);
            Date d2 = date.parse(time1);

            final TextView hh = findViewById(R.id.hh);
            final TextView mm = findViewById(R.id.mm);
            final TextView ss = findViewById(R.id.ss);
            long millis = d1.getTime() - d2.getTime();
            CountDownTimer countDownTimer = new CountDownTimer(millis, 1000) {

                public void onTick(long millisUntilFinished) {
                    long t =  millisUntilFinished / 1000;
                    DecimalFormat formatter = new DecimalFormat("00");
                    String hhFormatted = formatter.format(t/3600);
                    String mmFormatted = formatter.format((t%3600)/60);
                    String ssFormatted = formatter.format((t%3600)%60);
                    hh.setText(hhFormatted+":");
                    mm.setText(mmFormatted+":");
                    ss.setText(ssFormatted);
                }

                public void onFinish() {
                    hh.setText("00:");
                    mm.setText("00:");
                    ss.setText("00");
                }
            }.start();
        }
        catch (ParseException e){
            e.printStackTrace();
        }

    }

    public class getResults extends AsyncTask<URL, Void, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            try {
                String searchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                return searchResults;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pDialog.isShowing())
                pDialog.dismiss();
            if (s != null && !s.equals("")) {
                try {
                    JSONObject file = new JSONObject(s);
                    JSONArray array = file.getJSONArray("data");
                    JSONObject data = array.getJSONObject(0);


                    String application_number = data.getString("application_number");
                    String name = data.getString("student_fullname");
                    String email = data.getString("student_email");
                    String roll_number = data.getString("roll_number");


                    String[] temp = {"Name: " + name, "\nEmail: " + email, "\nRoll number: " + roll_number};
                    rvAdapter = new ProfileAdapter(temp);
                    rv.setAdapter(rvAdapter);
                    profileDataArray = temp;
                } catch (JSONException  e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int clickedId = item.getItemId();
        if (clickedId == R.id.menu_id)
        {
            Intent intent = new Intent(this,CourseActivity.class);
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }
    private final Runnable m_Runnable = new Runnable() {
        boolean flag = true;

        public void run()
        {
            Button mark = (Button) findViewById(R.id.mark);
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
            Date now = calendar.getTime();
            SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            final String currentTime = date.format(now);
            System.out.println("charan");
            attendanceContract.insert(1, 0.25,"0","0");
            attendanceContract.insert(2, 0.25,"0","0");
            attendanceContract.insert(3, 0.50,"0","0");

            if (currentTime.compareTo("08:50:00") > 0 && currentTime.compareTo("09:10:00") < 0) {
                if (flag && currentTime.compareTo("08:55:00") < 0) {
                    addNotification("Mark for Session1 before 09:10 AM");
                    flag = false;
                }
                startTimer(currentTime, "09:10:00");
            } else if (currentTime.compareTo("10:20:00") > 0 && currentTime.compareTo("11:10:00") < 0) {
                if (flag) {
                    addNotification("Mark for Session2 before 11:10 AM");
                    flag = false;
                }
                startTimer(currentTime, "11:10:00");
            } else if (currentTime.compareTo("13:50:00") > 0 && currentTime.compareTo("18:20:00") < 0) {
                if (flag) {
                    addNotification("Mark for Session3 before 06:20 PM");
                    flag = false;
                }
                startTimer(currentTime, "18:20:00");
            } else {
                flag = true;
            }

            mark.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int i = 0;
                    locationTrack = new LocationTrack(MainActivity.this);
                    if (currentTime.compareTo("08:50:00") > 0 && currentTime.compareTo("09:10:00") < 0) {
                        i = 1;
                    } else if (currentTime.compareTo("10:20:00") > 0 && currentTime.compareTo("11:10:00") < 0) {
                        i = 2;
                    } else if (currentTime.compareTo("12:34:00") > 0 && currentTime.compareTo("18:20:00") < 0) {
                        i = 3;
                    }
                    String msg="";
                    if (i != 0) {

                        if (locationTrack.canGetLocation()) {
                            System.out.println(locationTrack.getLocation());

                            double longitude = locationTrack.getLongitude();
                            double latitude = locationTrack.getLatitude();

                            attendanceContract.update(i, latitude + "", longitude + "");
                            msg="Checked in successfully at latitude: "+latitude+" ,longitude:"+longitude;

                            // Toast.makeText(MainActivity.this, "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
                        } else {

                            locationTrack.showSettingsAlert();
                        }
                    }
                    else
                    {
                        msg="Check in not available at this time";
                    }
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setTitle("Attendance Status");
                    // set dialog message
                    alertDialogBuilder
                            .setMessage(msg)
                            .setCancelable(false)
                            .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();

                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
            MainActivity.this.mHandler.postDelayed(m_Runnable,5000);

        }


    };


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTrack.stopListener();
    }
    public void checkAttendance(View view){
        Intent intent = new Intent(this, CheckAttendance.class);
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void goToNotificationSettings(String channel) {
        Intent i = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
        i.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        i.putExtra(Settings.EXTRA_CHANNEL_ID, channel);
        startActivity(i);
    }

    private void addNotification(String message)
    {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notifi)
                        .setContentTitle("Checkin for Attendance")   //this is the title of notification
                        .setContentText(message);   //this is the message showed in notification

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}
