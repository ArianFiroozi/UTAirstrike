package com.example.utairestrike.src.engine;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;

import com.example.utairestrike.src.hardware.AircraftSpeed;
import com.example.utairestrike.src.model.*;
import com.example.utairestrike.src.utill.*;

import lombok.Getter;

@Getter
public class GameEngine {
    private Player player;
    private static final ArrayList <Bullet> bullets = new ArrayList<>();
    private static final ArrayList <Enemy> enemies = new ArrayList<>();
    private static final ArrayList <Building> buildings = new ArrayList<>();
    private static final ColissionDetector cd = new ColissionDetector();
    private static AircraftSpeed aircraftSpeedDelta ;
    private static ZonedDateTime  startingTime;
    private static Vector2D canvasSize ;
    public static long gameDuration;
    public static boolean isWon;
    private static final float DELTA_TIME = 1;

    public GameEngine(AircraftSpeed aircraftSpeedDelta, Vector2D canvasSize){
        GameEngine.aircraftSpeedDelta = aircraftSpeedDelta;
        gameDuration = -1;
        isWon = false;
        GameEngine.canvasSize = canvasSize;
    }

    public void run(ArrayList<GameObject> map){
        startingTime = ZonedDateTime.now();
        for (GameObject object : map) {
            if (object instanceof Enemy) {
                enemies.add((Enemy) object);
            }
            else if (object instanceof Bullet) {
                bullets.add((Bullet) object);
            }
            else if (object instanceof Building) {
                buildings.add((Building) object);
            }
            else if (object instanceof Player) {
                player = (Player) object;
            }
            else
                System.out.println("arian ride");
        }
    }

    private boolean handleBuildingsUpdate(){
        for (Building building : buildings){
            if (cd.isCollide(building, player))
                return true;//loose
            Iterator<Bullet> iterator = bullets.iterator();
            while (iterator.hasNext()) {
                Bullet bullet = iterator.next();
                if (cd.isCollide(building, bullet))
                    iterator.remove();;
            }
        }
        return false;
    }

    private boolean handleEnemiesUpdate(){ // key assumption shut bullets will kill enemy and prevent aircraft collision
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
                return true; // loose
            }
        }
        return false;
    }

    private void removeLostBullets(){
        bullets.removeIf(bullet -> !bullet.isInside(canvasSize));
    }

    public boolean update(){
        player.update(DELTA_TIME, aircraftSpeedDelta.getVelocity(), aircraftSpeedDelta.getRotationDelta());
        for (Bullet bullet : bullets)
            bullet.update(DELTA_TIME, new Vector2D(), 0);
        boolean gameOver = false;
        gameOver = handleBuildingsUpdate();
        gameOver = (gameOver) ? gameOver : handleEnemiesUpdate();
        removeLostBullets();
        if (enemies.isEmpty())
            isWon = true;
        gameDuration = Duration.between(startingTime, ZonedDateTime.now()).toMillis();
        return gameOver || isWon;
    }

    public void shoot() {
        getObjects().add(player.shoot());
    }

    public ArrayList<GameObject> getObjects (){
        ArrayList<GameObject> objects = new ArrayList<>();
        objects.add(player);
        objects.addAll(bullets);
        objects.addAll(buildings);
        objects.addAll(enemies);
        return objects;
    }
}
