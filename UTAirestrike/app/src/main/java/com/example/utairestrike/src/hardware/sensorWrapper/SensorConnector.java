package com.example.utairestrike.src.hardware.sensorWrapper;

import static android.hardware.Sensor.TYPE_GRAVITY;
import static android.hardware.Sensor.TYPE_GYROSCOPE_UNCALIBRATED;
import static android.hardware.Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED;
import static android.hardware.Sensor.TYPE_ACCELEROMETER_UNCALIBRATED;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;

import java.util.Arrays;

public class SensorConnector implements SensorEventListener {
    private final int GYROSCOPE_TYPE = TYPE_GYROSCOPE_UNCALIBRATED;
    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;
    private final Sensor mGyroscope;
    private final Sensor mGravimeter;
    private final Sensor mMagnetometer;
    private SensorListener sensorListener;
    private float sampleRate;
    private float accSampleRate;
    private float gyroSampleRate;
    private float magnetSampleRate;
    private final long[] accTimestamps = new long[CALIBRATION_SAMPLE_COUNT];
    private final long[] gyroTimestamps = new long[CALIBRATION_SAMPLE_COUNT];
    private final long[] magnetTimestamps = new long[CALIBRATION_SAMPLE_COUNT];
    private final float filterBeta = 0.004f;

    private static final float ALPHA = 0.1f;

    private final float[] filteredAccel = new float[3];
    private final float[] filteredGyro = new float[3];
    private final float[] filteredMagnet = new float[3];

    private MadgwickAHRS mMadgwick;
    private final float[] accel = new float[3];
    private final float[] gyro = new float[3];
    private final float[] magnet = new float[3];
    private static final int CALIBRATION_SAMPLE_COUNT = 100;
    private int calibrationSampleIndex = 0;
    private boolean isCalibrating = false;

    private final float[] accelBias = new float[3];
    private final float[] gyroBias = new float[3];
    private final float[] magnetBias = new float[3];
    private final float[][] accelSamples = new float[CALIBRATION_SAMPLE_COUNT][3];
    private final float[][] gyroSamples = new float[CALIBRATION_SAMPLE_COUNT][3];
    private final float[][] magnetSamples = new float[CALIBRATION_SAMPLE_COUNT][3];

    public SensorConnector(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mAccelerometer = mSensorManager.getDefaultSensor(TYPE_ACCELEROMETER_UNCALIBRATED);
        } else {
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        mGyroscope = mSensorManager.getDefaultSensor(GYROSCOPE_TYPE);
        mGravimeter = mSensorManager.getDefaultSensor(TYPE_GRAVITY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mMagnetometer = mSensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD_UNCALIBRATED);
        } else {
            mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }

