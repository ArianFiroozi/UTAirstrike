package com.example.utairestrike.src.hardware.sensorWrapper;

import static android.hardware.Sensor.TYPE_GRAVITY;
import static android.hardware.Sensor.TYPE_GYROSCOPE;
import static android.hardware.Sensor.TYPE_MAGNETIC_FIELD;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorConnector implements SensorEventListener {
    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;
    private final Sensor mGyroscope;
    private final Sensor mGravimeter;
    private final Sensor mMagnetometer;
    private SensorListener sensorListener;

    public SensorConnector(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(TYPE_GYROSCOPE);
        mGravimeter = mSensorManager.getDefaultSensor(TYPE_GRAVITY);
        mMagnetometer = mSensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD);
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
        updateSensorListener(sensorEvent);
    }

    private void setSensorWrapper(SensorEvent event) {
        String name = event.sensor.getName();
        if (name.equals(mAccelerometer.getName())){
            SensorWrapper.Accelerometer.X = event.values[0];
            SensorWrapper.Accelerometer.Y = event.values[1];
            SensorWrapper.Accelerometer.Z = event.values[2];
        }
        if (name.equals(mMagnetometer.getName())){
            SensorWrapper.Magnetometer.X = event.values[0];
            SensorWrapper.Magnetometer.Y = event.values[1];
            SensorWrapper.Magnetometer.Z = event.values[2];
        }
        if (name.equals(mGravimeter.getName())){
            SensorWrapper.Gravimeter.X = event.values[0];
            SensorWrapper.Gravimeter.Y = event.values[1];
            SensorWrapper.Gravimeter.Z = event.values[2];
        }
        if (name.equals(mGyroscope.getName())){
            SensorWrapper.Gyroscope.X = event.values[0];
            SensorWrapper.Gyroscope.Y = event.values[1];
            SensorWrapper.Gyroscope.Z = event.values[2];
        }
    }

    private void updateSensorListener(SensorEvent event) {
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

    public void onAccuracyChanged(Sensor sensor, int acc) {}
}
