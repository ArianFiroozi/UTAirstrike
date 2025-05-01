package com.example.utairestrike.src.hardware.sensorWrapper;

public class SensorWrapper {
    public static class Accelerometer {
        public static float X, Y, Z;
        static float[] denoise() {
            return new float[]{X, Y, Z};
        }
    }

    public static class Gyroscope {
        public static float X, Y, Z;
        static float[] denoise() {
            return new float[]{X, Y, Z};
        }
    }

    public static class Magnetometer {
        public static float X, Y, Z;
        static float[] denoise() {
            return new float[]{X, Y, Z};
        }
    }

    public static class Gravimeter {
        public static float X, Y, Z;
        static float[] denoise() {
            return new float[]{X, Y, Z};
        }
    }
}

