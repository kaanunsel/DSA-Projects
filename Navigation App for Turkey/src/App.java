import java.awt.*;
import java.util.Locale; //imported this to set decimal separator to dot.
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * The program finds the shortest distance between the given cities in Turkey using Dijkstra's algorithm.
 * Also, visualizes the cities and the path on map of Turkey.
 *
 * @author Mehmet Kaan Unsel, Student ID: 2022400123
 * @since Date: 23.03.2024
 */
public class App {
    public static void main(String[] args) throws  FileNotFoundException{

        File f_coords = new File("Navigation App for Turkey/city_coordinates.txt"); // creating file object for coordinate file
        Scanner coords_scanner = new Scanner(f_coords); // creates scanner object for coordinate file

        ArrayList<City> cities = new ArrayList<>(); // creates arraylist of City datatype
        ArrayList<String> city_names = new ArrayList<>(); // creates an arraylist for finding the id's of cities
        ArrayList<City> unvisitedCities = new ArrayList<>(); // creates an arraylist for storing the unvisited cities


        int id_counter = 0; // counter to increment in each loop to assign an id to each city object to be able to access them by their indexes.

        // iterating through the city_coordinates.txt
        while (coords_scanner.hasNextLine()){
            String[] line = coords_scanner.nextLine().split(", ");
            City temp_city = new City(line[0],Integer.parseInt(line[1]),Integer.parseInt(line[2]),id_counter); // creating city object with name, x, y, id

            // checking if the current city is already in the lists.
            if(!city_names.contains(line[0]) && !unvisitedCities.contains(temp_city) && !cities.contains(temp_city)) {
                cities.add(temp_city);
                unvisitedCities.add(temp_city);
                city_names.add(line[0]);
            }

            // incrementing id counter by 1
            id_counter++;
        }

        // creating scanner object
        Scanner input = new Scanner(System.in);

        // declaring start and end cities for storing the input values
        String start_city;
        String end_city;

        // using while loop to make user enter a city name repeatedly in case of invalid input
        while(true){
            System.out.println("Enter starting city: ");
            start_city = input.nextLine();
            // if input is a valid city name, exit the loop
            if(city_names.contains(start_city))
                break;
            System.out.printf("City named '%s' not found. Please enter a valid city name.\n",start_city);
        }
        // using while loop to make user enter a city name repeatedly in case of invalid input
        while(true){
            System.out.println("Enter destination city: ");
            end_city = input.nextLine();
            // if input is a valid city name, exit the loop
            if(city_names.contains(end_city))
                break;
            System.out.printf("City named '%s' not found. Please enter a valid city name.\n",end_city);
        }

        File f_connects = new File("Navigation App for Turkey/city_connections.txt"); // creating file object for connections file
        Scanner connection_scanner = new Scanner(f_connects); // creating scanner object for connections file

        // reading each line from city_connections.txt with this while loop
        while (connection_scanner.hasNextLine()){
            String[] line = connection_scanner.nextLine().split(","); // formatting the line into a string list by spliting
            City city1 = cities.get(city_names.indexOf(line[0])); // first word is the first city
            City city2 = cities.get(city_names.indexOf(line[1])); // second word is the second city
            cities.get(city_names.indexOf(line[0])).connections.add(city2); // adding the second city to the connections list of the city1 object
            cities.get(city_names.indexOf(line[1])).connections.add(city1); // adding the first city to the connections list of the city2 object
        }

        // duplicating the address of the start city object and assigning it to start_city_object
        City start_city_object = cities.get(city_names.indexOf(start_city));
        // duplicating the address of the end city object and assigning it to end_city_object
        City end_city_object = cities.get(city_names.indexOf(end_city));
        start_city_object.distance = 0; // setting the distance of the start city to 0
        City nextCity = start_city_object; // duplicating start_city_object and storing its address under nextCity variable


        // this is the main loop for dijkstra algorithm, loop keeps iterating until the next city is not directly connected to the path or unvisited cities is empty
        while(nextCity.distance != Double.MAX_VALUE && unvisitedCities.isEmpty() == false){

            nextCity.visited = true; // setting the visited attribute of the next city to true
            unvisitedCities.remove(nextCity); // removing the current nextCity from unvisited cities

            // iterating through the connections of the current nextCity
            for(City connection : nextCity.connections){
                // if the connection is not visited, get into the if
                if (!connection.visited){
                    // first, calculate the distance from the current city to the current connection and store it in tempDistance
                    double tempDistance = Math.sqrt(Math.pow((nextCity.x-connection.x),2)+Math.pow((nextCity.y-connection.y),2));
                    // then, calculate the distance from start city to the current connection and store it in realDistance
                    double realDistance = nextCity.distance + tempDistance;
                    // if the calculated distance is less than the current distance of the connection city, update it
                    if (realDistance<connection.distance){
                        connection.prev_node = nextCity;
                        connection.distance = realDistance;
                    }
                }
            }


            double min_distance = Double.MAX_VALUE; // declaring a minimum distance variable and setting it to infinity
            // iterating through the unvisited cities
            for(City i : unvisitedCities){
                // if the distance of the current city is less than or equal to min_distance, make it the nextCity
                if(i.distance<=min_distance) {
                    nextCity = i;
                    min_distance = i.distance;
                }
            }
        }

        // if the distance of the next city node is infinite and the end city is still not visited, return "No path found"
        if (nextCity.distance == Double.MAX_VALUE && unvisitedCities.contains(end_city_object)) {
            System.out.println("No path could be found.");
            coords_scanner.close(); // close the scanner object
            connection_scanner.close(); // close the scanner object
            input.close(); // close the scanner object
            return;
        }

        System.out.printf(Locale.US,"Total Distance: %.2f. Path: ",end_city_object.distance); // print the total distance to the end city

        ArrayList<City> path_cities = new ArrayList<>(); // create a City array list for storing the cities on the path
        City prev_city = end_city_object; // declare prev_city node and assign the address of the end city object to it
        path_cities.add(prev_city); // add the prev_city to path cities

        // starting from the end city, trace back using the prev node attribute of each city object until the prev_node of the current city is null which means the first city
        while(prev_city.prev_node != null){
            prev_city = prev_city.prev_node;
            path_cities.add(prev_city);
        }

        // iterating through the path_cities array list
        for(int i = 0;i<path_cities.size();i++){
            // printing the i'th element from reverse since the array list is reversed
            if(i==path_cities.size()-1){
                System.out.println(path_cities.get(path_cities.size()-1-i).cityName);
                break; // break if it is the first element of the list
            }
            System.out.print(path_cities.get(path_cities.size()-1-i).cityName+" -> "); // print the current city name
        }


        // setting the canvas size and scales so that the window fits to the screen
        StdDraw.setCanvasSize(2377/2,1055/2);
        StdDraw.setXscale(0,2377);
        StdDraw.setYscale(0,1055);

        StdDraw.picture(2377/2.0,1055/2.0,"Navigation App for Turkey/turkey_map.png",2377,1055); // draws the map of Turkey
        StdDraw.enableDoubleBuffering(); // enable double buffering to improve performance
        StdDraw.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,12)); // setting the font size

        //drawing connections of each city by iterating each city
        for(City city : cities){
            StdDraw.setPenColor(new Color(128,128,128)); // setting the pen color to gray
            StdDraw.text(city.x,city.y+15, city.cityName); // printing the name of the city as text on the canvas
            StdDraw.filledCircle(city.x,city.y,5); // drawing a filled circle to represent each city
            // iterate through the connections
            for(City connectedCity : city.connections){
                StdDraw.line(city.x,city.y,connectedCity.x,connectedCity.y); // draw a line from thc city to its connection
            }
        }

        // if there is only one city in the path, paint it blue
        if(path_cities.size()==1) {
            StdDraw.setPenColor(new Color(128, 194, 237)); // set pen color to cyan
            StdDraw.text(path_cities.get(0).x, path_cities.get(0).y + 15, path_cities.get(0).cityName); // print the name of the city
            StdDraw.filledCircle(path_cities.get(0).x,path_cities.get(0).y,9); // draw the filled circle for the city
        }
        else{
            StdDraw.setPenRadius(0.009); // set the pen radius to 0.009
            // iterating through the path cities in order to trace back and in each iteration, draw the connections with cyan to indicate the path
            for(int i = 0;i< path_cities.size()-1;i++){
                StdDraw.setPenColor(new Color(128, 194, 237)); // set pen color to cyan
                StdDraw.filledCircle(path_cities.get(i).x,path_cities.get(i).y,9); // redrawing the filled circle in cyan
                StdDraw.text(path_cities.get(i).x, path_cities.get(i).y + 15, path_cities.get(i).cityName); // reprinting the name of the city in cyan
                StdDraw.line(path_cities.get(i).x,path_cities.get(i).y,path_cities.get(i+1).x,path_cities.get(i+1).y); // redrawing the line in cyan to indicate path
            }
            // make same drawings except the line for the last city
            StdDraw.text(path_cities.getLast().x, path_cities.getLast().y + 15, path_cities.getLast().cityName);
            StdDraw.filledCircle(path_cities.getLast().x,path_cities.getLast().y,7);
        }

        StdDraw.show();

        coords_scanner.close(); // close the scanner object
        connection_scanner.close(); // close the scanner object
        input.close(); // close the scanner object

    }
}
