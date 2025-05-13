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
import com.example.utairestrike.src.model.Enemy;
import com.example.utairestrike.src.model.Bullet;
public class EnemyView extends View {

    private Bitmap enemyBitmap;
    private Bitmap enemyBitmapFlipped;
    private Paint paint;
    private Matrix matrix;

    private Handler handler;
    private Runnable updateRunnable;
    private GameEngine engine;
    private static final long UPDATE_INTERVAL_MS = 30;

    public EnemyView(Context context, GameEngine engine) {
        super(context);
        init(engine);
    }


    private void init(GameEngine engine) {
        this.engine = engine;
        paint = new Paint();
        matrix = new Matrix();
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.enemy);
        int desiredWidth = 100;
        int desiredHeight = 100;
        enemyBitmap = Bitmap.createScaledBitmap(originalBitmap, desiredWidth, desiredHeight, true);

        Matrix flip = new Matrix();
        flip.preScale(-1f, 1f);
        Bitmap flippedRaw = Bitmap.createBitmap(
                originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(),
                flip, true);
        enemyBitmapFlipped = Bitmap.createScaledBitmap(
                flippedRaw, desiredWidth, desiredHeight, true);


        handler = new Handler(Looper.getMainLooper());
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
                handler.postDelayed(this, UPDATE_INTERVAL_MS);
            }
        };
        updateRunnable.run();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
//        engine.update();
        System.out.println("ENEMY SIZE: " + engine.getObjects().size());
        super.onDraw(canvas);
        for (Object enemy : engine.getObjects())
            if (enemy instanceof Enemy) {
                Enemy enemyInstance = (Enemy) enemy;
//                Bitmap enemyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.enemy);

                Bitmap bmp = enemyInstance.Isflipped() ? enemyBitmap : enemyBitmapFlipped;

//               int width = (int) ((Enemy) enemy).getSize().getX();
//               int height = (int) ((Enemy) enemy).getSize().getY();
//                enemyBitmap = Bitmap.createScaledBitmap(enemyBitmap, width, height, true);

                Matrix enemyMatrix = new Matrix();
                enemyMatrix.postTranslate(-enemyBitmap.getWidth() / 2f, -enemyBitmap.getHeight() / 2f);
                enemyMatrix.postTranslate(((Enemy) enemy).getPosition().getX(),
                        ((Enemy) enemy).getPosition().getY());

                canvas.drawBitmap(bmp, enemyMatrix, paint);
            }
    }
}


