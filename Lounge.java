import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Set;
import java.lang.Math;

public class Lounge {
    public HashMap<String, HashMap<Long, Integer>> weathersMap = new HashMap<>();

    public HashMap<String, Airport> airportsMap = new HashMap<>();

    public HashMap<String, LinkedList<String>> directionsMap = new HashMap<>();

    public HashMap<String, Double> airportWeatherMap = new HashMap<>();

    public long timeForWeather;

    public Lounge(){
        //empty constructor
    }

    public void task(String departure, String destination, long timeForWeather, FileWriter writer) throws IOException{
        this.timeForWeather = timeForWeather;
        dijkstra(departure,destination,writer);
    }

    public void dijkstra(String departure, String destination, FileWriter writer)throws IOException{
        HashMap<String, Double> weight = new HashMap<>();
        HashMap<String, String> connectedTo= new HashMap<>();
        for(String airportName : airportsMap.keySet()){
            weight.put(airportName, Double.MAX_VALUE);
        }

        PriorityQueue<String> queue = new PriorityQueue<>(airportsMap.size(), (a, b) -> weight.get(a).compareTo(weight.get(b)));

        weight.put(departure,0.0);
        queue.add(departure);
        Set<String> visitedNodes = new HashSet<>();

        while(!queue.isEmpty()){
            String currentNode = queue.poll();
            if(visitedNodes.contains(currentNode)){
                continue;
            }else{
                visitedNodes.add(currentNode);
            }

            for(String connected : directionsMap.get(currentNode)){
                double cost = totalCost(currentNode,connected);

                if(weight.get(currentNode) + cost < weight.get(connected)){
                    weight.put(connected, weight.get(currentNode) + cost);
                    connectedTo.put(connected,currentNode);
                    queue.add(connected);
                }
            }
        }
        printPath(departure,destination,connectedTo,writer);
        //System.out.println(String.format(Locale.US, " %.5f", weight.get(destination)));
        writer.write(String.format(Locale.US, " %.5f", weight.get(destination)) +"\n");
    }

    private void printPath( String departure, String destination, HashMap<String,String> connectedTo,FileWriter writer )throws IOException {
        if(connectedTo.get(destination) == null){
            //System.out.print(destination);
            writer.write(destination);
            return;
        }
        printPath(departure,connectedTo.get(destination),connectedTo, writer);
        //System.out.print(" " + destination);
        writer.write(" " + destination);
    }

    public double totalCost(String departure, String destination){
        double weatherDeparture = weatherCost(departure);
        double weatherDestination = weatherCost(destination);

        double lat1 = airportsMap.get(departure).latitude;
        double lat2 = airportsMap.get(destination).latitude;
        double long1 = airportsMap.get(departure).longitude;
        double long2 = airportsMap.get(destination).longitude;

        double distance = haversine(lat1,lat2,long1,long2);
        return 300.0 * weatherDeparture*weatherDestination + distance;
    }

    private double haversine(double lat1,double lat2, double long1, double long2){
        double diffLat = Math.toRadians(lat2-lat1);
        double diffLong = Math.toRadians(long2-long1);
        double rootInside = Math.sin(diffLat/2.0) * Math.sin(diffLat/2.0) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(diffLong/2.0) * Math.sin(diffLong/2.0);
        return 2 * 6371 * Math.asin(Math.sqrt(rootInside));
    }

    private  double weatherCost(String airportName){
        if(airportWeatherMap.containsKey(airportsMap.get(airportName).airfieldName)){
            return airportWeatherMap.get(airportsMap.get(airportName).airfieldName);
        }else{
            String airfieldName = airportsMap.get(airportName).airfieldName;
            int weatherCode = weathersMap.get(airfieldName).get(timeForWeather);
            int wind = (weatherCode & 16) >> 4;
            int rain = (weatherCode & 8) >> 3;
            int snow = (weatherCode & 4) >> 2;
            int hail = (weatherCode & 2) >> 1;
            int bolt = (weatherCode & 1);
            return (1.05 * wind + (1-wind)) * (1.05 * rain + (1-rain)) * (1.10 * snow + (1-snow))*
                    (1.15 * hail + (1-hail)) * (1.20 * bolt + (1-bolt));
        }
    }

}
