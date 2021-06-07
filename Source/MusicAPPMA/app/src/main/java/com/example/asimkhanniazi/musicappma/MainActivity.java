package com.example.asimkhanniazi.musicappma;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private static final String TAG = "MainActivity";
    ListView listview;
    Button btnPlayStop;
    Button btnPrev;
    Button btnNxt;
    TextView txtSongName;
    CardView cardView;

    ArrayList<SongObject> listOfContents;
    MyAdapter adapter;
    String path;
    static String absolutePath, songName;
    public static boolean playing = false;
    public static boolean paused = false;
    static int currentPositionofMusic = 0;
    int currentSongNumber;

    private SensorManager mSensorManager;
    private Sensor mLight;
    private float lux;

    boolean useSensor = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_songs);

        // If Android Marshmallow or above, then check if permission is granted
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermission();
        }
        else {
            initViews();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_UI);

        //if the song is playing on start i.e. managing state of app
        if (playing) {
            Log.d(TAG,"already playing");
            useSensor = true;

            //if it was playing then set status of player to pause with song name
            txtSongName.setText(songName);
            cardView.setVisibility(View.VISIBLE);
            btnPlayStop.setText("Pause");
        } else if(paused && !playing){
            Log.d(TAG,"already playing");
            //useSensor = true;

            //if it was paused then set status of player to play with song name
            txtSongName.setText(songName);
            cardView.setVisibility(View.VISIBLE);
            btnPlayStop.setText("Play");
        }
    }

    void initViews() {
        //initializing buttons
        btnPrev = (Button) findViewById(R.id.btnPrev);
        btnPlayStop = (Button) findViewById(R.id.btnPlayStop);
        btnNxt = (Button) findViewById(R.id.btnNxt);



        txtSongName = (TextView) findViewById(R.id.txtSongName);
        cardView = (CardView) findViewById(R.id.cardView);

        //listview n list of songs
        listview = (ListView) findViewById(R.id.listView);
        listOfContents = new ArrayList<>();

        //init sensors
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);


        //get external storage
        path = Environment.getExternalStorageDirectory().getAbsolutePath();

        //get the list of music files
        initList(path);

        //initializing the adapter and passing the context, list item and list of references of SongObject
        adapter = new MyAdapter(this, R.layout.list_item, listOfContents);
        listview.setAdapter(adapter);

        //handling events when user clicks on any music file in list view
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                currentSongNumber = position;
                Log.d(TAG, "onItemClick: Listview");

                //as the user wants to use the palyer from now on --> start using sensors
                useSensor = true;
                Log.d(TAG, "onItemClick: useSensor status "+ useSensor);
                //cardview with player is visible
                cardView.setVisibility(View.VISIBLE);

                //stop service if a song before is playing
                if (playing) {
                    Log.d(TAG,"Stopping already playing song");
                    Intent i = new Intent(MainActivity.this, MusicService.class);
                    stopService(i);
                }
                //status of player
                playing = true;
                paused = false;

                //getting absolute path of selected song when clicked on position
                SongObject songObject = listOfContents.get(position);
                //where absolute path will be accessed by service to play song
                absolutePath = songObject.getAbsolutePath();

                //Play the selected song by starting the service
                Intent start = new Intent(MainActivity.this, MusicService.class);
                //play song from start
                currentPositionofMusic = 0;
                start.putExtra("Position",currentPositionofMusic);
                Log.d(TAG, "onItemClick: "+currentPositionofMusic);
                startService(start);

                Log.d(TAG,"Playing song via click");

                //Get and set the name of song in the cardview player
                songName = listOfContents.get(position).getFileName();
                txtSongName.setText(songName);
                btnPlayStop.setText("Pause");
            }

        });

        //Handling events when button Prev is clicked in the player
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                useSensor = true;
                if (playing) {
                    Log.d(TAG,"Stopping already playing song");
                    Intent i = new Intent(MainActivity.this, MusicService.class);
                    stopService(i);
                }

                if (currentSongNumber==0){
                    currentSongNumber = listOfContents.size()-1;
                }
                else {
                    currentSongNumber--;
                }

                playing = true;
                paused= false;

                SongObject songObject = listOfContents.get(currentSongNumber);
                absolutePath = songObject.getAbsolutePath();

                //Play the selected song by starting the service
                Intent start = new Intent(MainActivity.this, MusicService.class);

                //play song from start
                currentPositionofMusic = 0;

                start.putExtra("Position",currentPositionofMusic);
                Log.d(TAG, "onItemClick: "+currentPositionofMusic);
                startService(start);
                songName = listOfContents.get(currentSongNumber).getFileName();
                txtSongName.setText(songName);
                btnPlayStop.setText("Pause");


            }
        });

        //Handling events when button Play/Pause is clicked in the player
        btnPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: sensor status"+ useSensor);
                if (playing) {

                    //as the user has manually started using music player so sensor status is false now i.e are off.
                    useSensor = false;
                    Toast.makeText(getApplicationContext(),"Manual Control enabled",Toast.LENGTH_SHORT).show();

                    //If song is playing and user clicks on Stop button
                    //Stop the song by calling stopService() and change boolean value
                    //text on button should be changed to 'Play'
                    Log.d(TAG,"Stopping Player by button");
                    playing = false;
                    paused = true;
                    btnPlayStop.setText("Play");
                    Intent i = new Intent(MainActivity.this, MusicService.class);
                    stopService(i);
                    Log.d(TAG, "onClick: pausing n pos is :"+currentPositionofMusic);
                } else if (!playing) {

                    Log.d(TAG,"Starting Player by button");

                    //If song is not playing and user clicks on Play button
                    //Start the song by calling startService() and change boolean value
                    //text on button should be changed to 'Stop'
                    playing = true;
                    paused = false;
                    //again using sensors
                    useSensor = true;
                    Toast.makeText(getApplicationContext(),"Sensors enabled",Toast.LENGTH_SHORT).show();

                    btnPlayStop.setText("Pause");
                    Intent i = new Intent(MainActivity.this, MusicService.class);
                    i.putExtra("Position",currentPositionofMusic);
                    startService(i);
                }
            }
        });

        //when button Play/Pause is clicked in the player
        btnNxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useSensor = true;
                if (playing) {
                    Log.d(TAG,"Stopping already playing song");
                    Intent i = new Intent(MainActivity.this, MusicService.class);
                    stopService(i);
                }

                if (currentSongNumber==listOfContents.size()-1){
                    currentSongNumber = 0;
                }
                else {
                    currentSongNumber++;
                }

                playing = true;
                paused = false;
                SongObject songObject = listOfContents.get(currentSongNumber);
                absolutePath = songObject.getAbsolutePath();

                //Play the selected song by starting the service
                Intent start = new Intent(MainActivity.this, MusicService.class);
                currentPositionofMusic = 0;
                start.putExtra("Position",currentPositionofMusic);
                Log.d(TAG, "onItemClick: "+currentPositionofMusic);
                startService(start);
                songName = listOfContents.get(currentSongNumber).getFileName();
                txtSongName.setText(songName);
                btnPlayStop.setText("Pause");


            }
        });

    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
            // The light sensor returns a single value.
            // Many sensors return 3 values, one for each axis.
            lux =  sensorEvent.values[0];
            //Log.d("Light Sensor: ", lux + "");
            // Do something with this sensor value.

            //upon the status of the sensors
            if(useSensor) {
                Log.d("Light Sensor: ", lux + "");
                Log.d(TAG, "onSensorChanged: "+useSensor);
                if (lux > 0 && !playing) {
                    playing = true;
                    paused = false;
                    btnPlayStop.setText("Pause");
                    Intent i = new Intent(MainActivity.this, MusicService.class);
                    i.putExtra("Position",currentPositionofMusic);
                    startService(i);
                } else if (lux < 3 && playing) {
                    playing = false;
                    paused = true;
                    btnPlayStop.setText("Play");
                    Intent i = new Intent(MainActivity.this, MusicService.class);
                    stopService(i);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }



    @Override
    protected void onPause() {
        super.onPause();

        //commented this because want to use even when app is not visible
        /*useSensor = false;
        mSensorManager.unregisterListener(this);*/
    }

    //Fetching .mp3 and .mp4 files from phone storage
    void initList(String path) {
        try {
            File file = new File(path);
            File[] filesArray = file.listFiles();
            String fileName;
            for (File file1 : filesArray) {
                if (file1.isDirectory()) {
                    initList(file1.getAbsolutePath());
                } else {
                    fileName = file1.getName();
                    if ((fileName.endsWith(".mp3")) || (fileName.endsWith(".mp4"))) {
                        listOfContents.add(new SongObject(file1.getName(), file1.getAbsolutePath()));
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Handling permissions for Android Marshmallow and above
    void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //if permission granted, initialize the views
            initViews();
        } else {
            //show the dialog requesting to grant permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }


    //helping reference: https://www.sitepoint.com/requesting-runtime-permissions-in-android-m-and-n/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initViews();
                } else {
                    //permission is denied --> this is the first time, when "never ask again" is not checked
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        finish();
                    }
                    //permission is denied (and never ask again is  checked)
                    else {
                        //shows the dialog describing the importance of permission, so that user should grant
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("You have forcefully denied Read storage permission.\n\nThis is necessary for the working of app." + "\n\n" + "Click on 'Grant' to grant permission")
                                //This will open app information where user can manually grant requested permission
                                .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", getPackageName(), null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                })
                                //close the app
                                .setNegativeButton("Don't", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                    }
                                });
                        builder.setCancelable(false);
                        builder.create().show();
                    }
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //stop using sensors only if destroyed
        useSensor = false;
        mSensorManager.unregisterListener(this);
    }
}



