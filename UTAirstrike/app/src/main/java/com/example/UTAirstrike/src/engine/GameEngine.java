package com.example.UTAirstrike.src.engine;

import java.time.ZonedDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;

import com.example.UTAirstrike.src.hardware.AircraftSpeed;
import com.example.UTAirstrike.src.model.*;
import com.example.UTAirstrike.src.util.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameEngine {
    public Player player;
    private static final ArrayList <Bullet> bullets = new ArrayList<>();
    private static final ArrayList <Enemy> enemies = new ArrayList<>();
    private static final ArrayList <Building> buildings = new ArrayList<>();
    private static final CollisionDetector cd = new CollisionDetector();
    public static AircraftSpeed aircraftSpeedDelta ;
    private static ZonedDateTime  startingTime;
    private static Vector2D canvasSize ;
    public static long gameDuration;
    public static boolean isWon;
    public static boolean isLost;
    private static final float DELTA_TIME = 1;

    public GameEngine(AircraftSpeed aircraftSpeedDelta, Vector2D canvasSize){
        GameEngine.aircraftSpeedDelta = aircraftSpeedDelta;
        gameDuration = -1;
        isWon = false;
        isLost = false;
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
            bullets.removeIf(bullet -> cd.isCollide(building, bullet));
        }
        return false;
    }

    private boolean handleEnemiesUpdate(){ // key assumption shut bullets will kill enemy and prevent aircraft collision
        Iterator<Enemy> enemiesIterator = enemies.iterator();
        while (enemiesIterator.hasNext()) {
            Enemy enemy = enemiesIterator.next();
            boolean removeEnemy = false;

            Iterator<Bullet> iterator = bullets.iterator();
            while (iterator.hasNext()) {
                Bullet bullet = iterator.next();
                if (cd.isCollide(enemy, bullet)) {
                    iterator.remove();  // Safe removal
                    removeEnemy = true; // Mark enemy for removal
                }
            }

            if (removeEnemy) {
                enemiesIterator.remove(); // Remove enemy safely
            }

            if (cd.isCollide(player, enemy)) {
                return true; // Lose condition
            }
        }
        return false;
    }

    private void removeLostBullets(){
        bullets.removeIf(bullet -> !bullet.isInside(canvasSize));
    }

    public boolean update(){
        if (isLost || isWon)
            return true;

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
        isLost = false;//gameOver;
        return gameOver || isWon;
    }

    public void shoot() {
        bullets.add(player.shoot());
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
