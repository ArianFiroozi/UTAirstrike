package com.example.UTAirstrike.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.example.UTAirstrike.R;
import com.example.UTAirstrike.src.engine.GameEngine;
import com.example.UTAirstrike.src.model.Bullet;

public class AircraftView extends View {

    private Bitmap aircraftBitmap;
    private Paint paint;
    private Matrix matrix;

    private Handler handler;
    private Runnable updateRunnable;
    private GameEngine engine;
    private static final long UPDATE_INTERVAL_MS = 100;

    public AircraftView(Context context, GameEngine engine) {
        super(context);
        init(engine);
    }

    private void init(GameEngine engine) {
        this.engine = engine;
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

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float x = engine.getPlayer().getPosition().getX();
        float y = engine.getPlayer().getPosition().getY();
        float angle = engine.getPlayer().getRotation();

        matrix.reset();
        matrix.postTranslate(-aircraftBitmap.getWidth() / 2f, -aircraftBitmap.getHeight() / 2f);
        matrix.postRotate(angle);
        matrix.postTranslate(x, y);

        for (Object bullet : engine.getObjects())
            if (bullet instanceof Bullet) {
                Bitmap bulletBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bullet);
                int width = 10;
                int height = 10;
                bulletBitmap = Bitmap.createScaledBitmap(bulletBitmap, width, height, true);


                Matrix bulletMatrix = new Matrix();
                bulletMatrix.postTranslate(-bulletBitmap.getWidth() / 2f, -bulletBitmap.getHeight() / 2f);
                bulletMatrix.postTranslate(((Bullet) bullet).getPosition().getX(),
                        ((Bullet) bullet).getPosition().getY());

                canvas.drawBitmap(bulletBitmap, bulletMatrix, paint);
            }


        canvas.drawBitmap(aircraftBitmap, matrix, paint);
    }
}
