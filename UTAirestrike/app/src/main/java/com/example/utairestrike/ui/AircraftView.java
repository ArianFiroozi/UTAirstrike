package com.example.utairestrike.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.utairestrike.R;
import com.example.utairestrike.src.logic.actors.Airecraft;

public class AircraftView extends View {

    private Bitmap aircraftBitmap;
    private Paint paint;
    private Matrix matrix;

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
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fighter_jet);
        int desiredWidth = 100;
        int desiredHeight = 100;
        aircraftBitmap = Bitmap.createScaledBitmap(originalBitmap, desiredWidth, desiredHeight, true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float x = Airecraft.X;
        float y = Airecraft.Y;
        float angle = Airecraft.angle;

        matrix.reset();
        matrix.postTranslate(-aircraftBitmap.getWidth() / 2f, -aircraftBitmap.getHeight() / 2f);
        matrix.postRotate(angle);
        matrix.postTranslate(x, y);

        canvas.drawBitmap(aircraftBitmap, matrix, paint);

        invalidate();
    }
}

