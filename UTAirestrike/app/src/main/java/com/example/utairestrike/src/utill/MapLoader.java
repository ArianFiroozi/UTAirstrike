package com.example.utairestrike.src.utill;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import com.example.utairestrike.src.model.*;

public class MapLoader {
    public static ArrayList<GameObject> loadFromCSVFile(BufferedReader csvFile) throws IOException {
        ArrayList<GameObject> objects = new ArrayList<>();
        String line;
        csvFile.readLine();

        while ((line = csvFile.readLine()) != null) {
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
        System.out.println("MAP LEN:"+objects.size());

        return objects;
    }
}
