package com.example.utairestrike.src.utill;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import com.example.utairestrike.src.model.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MapLoader {
    public static ArrayList<GameObject> loadFromCSVFile(String csvFilePath) {
        ArrayList<GameObject> objects = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            // Skip the header line if it exists
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                String type = values[0];
                float pos_x = Float.parseFloat(values[1]);
                float pos_y = Float.parseFloat(values[2]);
                float size_x = Float.parseFloat(values[3]);
                float size_y = Float.parseFloat(values[4]);
                float vel_x = Float.parseFloat(values[5]);
                float vel_y = Float.parseFloat(values[6]);

                Vector2D position = new Vector2D(pos_x, pos_y);
                Vector2D size = new Vector2D(size_x, size_y);
                Vector2D velocity = new Vector2D(vel_x, vel_y);

                switch (type) {
                    case "building":
                        objects.add(new Building(position, size));
                        break;
                    case "enemy":
                        objects.add(new Enemy(position, velocity, size));
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return objects;
    }
}
