package com.example.utairestrike.src.hardware.sensorWrapper;
public class MadgwickAHRS {
    private float sampleFreq = 512.0f; // sample frequency in Hz
    private float beta = 0.1f;         // algorithm gain

    private float q0 = 1f, q1 = 0f, q2 = 0f, q3 = 0f; // quaternion

    public void update(float gx, float gy, float gz, float ax, float ay, float az,
                       float mx, float my, float mz) {
        float norm;
        float hx, hy, _2bx, _2bz;
        float s0, s1, s2, s3;
        float qDot1, qDot2, qDot3, qDot4;

        // Normalize accelerometer
        norm = (float)Math.sqrt(ax * ax + ay * ay + az * az);
        if (norm == 0f) return;
        ax /= norm;
        ay /= norm;
        az /= norm;

        // Normalize magnetometer
        norm = (float)Math.sqrt(mx * mx + my * my + mz * mz);
        if (norm == 0f) return;
        mx /= norm;
        my /= norm;
        mz /= norm;

        // Auxiliary variables
        float _2q0mx = 2f * q0 * mx;
        float _2q0my = 2f * q0 * my;
        float _2q0mz = 2f * q0 * mz;
        float _2q1mx = 2f * q1 * mx;
        float _2q0 = 2f * q0;
        float _2q1 = 2f * q1;
        float _2q2 = 2f * q2;
        float _2q3 = 2f * q3;
        float _2q0q2 = 2f * q0 * q2;
        float _2q2q3 = 2f * q2 * q3;
        float q0q0 = q0 * q0;
        float q0q1 = q0 * q1;
        float q0q2 = q0 * q2;
        float q0q3 = q0 * q3;
        float q1q1 = q1 * q1;
        float q1q2 = q1 * q2;
        float q1q3 = q1 * q3;
        float q2q2 = q2 * q2;
        float q2q3 = q2 * q3;
        float q3q3 = q3 * q3;

        // Reference direction of Earth's magnetic field
        hx = mx * q0q0 - _2q0my * q3 + _2q0mz * q2 + mx * q1q1 + _2q1 * my * q2 + _2q1 * mz * q3 - mx * q2q2 - mx * q3q3;
        hy = _2q0mx * q3 + my * q0q0 - _2q0mz * q1 + _2q1mx * q2 - my * q1q1 + my * q2q2 + _2q2 * mz * q3 - my * q3q3;
        _2bx = (float)Math.sqrt(hx * hx + hy * hy);
        _2bz = -_2q0mx * q2 + _2q0my * q1 + mz * q0q0 + _2q1mx * q3 - mz * q1q1 + _2q2 * my * q3 - mz * q2q2 + mz * q3q3;

        // Gradient descent algorithm corrective step
        s0 = -_2q2 * (2f * q1q3 - _2q0q2 - ax) + _2q1 * (2f * q0q1 + _2q2q3 - ay) - _2bz * q2 * (_2bx * (0.5f - q2q2 - q3q3) + _2bz * (q1q3 - q0q2) - mx)
                + (-_2bx * q3 + _2bz * q1) * (_2bx * (q1q2 - q0q3) + _2bz * (q0q1 + q2q3) - my)
                + _2bx * q2 * (_2bx * (q0q2 + q1q3) + _2bz * (0.5f - q1q1 - q2q2) - mz);
        s1 = _2q3 * (2f * q1q3 - _2q0q2 - ax) + _2q0 * (2f * q0q1 + _2q2q3 - ay) - 4f * q1 * (1f - 2f * q1q1 - 2f * q2q2 - az)
                + _2bz * q3 * (_2bx * (0.5f - q2q2 - q3q3) + _2bz * (q1q3 - q0q2) - mx)
                + (_2bx * q2 + _2bz * q0) * (_2bx * (q1q2 - q0q3) + _2bz * (q0q1 + q2q3) - my)
                + (_2bx * q3 - _2bz * q1) * (_2bx * (q0q2 + q1q3) + _2bz * (0.5f - q1q1 - q2q2) - mz);
        s2 = -_2q0 * (2f * q1q3 - _2q0q2 - ax) + _2q3 * (2f * q0q1 + _2q2q3 - ay) - 4f * q2 * (1f - 2f * q1q1 - 2f * q2q2 - az)
                + (-_2bx * q2 - _2bz * q0) * (_2bx * (0.5f - q2q2 - q3q3) + _2bz * (q1q3 - q0q2) - mx)
                + (_2bx * q1 + _2bz * q3) * (_2bx * (q1q2 - q0q3) + _2bz * (q0q1 + q2q3) - my)
                + (_2bx * q0 - _2bz * q2) * (_2bx * (q0q2 + q1q3) + _2bz * (0.5f - q1q1 - q2q2) - mz);
        s3 = _2q1 * (2f * q1q3 - _2q0q2 - ax) + _2q2 * (2f * q0q1 + _2q2q3 - ay)
                + (-_2bx * q3 + _2bz * q1) * (_2bx * (0.5f - q2q2 - q3q3) + _2bz * (q1q3 - q0q2) - mx)
                + (-_2bx * q0 + _2bz * q2) * (_2bx * (q1q2 - q0q3) + _2bz * (q0q1 + q2q3) - my)
                + _2bx * q1 * (_2bx * (q0q2 + q1q3) + _2bz * (0.5f - q1q1 - q2q2) - mz);

        norm = (float)Math.sqrt(s0 * s0 + s1 * s1 + s2 * s2 + s3 * s3);
        s0 /= norm;
        s1 /= norm;
        s2 /= norm;
        s3 /= norm;

        // Apply feedback step
        qDot1 = 0.5f * (-q1 * gx - q2 * gy - q3 * gz) - beta * s0;
        qDot2 = 0.5f * ( q0 * gx + q2 * gz - q3 * gy) - beta * s1;
        qDot3 = 0.5f * ( q0 * gy - q1 * gz + q3 * gx) - beta * s2;
        qDot4 = 0.5f * ( q0 * gz + q1 * gy - q2 * gx) - beta * s3;

        // Integrate to yield quaternion
        q0 += qDot1 / sampleFreq;
        q1 += qDot2 / sampleFreq;
        q2 += qDot3 / sampleFreq;
        q3 += qDot4 / sampleFreq;

        // Normalize quaternion
        norm = (float)Math.sqrt(q0 * q0 + q1 * q1 + q2 * q2 + q3 * q3);
        q0 /= norm;
        q1 /= norm;
        q2 /= norm;
        q3 /= norm;
    }

    public float[] getQuaternion() {
        return new float[] { q0, q1, q2, q3 };
    }
}
