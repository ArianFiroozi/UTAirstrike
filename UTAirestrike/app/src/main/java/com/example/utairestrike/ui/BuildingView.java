package com.example.utairestrike.ui;

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

import com.example.utairestrike.R;
import com.example.utairestrike.src.engine.GameEngine;
import com.example.utairestrike.src.logic.actors.Aircraft;
import com.example.utairestrike.src.model.Building;
import com.example.utairestrike.src.model.Bullet;

public class BuildingView extends View {

    private Bitmap buildingBitmap;
    private Paint paint;
    private Matrix matrix;

    private Handler handler;
    private Runnable updateRunnable;
    private GameEngine engine;
    private static final long UPDATE_INTERVAL_MS = 100;

    public BuildingView(Context context, GameEngine engine) {
        super(context);
        init(engine);
    }


    private void init(GameEngine engine) {
        this.engine = engine;
        paint = new Paint();
        matrix = new Matrix();
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.building);
        int desiredWidth = 400;
        int desiredHeight = 400;
        buildingBitmap = Bitmap.createScaledBitmap(originalBitmap, desiredWidth, desiredHeight, true);

        handler = new Handler(Looper.getMainLooper());
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                //Building.update(null,null,null);
                invalidate();
                handler.postDelayed(this, UPDATE_INTERVAL_MS);
            }
        };
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        engine.update();

//        float x = engine.getPlayer().getPosition().getX();
//        float y = engine.getPlayer().getPosition().getY();
//        float angle = engine.getPlayer().getRotation();
//
//        matrix.reset();
//        matrix.postTranslate(-aircraftBitmap.getWidth() / 2f, -aircraftBitmap.getHeight() / 2f);
//        matrix.postRotate(angle);
//        matrix.postTranslate(x, y);

        for (Object building : engine.getObjects())
            if (building instanceof Building) {
                Bitmap buildingBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.building);
                int width = (int) ((Building) building).getSize().getX();
                int height = (int) ((Building) building).getSize().getY();
                buildingBitmap = Bitmap.createScaledBitmap(buildingBitmap, width, height, true);


                Matrix buildingMatrix = new Matrix();
                buildingMatrix.postTranslate(-buildingBitmap.getWidth() / 2f, -buildingBitmap.getHeight() / 2f);
                buildingMatrix.postTranslate(((Building) building).getPosition().getX(),
                        ((Building) building).getPosition().getY());

                canvas.drawBitmap(buildingBitmap, buildingMatrix, paint);
            }


//        canvas.drawBitmap(aircraftBitmap, matrix, paint);
    }
}