        sampleRate = magnetSampleRate = accSampleRate = gyroSampleRate = 0f;
        mMadgwick = new MadgwickAHRS(1);
    }

    private void lowPassFilter(float[] input, float[] output) {
        if (output == null) return;
        for (int i = 0; i < Math.min(input.length, output.length); i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
    }

    public void setSensorUpdateListener(SensorListener listener) {
        sensorListener = listener;
    }

    public void startCalibration() {
        calibrationSampleIndex = 0;
        isCalibrating = true;
    }

    protected void onResume() {
        registerSensors();
    }

    protected void onPause() {
        unregisterSensors();
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
        float[] values = sensorEvent.values.clone();
        int type = sensorEvent.sensor.getType();

        if (isCalibrating && calibrationSampleIndex < CALIBRATION_SAMPLE_COUNT) {
            long currentTime = System.nanoTime();
            if (type == TYPE_ACCELEROMETER_UNCALIBRATED || type == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(values, 0, accelSamples[calibrationSampleIndex], 0, 3);
                accTimestamps[calibrationSampleIndex] = currentTime;
            } else if (type == GYROSCOPE_TYPE) {
                System.arraycopy(values, 0, gyroSamples[calibrationSampleIndex], 0, 3);
                gyroTimestamps[calibrationSampleIndex] = currentTime;
            } else if (type == TYPE_MAGNETIC_FIELD_UNCALIBRATED || type == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(values, 0, magnetSamples[calibrationSampleIndex], 0, 3);
                magnetTimestamps[calibrationSampleIndex] = currentTime;
            }

            if (type == TYPE_MAGNETIC_FIELD_UNCALIBRATED || type == Sensor.TYPE_MAGNETIC_FIELD) {
                calibrationSampleIndex++;
                if (calibrationSampleIndex >= CALIBRATION_SAMPLE_COUNT) {
                    computeBiases();
                    computeSampleRates();
                    isCalibrating = false;
                    mMadgwick = new MadgwickAHRS(1000 / Math.min(accSampleRate, Math.min(gyroSampleRate, 1f)), filterBeta);
                    System.out.println("Callibration done");
                }
            }
            return;
        }

        if (type == TYPE_ACCELEROMETER_UNCALIBRATED || type == Sensor.TYPE_ACCELEROMETER) {
            values[0] -= accelBias[0];
            values[1] -= accelBias[1];
            values[2] -= accelBias[2];
            lowPassFilter(values, filteredAccel);
            System.arraycopy(filteredAccel, 0, accel, 0, 3);

        } else if (type == GYROSCOPE_TYPE) {
            values[0] -= gyroBias[0];
            values[1] -= gyroBias[1];
            values[2] -= gyroBias[2];
            lowPassFilter(values, filteredGyro);
            System.arraycopy(filteredGyro, 0, gyro, 0, 3);

        } else if (type == TYPE_MAGNETIC_FIELD_UNCALIBRATED || type == Sensor.TYPE_MAGNETIC_FIELD) {
            values[0] -= magnetBias[0];
            values[1] -= magnetBias[1];
            values[2] -= magnetBias[2];
            lowPassFilter(values, filteredMagnet);
            System.arraycopy(filteredMagnet, 0, magnet, 0, 3);
        }

        float[] quat = new float[4];
        if (accel != null && gyro != null && magnet != null) {
            System.out.println(Arrays.toString(gyro));
            mMadgwick.no_filter(gyro[0],gyro[1],gyro[2]);
            var euler = mMadgwick.getEulerAngles();
            System.out.println("euler Ang "+Arrays.toString(euler));
        }

        values[0] = roundTo3Decimals(values[0]);
        values[1] = roundTo3Decimals(values[1]);
        values[2] = roundTo3Decimals(values[2]);
        quat[0] = roundTo3Decimals(quat[0]);
        quat[1] = roundTo3Decimals(quat[1]);
        quat[2] = roundTo3Decimals(quat[2]);
        quat[3] = roundTo3Decimals(quat[3]);

        updateSensorListenerValues(sensorEvent.sensor, values, quat);
    }

    private void updateSensorListenerValues(Sensor sensor, float[] values, float[] q) {
        if (sensorListener == null) return;

        int type = sensor.getType();
        if (type == TYPE_ACCELEROMETER_UNCALIBRATED || type == Sensor.TYPE_ACCELEROMETER)
            sensorListener.onAccelerometerUpdate(q[1], q[2], q[3]);
        else if (type == GYROSCOPE_TYPE)
            sensorListener.onGyroscopeUpdate(values[0], values[1], values[2]);
        else if (type == TYPE_GRAVITY)
            sensorListener.onGravimeterUpdate(values[0], values[1], values[2]);
        else if (type == TYPE_MAGNETIC_FIELD_UNCALIBRATED || type == Sensor.TYPE_MAGNETIC_FIELD)
            sensorListener.onMagnetometerUpdate(values[0], values[1], values[2]);
    }

    private float roundTo3Decimals(float value) {
        return Math.round(value * 1000f) / 1000f;
    }

    private void computeSampleRates() {
        accSampleRate = computeRateFromTimestamps(accTimestamps);
        gyroSampleRate = computeRateFromTimestamps(gyroTimestamps);
        magnetSampleRate = computeRateFromTimestamps(magnetTimestamps);
    }

    private float computeRateFromTimestamps(long[] timestamps) {
        if (timestamps.length < 2) return 0;
        long totalInterval = 0;
        for (int i = 1; i < timestamps.length; i++) {
            totalInterval += (timestamps[i] - timestamps[i - 1]);
        }
        float averageInterval = totalInterval / (float) (timestamps.length - 1);
        return 1_000_000_000f / averageInterval;
    }

    private void computeBiases() {
        for (int i = 0; i < 3; i++) {
            accelBias[i] = gyroBias[i] = magnetBias[i] = 0f;
            for (int j = 0; j < CALIBRATION_SAMPLE_COUNT; j++) {
                accelBias[i] += accelSamples[j][i];
                gyroBias[i] += gyroSamples[j][i];
                magnetBias[i] += magnetSamples[j][i];
            }
            accelBias[i] /= CALIBRATION_SAMPLE_COUNT;
            gyroBias[i] /= CALIBRATION_SAMPLE_COUNT;
            magnetBias[i] /= CALIBRATION_SAMPLE_COUNT;
        }
    }

    public void onAccuracyChanged(Sensor sensor, int acc) {}
}
