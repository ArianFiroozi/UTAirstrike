package com.example.UTAirstrike.src.hardware.sensorWrapper;

public class MadgwickAHRS {

    private float samplePeriod;
    private float beta;
    private float[] quaternion;
    private long prevTime;

    /**
     * Gets the sample period.
     *
     * @return Sample Period
     */
    public float getSamplePeriod() {
        return samplePeriod;
    }

    public void setSamplePeriod(float samplePeriod) {
        this.samplePeriod = samplePeriod;
    }

    /**
     * Gets the sample algorithm gain beta.
     *
     * @return Algorithm gain beta
     */
    public float getBeta() {
        return beta;
    }


    public void setBeta(float beta) {
        this.beta = beta;
    }

    /**
     * Gets the quaternion output.
     *
     * @return Quaternion output
     */
    public float[] getQuaternion() {
        return quaternion;
    }
    public float[] getEulerAngles() {
        float q0 = quaternion[0];
        float q1 = quaternion[1];
        float q2 = quaternion[2];
        float q3 = quaternion[3];

        // Roll (x-axis rotation)
        float sinr_cosp = 2f * (q0 * q1 + q2 * q3);
        float cosr_cosp = 1f - 2f * (q1 * q1 + q2 * q2);
        float roll = (float) Math.atan2(sinr_cosp, cosr_cosp);

        // Pitch (y-axis rotation)
        float sinp = 2f * (q0 * q2 - q3 * q1);
        float pitch;
        if (Math.abs(sinp) >= 1)
            pitch = (float) (Math.copySign(Math.PI / 2, sinp)); // use 90 degrees if out of range
        else
            pitch = (float) Math.asin(sinp);

        // Yaw (z-axis rotation)
        float siny_cosp = 2f * (q0 * q3 + q1 * q2);
        float cosy_cosp = 1f - 2f * (q2 * q2 + q3 * q3);
        float yaw = (float) Math.atan2(siny_cosp, cosy_cosp);

        return new float[]{roll, pitch, yaw};
    }

    /**
     * Initializes a new instance of the {@link MadgwickAHRS} class.
     *
     * @param samplePeriod Sample period.
     */
    public MadgwickAHRS(float samplePeriod) {
        this(samplePeriod, 1f);
        prevTime = System.nanoTime();
    }

    /**
     * Initializes a new instance of the {@link MadgwickAHRS} class.
     *
     * @param samplePeriod Sample period.
     * @param beta         Algorithm gain beta.
     */
    public MadgwickAHRS(float samplePeriod, float beta) {
        this.samplePeriod = samplePeriod;
        this.beta = beta;
        this.quaternion = new float[]{1f, 0f, 0f, 0f};
        prevTime = System.nanoTime();
    }

