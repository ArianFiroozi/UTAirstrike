package com.example.UTAirstrike.ui;

import android.os.Bundle;

import com.example.UTAirstrike.R;
import com.example.UTAirstrike.src.engine.GameEngine;
import com.example.UTAirstrike.src.hardware.AircraftSpeed;
import com.example.UTAirstrike.src.hardware.sensorWrapper.SensorConnector;
import com.example.UTAirstrike.src.hardware.sensorWrapper.SensorListener;
import com.example.UTAirstrike.src.model.Building;
import com.example.UTAirstrike.src.model.Enemy;
import com.example.UTAirstrike.src.model.GameObject;
import com.example.UTAirstrike.src.model.Player;
import com.example.UTAirstrike.src.util.Vector2D;
import com.google.android.material.snackbar.Snackbar;
import android.content.pm.ActivityInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.view.View;

import com.example.UTAirstrike.databinding.ActivityMainBinding;

import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import android.os.Handler;

public class MainActivity extends AppCompatActivity implements SensorListener {

    private ActivityMainBinding binding;
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
        engine = initEngine(screenWidth, screenHeight);
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
        sensorConnector.startCalibration();

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sensorConnector.startCalibration();
                Snackbar.make(view, "Calibrating... Please keep the phone still", Snackbar.LENGTH_SHORT)
                        .setAnchorView(R.id.fab)
                        .show();
            }
        });


        Button shootButton = findViewById(R.id.shoot);
        shootButton.bringToFront();
        shootButton.invalidate();
        shootButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                engine.shoot();
            }
        });

        textViewTimer = findViewById(R.id.textViewTimer);
        running = true;
        startTimer();
        startGame();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);


    }
    @Override
    public void onCalibrationDone()
    {
        Snackbar.make(binding.getRoot(), "Calibration Done", Snackbar.LENGTH_SHORT)
                .setAnchorView(R.id.fab)
                .show();
    }


    @NonNull
    private static
    GameEngine initEngine(int screenWidth, int screenHeight) {
        ArrayList<GameObject> gameObjects = new ArrayList<>();
        int BUILDING_SIZE=200;


        gameObjects.add(new Building(new Vector2D((float) BUILDING_SIZE/2,BUILDING_SIZE*2), new Vector2D(250, BUILDING_SIZE)));
        gameObjects.add(new Building(new Vector2D(screenWidth-BUILDING_SIZE/2,screenHeight-BUILDING_SIZE*2), new Vector2D(250, BUILDING_SIZE)));
        gameObjects.add(new Enemy(new Vector2D(screenWidth-BUILDING_SIZE/2,screenHeight-BUILDING_SIZE*3), new Vector2D(0,0), new Vector2D(70, 110), true));
        gameObjects.add(new Enemy(new Vector2D(screenWidth-BUILDING_SIZE/2,250), new Vector2D(0,0), new Vector2D(70, 110), true));
        gameObjects.add(new Enemy(new Vector2D(100,250), new Vector2D(0,0), new Vector2D(70, 110), false));


        var gameEngine = new GameEngine(new AircraftSpeed(0, 0, 0), new Vector2D(screenWidth, screenHeight));
        gameEngine.run(gameObjects);
        return gameEngine;
    }

    private void startTimer() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes = seconds / 60;
                int secs = seconds % 60;
                textViewTimer.setText(String.format("%02d:%02d", minutes, secs));

                if (running) {
                    engine.update();
                    seconds++;
                    handler.postDelayed(this, 1000); // delay 1 second
                    if(seconds == 1800) {
                        running = false;
                    }
                }
            }
        });
    }

    private void startGame() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (running) {
                    engine.update();
                    handler.postDelayed(this, 3);
                    if(GameEngine.isWon){
                        running=false;
                        showPopup(seconds, true);
                    }
                    else if(GameEngine.isLost){
                        running=false;
                        showPopup(seconds, false);
                    }
                }
            }
        });
    }

    private void showPopup(int timeSeconds ,boolean IsWin) {
        View popupView = getLayoutInflater().inflate(R.layout.popup_layout, null);

        TextView title = popupView.findViewById(R.id.popupText);
        TextView time = popupView.findViewById(R.id.popupTime);
        Button closeButton = popupView.findViewById(R.id.closeButton);
        if (IsWin){
            title.setText("Victory");
            time.setText("TIME: " + timeSeconds + " SECONDS");
        }
        else{
            title.setText("Game Over");
            time.setText(" ");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupView);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();

        closeButton.setOnClickListener(v -> System.exit(0));
        dialog.show();
    }

    @Override
    public void onAccelerometerUpdate(float x, float y, float z) {
    }

    @Override
    public void onGyroscopeUpdate(float x, float y, float z) {
        GameEngine.aircraftSpeedDelta = new AircraftSpeed(GameEngine.aircraftSpeedDelta.getVelocity().getX(),GameEngine.aircraftSpeedDelta.getVelocity().getY(),- z*2);
    }

    @Override
    public void onGravimeterUpdate(float x, float y, float z) {

    }
    @Override
    public void onRollPitch(float roll, float pitch)
    {
        GameEngine.aircraftSpeedDelta = new AircraftSpeed( -roll, pitch, GameEngine.aircraftSpeedDelta.getRotationDelta());
    }
    @Override
    public void onMagnetometerUpdate(float x, float y, float z) {
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