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
import com.example.utairestrike.src.logic.actors.Aircraft;
import com.example.utairestrike.src.model.Building;
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
//        init(engine);
    }



}
