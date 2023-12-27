import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main  (String[] args) throws IOException{
        //Reading the weather file.
        Lounge aviation = new Lounge();
        FileWriter myWriter = new FileWriter("output.txt");

        File weatherFile = new File("weather-part2.txt");
        Scanner weatherReader = new Scanner(weatherFile);
        weatherReader.nextLine();
        while(weatherReader.hasNextLine()){
            String[] line = weatherReader.nextLine().split(",");
            String airfieldName = line[0];
            long time = Long.parseLong(line[1]);
            int weatherCode = Integer.parseInt(line[2]);
            if(aviation.weathersMap.containsKey(airfieldName)){
                aviation.weathersMap.get(airfieldName).put(time,weatherCode);
            }else {
                HashMap<Long,Integer> newForWeathers = new HashMap<>();
                newForWeathers.put(time,weatherCode);
                aviation.weathersMap.put(airfieldName,newForWeathers);
            }
        }
        //End of weather file.

        //Reading the airports file
        File airportsFile = new File("TR-3-airports.txt");
        Scanner airportsReader = new Scanner(airportsFile);
        airportsReader.nextLine();
        while(airportsReader.hasNextLine()){
            String[] line = airportsReader.nextLine().split(",");
            String airportName = line[0];
            String airfieldName = line[1];
            double latitude = Double.parseDouble(line[2]);
            double longitude = Double.parseDouble(line[3]);
            double parkingCost = Double.parseDouble(line[4]);
            Airport airport = new Airport(airportName,airfieldName,latitude,longitude,parkingCost);
            aviation.airportsMap.put(airportName,airport);
            aviation.directionsMap.put(airportName, new LinkedList<String>());
        }
        //End of airport file

        //Reading the directions file
        File directionsFile = new File("TR-3-directions.txt");
        Scanner directionsReader = new Scanner(directionsFile);
        directionsReader.nextLine();
        while(directionsReader.hasNextLine()){
            String[] line = directionsReader.nextLine().split(",");
            String departure = line[0];
            String destination = line[1];
            aviation.directionsMap.get(departure).add(destination);
        }
        //End of directions file

        //Reading the missions file
        File missionsFile = new File("TR-3-missions.txt");
        Scanner missionsReader = new Scanner(missionsFile);
        missionsReader.nextLine();
        while (missionsReader.hasNextLine()){
            String[] line = missionsReader.nextLine().split(" ");
            String departure = line[0];
            String destination = line[1];
            long timeForWeather = Long.parseLong(line[2]);
            aviation.task(departure,destination,timeForWeather,myWriter);
        }
        myWriter.close();


    }

}