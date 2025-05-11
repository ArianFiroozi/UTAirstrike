package com.example.utairestrike.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.example.utairestrike.R;
import com.example.utairestrike.src.engine.GameEngine;

public class EnemyView extends View {

    private Bitmap EnemyBitmap;
    private Paint paint;
    private Matrix matrix;

    private Handler handler;
    private Runnable updateRunnable;
    private GameEngine engine;
    private static final long UPDATE_INTERVAL_MS = 100;

    public EnemyView(Context context, GameEngine engine) {
        super(context);
        init(engine);
    }


    private void init(GameEngine engine) {
        this.engine = engine;
        paint = new Paint();
        matrix = new Matrix();
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.building);
        int desiredWidth = 100;
        int desiredHeight = 100;
        EnemyBitmap = Bitmap.createScaledBitmap(originalBitmap, desiredWidth, desiredHeight, true);

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
}
