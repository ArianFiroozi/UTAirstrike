package com.example.utairestrike.src.hardware.sensorWrapper;

import static android.hardware.Sensor.TYPE_GRAVITY;
import static android.hardware.Sensor.TYPE_GYROSCOPE;
import static android.hardware.Sensor.TYPE_MAGNETIC_FIELD;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import lombok.Getter;

public class SensorConnector implements SensorEventListener {
    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;
    private final Sensor mGyroscope;
    private final Sensor mGravimeter;
    private final Sensor mMagnetometer;
    @Getter
    private final SensorWrapper accelerometer;
    @Getter
    private final SensorWrapper gyroscope;
    @Getter
    private final SensorWrapper gravimeter;
    @Getter
    private final SensorWrapper magnetometer;
    private SensorListener sensorListener;

    public SensorConnector(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(TYPE_GYROSCOPE);
        mGravimeter = mSensorManager.getDefaultSensor(TYPE_GRAVITY);
        mMagnetometer = mSensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD);
        magnetometer = new SensorWrapper();
        gravimeter = new SensorWrapper();
        gyroscope = new SensorWrapper();
        accelerometer = new SensorWrapper();
    }

    public void setSensorUpdateListener(SensorListener listener) {
        sensorListener = listener;
    }

    protected void onResume() {
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGravimeter, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        mSensorManager.unregisterListener(this);
    }

    public void registerSensors() {
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGravimeter, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregisterSensors() {
        mSensorManager.unregisterListener(this);
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        var sensorName = sensorEvent.sensor.getName();
        System.out.println(sensorName + ": X: " + sensorEvent.values[0] +
                "; Y: " + sensorEvent.values[1] +
                "; Z: " + sensorEvent.values[2] + ";");
        setSensorWrapper(sensorEvent);

    }

    private void setSensorWrapper(SensorEvent event) {
        SensorWrapper sensor = nameToWrapper(event.sensor.getName());

        if (sensor != null)
            sensor.denoise(event.values[0], event.values[1], event.values[2]);

        if (sensorListener == null) return;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            sensorListener.onAccelerometerUpdate(event.values[0], event.values[1], event.values[2]);
        else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
            sensorListener.onGyroscopeUpdate(event.values[0], event.values[1], event.values[2]);
        else if (event.sensor.getType() == Sensor.TYPE_GRAVITY)
            sensorListener.onGravimeterUpdate(event.values[0], event.values[1], event.values[2]);
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            sensorListener.onMagnetometerUpdate(event.values[0], event.values[1], event.values[2]);

    }

    private SensorWrapper nameToWrapper(String name) {
        if (name.equals(mAccelerometer.getName()))
            return accelerometer;
        if (name.equals(mMagnetometer.getName()))
            return magnetometer;
        if (name.equals(mGravimeter.getName()))
            return gravimeter;
        if (name.equals(mGyroscope.getName()))
            return gyroscope;
        else
            return null;
    }

    public void onAccuracyChanged(Sensor sensor, int acc) {}
}