    /**
     * Algorithm AHRS update method. Requires only gyroscope and accelerometer
     * data.
     * <p>
     * Optimised for minimal arithmetic. <br>
     * Total ±: 160 <br>
     * Total *: 172 <br>
     * Total /: 5 <br>
     * Total sqrt: 5 <br>
     *
     * @param gx Gyroscope x axis measurement in radians/s.
     * @param gy Gyroscope y axis measurement in radians/s.
     * @param gz Gyroscope z axis measurement in radians/s.
     * @param ax Accelerometer x axis measurement in any calibrated units.
     * @param ay Accelerometer y axis measurement in any calibrated units.
     * @param az Accelerometer z axis measurement in any calibrated units.
     * @param mx Magnetometer x axis measurement in any calibrated units.
     * @param my Magnetometer y axis measurement in any calibrated units.
     * @param mz Magnetometer z axis measurement in any calibrated units.
     */
    public void update(float gx, float gy, float gz, float ax, float ay,
                       float az, float mx, float my, float mz) {

        float q1 = quaternion[0], q2 = quaternion[1], q3 = quaternion[2], q4 = quaternion[3]; // short
        // name
        // local
        // variable
        // for
        // readability
        float norm;
        float hx, hy, _2bx, _2bz;
        float s1, s2, s3, s4;
        float qDot1, qDot2, qDot3, qDot4;

        // Auxiliary variables to avoid repeated arithmetic
        float _2q1mx;
        float _2q1my;
        float _2q1mz;
        float _2q2mx;
        float _4bx;
        float _4bz;
        float _2q1 = 2f * q1;
        float _2q2 = 2f * q2;
        float _2q3 = 2f * q3;
        float _2q4 = 2f * q4;
        float _2q1q3 = 2f * q1 * q3;
        float _2q3q4 = 2f * q3 * q4;
        float q1q1 = q1 * q1;
        float q1q2 = q1 * q2;
        float q1q3 = q1 * q3;
        float q1q4 = q1 * q4;
        float q2q2 = q2 * q2;
        float q2q3 = q2 * q3;
        float q2q4 = q2 * q4;
        float q3q3 = q3 * q3;
        float q3q4 = q3 * q4;
        float q4q4 = q4 * q4;

        // Normalise accelerometer measurement
        norm = (float) Math.sqrt(ax * ax + ay * ay + az * az);
        if (norm == 0f)
            return; // handle NaN
        norm = 1 / norm; // use reciprocal for division
        ax *= norm;
        ay *= norm;
        az *= norm;

        // Normalise magnetometer measurement
        norm = (float) Math.sqrt(mx * mx + my * my + mz * mz);
        if (norm == 0f)
            return; // handle NaN
        norm = 1 / norm; // use reciprocal for division
        mx *= norm;
        my *= norm;
        mz *= norm;

        // Reference direction of Earth's magnetic field
        _2q1mx = 2f * q1 * mx;
        _2q1my = 2f * q1 * my;
        _2q1mz = 2f * q1 * mz;
        _2q2mx = 2f * q2 * mx;
        hx = mx * q1q1 - _2q1my * q4 + _2q1mz * q3 + mx * q2q2 + _2q2 * my * q3
                + _2q2 * mz * q4 - mx * q3q3 - mx * q4q4;
        hy = _2q1mx * q4 + my * q1q1 - _2q1mz * q2 + _2q2mx * q3 - my * q2q2
                + my * q3q3 + _2q3 * mz * q4 - my * q4q4;
        _2bx = (float) Math.sqrt(hx * hx + hy * hy);
        _2bz = -_2q1mx * q3 + _2q1my * q2 + mz * q1q1 + _2q2mx * q4 - mz * q2q2
                + _2q3 * my * q4 - mz * q3q3 + mz * q4q4;
        _4bx = 2f * _2bx;
        _4bz = 2f * _2bz;

        // Gradient decent algorithm corrective step
        s1 = -_2q3 * (2f * q2q4 - _2q1q3 - ax) + _2q2
                * (2f * q1q2 + _2q3q4 - ay) - _2bz * q3
                * (_2bx * (0.5f - q3q3 - q4q4) + _2bz * (q2q4 - q1q3) - mx)
                + (-_2bx * q4 + _2bz * q2)
                * (_2bx * (q2q3 - q1q4) + _2bz * (q1q2 + q3q4) - my) + _2bx
                * q3
                * (_2bx * (q1q3 + q2q4) + _2bz * (0.5f - q2q2 - q3q3) - mz);
        s2 = _2q4 * (2f * q2q4 - _2q1q3 - ax) + _2q1
                * (2f * q1q2 + _2q3q4 - ay) - 4f * q2
                * (1 - 2f * q2q2 - 2f * q3q3 - az) + _2bz * q4
                * (_2bx * (0.5f - q3q3 - q4q4) + _2bz * (q2q4 - q1q3) - mx)
                + (_2bx * q3 + _2bz * q1)
                * (_2bx * (q2q3 - q1q4) + _2bz * (q1q2 + q3q4) - my)
                + (_2bx * q4 - _4bz * q2)
                * (_2bx * (q1q3 + q2q4) + _2bz * (0.5f - q2q2 - q3q3) - mz);
        s3 = -_2q1 * (2f * q2q4 - _2q1q3 - ax) + _2q4
                * (2f * q1q2 + _2q3q4 - ay) - 4f * q3
                * (1 - 2f * q2q2 - 2f * q3q3 - az) + (-_4bx * q3 - _2bz * q1)
                * (_2bx * (0.5f - q3q3 - q4q4) + _2bz * (q2q4 - q1q3) - mx)
                + (_2bx * q2 + _2bz * q4)
                * (_2bx * (q2q3 - q1q4) + _2bz * (q1q2 + q3q4) - my)
                + (_2bx * q1 - _4bz * q3)
                * (_2bx * (q1q3 + q2q4) + _2bz * (0.5f - q2q2 - q3q3) - mz);
        s4 = _2q2 * (2f * q2q4 - _2q1q3 - ax) + _2q3
                * (2f * q1q2 + _2q3q4 - ay) + (-_4bx * q4 + _2bz * q2)
                * (_2bx * (0.5f - q3q3 - q4q4) + _2bz * (q2q4 - q1q3) - mx)
                + (-_2bx * q1 + _2bz * q3)
                * (_2bx * (q2q3 - q1q4) + _2bz * (q1q2 + q3q4) - my) + _2bx
                * q2
                * (_2bx * (q1q3 + q2q4) + _2bz * (0.5f - q2q2 - q3q3) - mz);
        norm = 1f / (float) Math.sqrt(s1 * s1 + s2 * s2 + s3 * s3 + s4 * s4); // normalise
        // step
        // magnitude
        s1 *= norm;
        s2 *= norm;
        s3 *= norm;
        s4 *= norm;

        // Compute rate of change of quaternion
        qDot1 = 0.5f * (-q2 * gx - q3 * gy - q4 * gz) - beta * s1;
        qDot2 = 0.5f * (q1 * gx + q3 * gz - q4 * gy) - beta * s2;
        qDot3 = 0.5f * (q1 * gy - q2 * gz + q4 * gx) - beta * s3;
        qDot4 = 0.5f * (q1 * gz + q2 * gy - q3 * gx) - beta * s4;

        // Integrate to yield quaternion
        q1 += qDot1 * samplePeriod;
        q2 += qDot2 * samplePeriod;
        q3 += qDot3 * samplePeriod;
        q4 += qDot4 * samplePeriod;
        norm = 1f / (float) Math.sqrt(q1 * q1 + q2 * q2 + q3 * q3 + q4 * q4); // normalise
        // quaternion
        quaternion[0] = q1 * norm;
        quaternion[1] = q2 * norm;
        quaternion[2] = q3 * norm;
        quaternion[3] = q4 * norm;
    }

