package com.example.UTAirstrike.src.engine;

import java.time.ZonedDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
        // 1) Check if player collides with any building
        boolean playerHit = buildings.parallelStream()
                .anyMatch(b -> cd.isCollide(b, player));
        if (playerHit) return true;

        // 2) Remove any bullets that hit a building (in parallel)
        Set<Bullet> deadBullets = bullets.parallelStream()
                .filter(b -> buildings.stream().anyMatch(building -> cd.isCollide(building, b)))
                .collect(Collectors.toSet());
        bullets.removeAll(deadBullets);

        return false;
    }

    private boolean handleEnemiesUpdate(){
        // 1) Find all (bullet, enemy) collisions in parallel
        Set<Bullet> bulletsToRemove = ConcurrentHashMap.newKeySet();
        Set<Enemy>  enemiesToRemove = ConcurrentHashMap.newKeySet();

        bullets.parallelStream().forEach(b -> {
            enemies.stream()
                    .filter(e -> cd.isCollide(e, b))
                    .findFirst()
                    .ifPresent(e -> {
                        bulletsToRemove.add(b);
                        enemiesToRemove.add(e);
                    });
        });

        bullets.removeAll(bulletsToRemove);
        enemies.removeAll(enemiesToRemove);

        // 2) Check if any enemy now collides with player
        boolean playerCrashed = enemies.parallelStream()
                .anyMatch(e -> cd.isCollide(player, e));
        if (playerCrashed) return true;

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
