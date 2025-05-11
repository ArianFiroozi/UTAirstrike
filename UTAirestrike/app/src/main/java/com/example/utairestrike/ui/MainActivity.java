package com.example.utairestrike.ui;

import android.graphics.Typeface;
import android.os.Bundle;

import com.example.utairestrike.R;
import com.example.utairestrike.src.engine.GameEngine;
import com.example.utairestrike.src.hardware.AircraftSpeed;
import com.example.utairestrike.src.hardware.sensorWrapper.SensorConnector;
import com.example.utairestrike.src.hardware.sensorWrapper.SensorListener;
import com.example.utairestrike.src.model.GameObject;
import com.example.utairestrike.src.model.Player;
import com.example.utairestrike.src.utill.Vector2D;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.utairestrike.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.FrameLayout;

import java.util.ArrayList;
import android.os.Handler;

public class MainActivity extends AppCompatActivity implements SensorListener {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    TextView xGyro, yGyro, zGyro;
    TextView xAccel, yAccel, zAccel;
    TextView xMagnet, yMagnet, zMagnet;
    TextView xGrav, yGrav, zGrav;
    private GameEngine engine;
    private SensorConnector sensorConnector;

    private TextView textViewTimer;
    private int seconds = 0;
    private boolean running = false;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        ArrayList<GameObject> objects = new ArrayList<GameObject>();
        objects.add(new Player(new Vector2D(screenWidth, screenHeight)));
        engine = new GameEngine(new AircraftSpeed(0,0,0), new Vector2D(screenWidth, screenHeight));
        engine.run(objects);
        AircraftView aircraftView = new AircraftView(this, engine);
        BuildingView buildingView = new BuildingView(this, engine);
        EnemyView enemyView = new EnemyView(this,engine);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.getRoot().addView(aircraftView);
        binding.getRoot().addView(buildingView);
        binding.getRoot().addView(enemyView);

        setContentView(binding.getRoot());

        sensorConnector = new SensorConnector(this);
        sensorConnector.setSensorUpdateListener(this);

        setSensorDisplay();

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sensorConnector.startCalibration();
                Snackbar.make(view, "Calibrating... Please keep the phone still", Snackbar.LENGTH_SHORT)
                        .setAnchorView(R.id.fab)
                        .show();
            }
        });

        binding.shoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                engine.shoot();
//                Snackbar.make(view, "shot", Snackbar.LENGTH_SHORT)
//                        .setAnchorView(R.id.shoot)
//                        .show();
            }
        });

        textViewTimer = findViewById(R.id.textViewTimer);
        running = true;
        startTimer();

    }

    private void startTimer() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes = seconds / 60;
                int secs = seconds % 60;

                // Format as mm:ss
                textViewTimer.setText(String.format("%02d:%02d", minutes, secs));

                if (running) {
                    seconds++;
                    handler.postDelayed(this, 1000); // delay 1 second
                    if(seconds == 10){  //for test
                        showVictoryPopup(seconds);
                    }
                    if(seconds == 1800) {
                        running = false;
                    }
                }
            }
        });
    }

    private void showVictoryPopup(int timeSeconds) {
        View popupView = getLayoutInflater().inflate(R.layout.popup_layout, null);

        TextView title = popupView.findViewById(R.id.popupText);
        TextView time = popupView.findViewById(R.id.popupTime);
        Button closeButton = popupView.findViewById(R.id.closeButton);

        title.setText("Victory");
        time.setText("TIME: " + timeSeconds + " SECONDS");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupView);
        builder.setCancelable(false); // prevent closing by tapping outside

        AlertDialog dialog = builder.create();

        closeButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }


    private void setSensorDisplay() {
        xGyro = findViewById(R.id.XGyroscope);
        yGyro = findViewById(R.id.YGyroscope);
        zGyro = findViewById(R.id.ZGyroscope);

        xAccel = findViewById(R.id.XAccelerometer);
        yAccel = findViewById(R.id.YAccelerometer);
        zAccel = findViewById(R.id.ZAccelerometer);

        xMagnet = findViewById(R.id.XMagnet);
        yMagnet = findViewById(R.id.YMagnet);
        zMagnet = findViewById(R.id.ZMagnet);

        xGrav = findViewById(R.id.Xgravity);
        yGrav = findViewById(R.id.Ygravity);
        zGrav = findViewById(R.id.Zgravity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onAccelerometerUpdate(float x, float y, float z) {
        runOnUiThread(() -> {
            xAccel.setText("X: " + x);
            yAccel.setText("Y: " + y);
            zAccel.setText("Z: " + z);
        });
    }

    @Override
    public void onGyroscopeUpdate(float x, float y, float z) {
        Vector2D kir = new Vector2D(x*5, y*5);
        float kos = z*100;
        engine.player.setVelocity(kir);
        engine.player.setRotation(kos);
        GameEngine.aircraftSpeedDelta = new AircraftSpeed(x*5, y*5, z*10);
//        engine.player.update();
        engine.update();
        runOnUiThread(() -> {
            xGyro.setText("X: " + x);
            yGyro.setText("Y: " + y);
            zGyro.setText("Z: " + z);
        });
        System.out.println("kiram tu kargahi");
    }

    @Override
    public void onGravimeterUpdate(float x, float y, float z) {
        runOnUiThread(() -> {
            xGrav.setText("X: " + x);
            yGrav.setText("Y: " + y);
            zGrav.setText("Z: " + z);
        });
    }

    @Override
    public void onMagnetometerUpdate(float x, float y, float z) {
        runOnUiThread(() -> {
            xMagnet.setText("X: " + x);
            yMagnet.setText("Y: " + y);
            zMagnet.setText("Z: " + z);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorConnector.registerSensors();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorConnector.unregisterSensors();
    }



}