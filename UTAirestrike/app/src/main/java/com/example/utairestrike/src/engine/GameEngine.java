package com.example.utairestrike.src.engine;

import java.util.ArrayList;
import java.util.Iterator;

import com.example.utairestrike.src.hardware.AircraftSpeed;
import com.example.utairestrike.src.model.*;
import com.example.utairestrike.src.utill.*;

public class GameEngine {
    private Player player;
    private static ArrayList <Bullet> bullets = new ArrayList<>();
    private static ArrayList <Enemy> enemies = new ArrayList<>();
    private static ArrayList <Building> buildings = new ArrayList<>();
    private static final ColissionDetector cd = new ColissionDetector();
    private static AircraftSpeed aircraftSpeedDelta ;
    private static final float DELTA_TIME = 1;

    public GameEngine(AircraftSpeed aircraftSpeedDelta){
        GameEngine.aircraftSpeedDelta = aircraftSpeedDelta;
    }

    private void handleBuildingsUpdate(){
        for (Building building : buildings){
            if (cd.isCollide(building, player))
                return ;//loose
            Iterator<Bullet> iterator = bullets.iterator();
            while (iterator.hasNext()) {
                Bullet bullet = iterator.next();
                if (cd.isCollide(building, bullet))
                    iterator.remove();;
            }
        }
    }

    private void handleEnemiesUpdate(){ // key assumption shut bullets will kill enemy and prevent aircraft collision
        Iterator<Enemy> enemiesIterator = enemies.iterator();
        while (enemiesIterator.hasNext()){
            Enemy enemy = enemiesIterator.next();
            Iterator<Bullet> iterator = bullets.iterator();
            while (iterator.hasNext()) {
                Bullet bullet = iterator.next();
                if (cd.isCollide(enemy, bullet)) {
                    iterator.remove();
                    enemiesIterator.remove();
                }
            }

            if (cd.isCollide(player, enemy)){
                return; // loose
            }
        }
    }

    public void update(boolean shoot){
        player.update(DELTA_TIME, aircraftSpeedDelta.getVelocity(), aircraftSpeedDelta.getRotationDelta());

        for (Bullet bullet : bullets)
            bullet.update(DELTA_TIME, new Vector2D(), 0);

        if (shoot)
            player.shoot();

        handleBuildingsUpdate();
        handleEnemiesUpdate();

    }
}
