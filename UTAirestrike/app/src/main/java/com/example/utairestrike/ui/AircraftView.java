package com.example.utairestrike.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import com.example.utairestrike.R;
import com.example.utairestrike.src.logic.actors.Aircraft;

public class AircraftView extends View {

    private Bitmap aircraftBitmap;
    private Paint paint;
    private Matrix matrix;

    private Handler handler;
    private Runnable updateRunnable;

    private static final long UPDATE_INTERVAL_MS = 100;

    public AircraftView(Context context) {
        super(context);
        init();
    }

    public AircraftView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        matrix = new Matrix();
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fighter);
        int desiredWidth = 100;
        int desiredHeight = 100;
        aircraftBitmap = Bitmap.createScaledBitmap(originalBitmap, desiredWidth, desiredHeight, true);

        handler = new Handler(Looper.getMainLooper());
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                Aircraft.update();
                invalidate();
                handler.postDelayed(this, UPDATE_INTERVAL_MS);
            }
        };
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Start the periodic updates when the view is attached to the window
        handler.postDelayed(updateRunnable, UPDATE_INTERVAL_MS);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(updateRunnable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float x = Aircraft.X;
        float y = Aircraft.Y;
        float angle = Aircraft.angle;

        matrix.reset();
        matrix.postTranslate(-aircraftBitmap.getWidth() / 2f, -aircraftBitmap.getHeight() / 2f);
        matrix.postRotate(angle);
        matrix.postTranslate(x, y);

        canvas.drawBitmap(aircraftBitmap, matrix, paint);
    }
}