    public void wierdUpdate(float ax, float ay, float az, float gx, float gy, float gz, float mx, float my, float mz) {
        double q0 = quaternion[0], q1 = quaternion[1], q2 = quaternion[2], q3 = quaternion[3];  // short name local variable for readability
        double recipNorm;
        double s0, s1, s2, s3;
        double qDot1, qDot2, qDot3, qDot4;
        double hx, hy;
        double _2q0mx, _2q0my, _2q0mz, _2q1mx, _2bx, _2bz, _4bx, _4bz, _2q0, _2q1, _2q2, _2q3, _2q0q2, _2q2q3, q0q0, q0q1, q0q2, q0q3, q1q1, q1q2, q1q3, q2q2, q2q3, q3q3;

        // Rate of change of quaternion from gyroscope
        qDot1 = 0.5f * (-q1 * gx - q2 * gy - q3 * gz);
        qDot2 = 0.5f * (q0 * gx + q2 * gz - q3 * gy);
        qDot3 = 0.5f * (q0 * gy - q1 * gz + q3 * gx);
        qDot4 = 0.5f * (q0 * gz + q1 * gy - q2 * gx);

        // Normalise accelerometer measurement
        double a_norm = ax * ax + ay * ay + az * az;
        if (a_norm == 0.) return;  // handle NaN
        recipNorm = 1.0 / Math.sqrt(a_norm);
        ax *= (float) recipNorm;
        ay *= (float) recipNorm;
        az *= (float) recipNorm;

        // Normalise magnetometer measurement
        double m_norm = mx * mx + my * my + mz * mz;
        if (m_norm == 0.) return;  // handle NaN
        recipNorm = 1.0 / Math.sqrt(m_norm);
        mx *= (float) recipNorm;
        my *= (float) recipNorm;
        mz *= (float) recipNorm;

        // Auxiliary variables to avoid repeated arithmetic
        _2q0mx = 2.0f * q0 * mx;
        _2q0my = 2.0f * q0 * my;
        _2q0mz = 2.0f * q0 * mz;
        _2q1mx = 2.0f * q1 * mx;
        _2q0 = 2.0f * q0;
        _2q1 = 2.0f * q1;
        _2q2 = 2.0f * q2;
        _2q3 = 2.0f * q3;
        _2q0q2 = 2.0f * q0 * q2;
        _2q2q3 = 2.0f * q2 * q3;
        q0q0 = q0 * q0;
        q0q1 = q0 * q1;
        q0q2 = q0 * q2;
        q0q3 = q0 * q3;
        q1q1 = q1 * q1;
        q1q2 = q1 * q2;
        q1q3 = q1 * q3;
        q2q2 = q2 * q2;
        q2q3 = q2 * q3;
        q3q3 = q3 * q3;

        // Reference direction of Earth's magnetic field
        hx = mx * q0q0 - _2q0my * q3 + _2q0mz * q2 + mx * q1q1 + _2q1 * my * q2 + _2q1 * mz * q3 - mx * q2q2 - mx * q3q3;
        hy = _2q0mx * q3 + my * q0q0 - _2q0mz * q1 + _2q1mx * q2 - my * q1q1 + my * q2q2 + _2q2 * mz * q3 - my * q3q3;
        _2bx = Math.sqrt(hx * hx + hy * hy);
        _2bz = -_2q0mx * q2 + _2q0my * q1 + mz * q0q0 + _2q1mx * q3 - mz * q1q1 + _2q2 * my * q3 - mz * q2q2 + mz * q3q3;
        _4bx = 2.0f * _2bx;
        _4bz = 2.0f * _2bz;

        // Gradient decent algorithm corrective step
        s0 = -_2q2 * (2.0f * q1q3 - _2q0q2 - ax) + _2q1 * (2.0f * q0q1 + _2q2q3 - ay) - _2bz * q2 * (_2bx * (0.5f - q2q2 - q3q3) + _2bz * (q1q3 - q0q2) - mx) + (-_2bx * q3 + _2bz * q1) * (_2bx * (q1q2 - q0q3) + _2bz * (q0q1 + q2q3) - my) + _2bx * q2 * (_2bx * (q0q2 + q1q3) + _2bz * (0.5f - q1q1 - q2q2) - mz);
        s1 = _2q3 * (2.0f * q1q3 - _2q0q2 - ax) + _2q0 * (2.0f * q0q1 + _2q2q3 - ay) - 4.0f * q1 * (1 - 2.0f * q1q1 - 2.0f * q2q2 - az) + _2bz * q3 * (_2bx * (0.5f - q2q2 - q3q3) + _2bz * (q1q3 - q0q2) - mx) + (_2bx * q2 + _2bz * q0) * (_2bx * (q1q2 - q0q3) + _2bz * (q0q1 + q2q3) - my) + (_2bx * q3 - _4bz * q1) * (_2bx * (q0q2 + q1q3) + _2bz * (0.5f - q1q1 - q2q2) - mz);
        s2 = -_2q0 * (2.0f * q1q3 - _2q0q2 - ax) + _2q3 * (2.0f * q0q1 + _2q2q3 - ay) - 4.0f * q2 * (1 - 2.0f * q1q1 - 2.0f * q2q2 - az) + (-_4bx * q2 - _2bz * q0) * (_2bx * (0.5f - q2q2 - q3q3) + _2bz * (q1q3 - q0q2) - mx) + (_2bx * q1 + _2bz * q3) * (_2bx * (q1q2 - q0q3) + _2bz * (q0q1 + q2q3) - my) + (_2bx * q0 - _4bz * q2) * (_2bx * (q0q2 + q1q3) + _2bz * (0.5f - q1q1 - q2q2) - mz);
        s3 = _2q1 * (2.0f * q1q3 - _2q0q2 - ax) + _2q2 * (2.0f * q0q1 + _2q2q3 - ay) + (-_4bx * q3 + _2bz * q1) * (_2bx * (0.5f - q2q2 - q3q3) + _2bz * (q1q3 - q0q2) - mx) + (-_2bx * q0 + _2bz * q2) * (_2bx * (q1q2 - q0q3) + _2bz * (q0q1 + q2q3) - my) + _2bx * q1 * (_2bx * (q0q2 + q1q3) + _2bz * (0.5f - q1q1 - q2q2) - mz);
        recipNorm = 1.0 / Math.sqrt(s0 * s0 + s1 * s1 + s2 * s2 + s3 * s3);  // normalise step magnitude
        s0 *= recipNorm;
        s1 *= recipNorm;
        s2 *= recipNorm;
        s3 *= recipNorm;

        // Apply feedback step
        qDot1 -= beta * s0;
        qDot2 -= beta * s1;
        qDot3 -= beta * s2;
        qDot4 -= beta * s3;

        // Integrate rate of change of quaternion to yield quaternion
        q0 += qDot1 * samplePeriod;
        q1 += qDot2 * samplePeriod;
        q2 += qDot3 * samplePeriod;
        q3 += qDot4 * samplePeriod;

        // Normalise quaternion
        recipNorm = 1.0 / Math.sqrt(q0 * q0 + q1 * q1 + q2 * q2 + q3 * q3);
        q0 *= recipNorm;
        q1 *= recipNorm;
        q2 *= recipNorm;
        q3 *= recipNorm;

        quaternion[0] = (float) q0;
        quaternion[1] = (float) q1;
        quaternion[2] = (float) q2;
        quaternion[3] = (float) q3;
    }
    void no_filter(float gx, float gy, float gz) {
        long now = System.nanoTime();
        float deltaT = (float) (now-prevTime)/1000000;
        prevTime = now;
        if (deltaT> 1)
        {
            return;
        }
        float q0 = quaternion[0], q1 = quaternion[1], q2 = quaternion[2], q3 = quaternion[3];  // variable for readability
        quaternion[0] += 0.5f * (-q1 * gx - q2 * gy - q3 * gz) * deltaT;
        quaternion[1] += 0.5f * (q0 * gx + q2 * gz - q3 * gy) * deltaT;
        quaternion[2] += 0.5f * (q0 * gy - q1 * gz + q3 * gx) * deltaT;
        quaternion[3] += 0.5f * (q0 * gz + q1 * gy - q2 * gx) * deltaT;
        float recipNorm = 1.0f / (float) Math.sqrt(quaternion[0] * quaternion[0] + quaternion[1] * quaternion[1] + quaternion[2] * quaternion[2] + quaternion[3] * quaternion[3]);
        quaternion[0] *= recipNorm;
        quaternion[1] *= recipNorm;
        quaternion[2] *= recipNorm;
        quaternion[3] *= recipNorm;
    }
    /**
     * Algorithm IMU update method. Requires only gyroscope and accelerometer
     * data.
     * <p>
     * Optimised for minimal arithmetic. <br>
     * Total ±: 45 <br>
     * Total *: 85 <br>
     * Total /: 3 <br>
     * Total sqrt: 3
     *
     * @param gx Gyroscope x axis measurement in radians/s.
     * @param gy Gyroscope y axis measurement in radians/s.
     * @param gz Gyroscope z axis measurement in radians/s.
     * @param ax Accelerometer x axis measurement in any calibrated units.
     * @param ay Accelerometer y axis measurement in any calibrated units.
     * @param az Accelerometer z axis measurement in any calibrated units.
     */
    public void update(float gx, float gy, float gz, float ax, float ay,
                       float az) {
        float q1 = quaternion[0], q2 = quaternion[1], q3 = quaternion[2], q4 = quaternion[3]; // short
        // name
        // local
        // variable
        // for
        // readability
        float norm;
        float s1, s2, s3, s4;
        float qDot1, qDot2, qDot3, qDot4;

        // Auxiliary variables to avoid repeated arithmetic
        float _2q1 = 2f * q1;
        float _2q2 = 2f * q2;
        float _2q3 = 2f * q3;
        float _2q4 = 2f * q4;
        float _4q1 = 4f * q1;
        float _4q2 = 4f * q2;
        float _4q3 = 4f * q3;
        float _8q2 = 8f * q2;
        float _8q3 = 8f * q3;
        float q1q1 = q1 * q1;
        float q2q2 = q2 * q2;
        float q3q3 = q3 * q3;
        float q4q4 = q4 * q4;

        // Normalise accelerometer measurement
        norm = (float) Math.sqrt(ax * ax + ay * ay + az * az);
        if (norm == 0f)
            return; // handle NaN
        norm = 1 / norm; // use reciprocal for division
        ax *= norm;
        ay *= norm;
        az *= norm;

        // Gradient decent algorithm corrective step
        s1 = _4q1 * q3q3 + _2q3 * ax + _4q1 * q2q2 - _2q2 * ay;
        s2 = _4q2 * q4q4 - _2q4 * ax + 4f * q1q1 * q2 - _2q1 * ay - _4q2 + _8q2
                * q2q2 + _8q2 * q3q3 + _4q2 * az;
        s3 = 4f * q1q1 * q3 + _2q1 * ax + _4q3 * q4q4 - _2q4 * ay - _4q3 + _8q3
                * q2q2 + _8q3 * q3q3 + _4q3 * az;
        s4 = 4f * q2q2 * q4 - _2q2 * ax + 4f * q3q3 * q4 - _2q3 * ay;
        norm = 1f / (float) Math.sqrt(s1 * s1 + s2 * s2 + s3 * s3 + s4 * s4); // normalise
        // step
        // magnitude
        s1 *= norm;
        s2 *= norm;
        s3 *= norm;
        s4 *= norm;

        // Compute rate of change of quaternion
        qDot1 = 0.5f * (-q2 * gx - q3 * gy - q4 * gz) - beta * s1;
        qDot2 = 0.5f * (q1 * gx + q3 * gz - q4 * gy) - beta * s2;
        qDot3 = 0.5f * (q1 * gy - q2 * gz + q4 * gx) - beta * s3;
        qDot4 = 0.5f * (q1 * gz + q2 * gy - q3 * gx) - beta * s4;

        // Integrate to yield quaternion
        q1 += qDot1 * samplePeriod;
        q2 += qDot2 * samplePeriod;
        q3 += qDot3 * samplePeriod;
        q4 += qDot4 * samplePeriod;
        norm = 1f / (float) Math.sqrt(q1 * q1 + q2 * q2 + q3 * q3 + q4 * q4); // normalise
        // quaternion
        quaternion[0] = q1 * norm;
        quaternion[1] = q2 * norm;
        quaternion[2] = q3 * norm;
        quaternion[3] = q4 * norm;
    }


}