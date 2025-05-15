package com.example.UTAirstrike.src.engine;

import java.time.ZonedDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
    private static final float DELTA_TIME = 0.05F;
    private static final Vector2D ZERO_VELOCITY = new Vector2D(0, 0);

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
        for (Building building : buildings) {
            if (cd.isCollide(building, player))
                return true;

            Iterator<Bullet> bulletIterator = bullets.iterator();
            while (bulletIterator.hasNext()) {
                Bullet bullet = bulletIterator.next();
                if (cd.isCollide(building, bullet)) {
                    bulletIterator.remove();
                    break;
                }
            }
        }

        return false;
    }

    private boolean handleEnemiesUpdate(){
        Set<Bullet> bulletsToRemove = new HashSet<>();
        Set<Enemy> enemiesToRemove = new HashSet<>();

        for (Enemy enemy : enemies){
            for (Bullet bullet: bullets){
                if (cd.isCollide(enemy, bullet)) {
                    bulletsToRemove.add(bullet);
                    enemiesToRemove.add(enemy);
                    break;
                }
            }
            if (!enemiesToRemove.contains(enemy) && cd.isCollide(player, enemy))
                return true;
        }
        bullets.removeAll(bulletsToRemove);
        enemies.removeAll(enemiesToRemove);
        return false;
    }


    private void removeLostBullets(){
        bullets.removeIf(bullet -> !bullet.isInside(canvasSize));
    }

    public boolean update(){
        if (isLost || isWon)
            return true;

        player.update(DELTA_TIME * (bullets.size()*bullets.size()+1), aircraftSpeedDelta.getVelocity(), aircraftSpeedDelta.getRotationDelta());
        for (Bullet bullet : bullets)
            bullet.update(DELTA_TIME * bullets.size()*bullets.size(), ZERO_VELOCITY, 0);
        boolean gameOver = handleBuildingsUpdate() || handleEnemiesUpdate();
        removeLostBullets();
        if (enemies.isEmpty())
            isWon = true;
        gameDuration = Duration.between(startingTime, ZonedDateTime.now()).toMillis();
        isLost = gameOver;
        return gameOver || isWon;
    }

    public void shoot() {
        Bullet bullet = player.shoot();
        if (bullet != null)
            bullets.add(bullet);
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
