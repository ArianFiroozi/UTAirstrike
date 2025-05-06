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
    private final float sampleRate= 0.125f;
    private final float filterBeta = 1f;
    private MadgwickAHRS mMadgwick = new MadgwickAHRS(sampleRate , filterBeta);
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
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(TYPE_GYROSCOPE);
        mGravimeter = mSensorManager.getDefaultSensor(TYPE_GRAVITY);
        mMagnetometer = mSensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD);
    }

    public void setSensorUpdateListener(SensorListener listener) {
        sensorListener = listener;
    }
    public void startCalibration() {
        System.out.println("start calibration called " );
        calibrationSampleIndex = 0;
        isCalibrating = true;
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
        float[] values = sensorEvent.values.clone();
        int type = sensorEvent.sensor.getType();

        if (isCalibrating && calibrationSampleIndex < CALIBRATION_SAMPLE_COUNT) {
            System.out.println("Calibrating...");
            if (type == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(values, 0, accelSamples[calibrationSampleIndex], 0, 3);
            } else if (type == Sensor.TYPE_GYROSCOPE) {
                System.arraycopy(values, 0, gyroSamples[calibrationSampleIndex], 0, 3);
            } else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(values, 0, magnetSamples[calibrationSampleIndex], 0, 3);
            }

            if (type == Sensor.TYPE_MAGNETIC_FIELD) {
                calibrationSampleIndex++;
                if (calibrationSampleIndex >= CALIBRATION_SAMPLE_COUNT) {
                    computeBiases();
                    isCalibrating = false;
                    System.out.println("Calibration complete.");
                }
            }
            return;
        }

        // Apply bias correction
        if (type == Sensor.TYPE_ACCELEROMETER) {
            values[0] -= accelBias[0];
            values[1] -= accelBias[1];
            values[2] -= accelBias[2];
        } else if (type == Sensor.TYPE_GYROSCOPE) {
            values[0] -= gyroBias[0];
            values[1] -= gyroBias[1];
            values[2] -= gyroBias[2];
        } else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
            values[0] -= magnetBias[0];
            values[1] -= magnetBias[1];
            values[2] -= magnetBias[2];
        }
        if (type == Sensor.TYPE_ACCELEROMETER)
            System.arraycopy(values, 0, accel, 0, 3);
        else if (type == Sensor.TYPE_GYROSCOPE)
            System.arraycopy(values, 0, gyro, 0, 3);
        else if (type == Sensor.TYPE_MAGNETIC_FIELD)
            System.arraycopy(values, 0, magnet, 0, 3);

// When all three are available, update the filter:
        if (accel != null && gyro != null && magnet != null) {
            mMadgwick.update(
                    gyro[0], gyro[1], gyro[2],
                    accel[0], accel[1], accel[2],
                    magnet[0], magnet[1], magnet[2]
            );
            float[] quat = mMadgwick.getQuaternion();
            System.out.println("Quaternion: q0=" + quat[0] +
                    ", q1=" + quat[1] +
                    ", q2=" + quat[2] +
                    ", q3=" + quat[3]);

            System.out.println("ACC: " + accel[0] + ", " + accel[1] + ", " + accel[2]);
            System.out.println("GYRO: " + gyro[0] + ", " + gyro[1] + ", " + gyro[2]);
            System.out.println("MAG: " + magnet[0] + ", " + magnet[1] + ", " + magnet[2]);

            // Convert to Euler angles if needed and pass to listener
        }
        System.out.println(sensorEvent.sensor.getName() + ": X: " + values[0] +
                "; Y: " + values[1] +
                "; Z: " + values[2] + ";");

        updateSensorListenerValues(sensorEvent.sensor, values);
    }
    private void updateSensorListenerValues(Sensor sensor, float[] values) {
        if (sensorListener == null) return;

        int type = sensor.getType();
        if (type == Sensor.TYPE_ACCELEROMETER)
            sensorListener.onAccelerometerUpdate(values[0], values[1], values[2]);
        else if (type == Sensor.TYPE_GYROSCOPE)
            sensorListener.onGyroscopeUpdate(values[0], values[1], values[2]);
        else if (type == Sensor.TYPE_GRAVITY)
            sensorListener.onGravimeterUpdate(values[0], values[1], values[2]);
        else if (type == Sensor.TYPE_MAGNETIC_FIELD)
            sensorListener.onMagnetometerUpdate(values[0], values[1], values[2]);

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
