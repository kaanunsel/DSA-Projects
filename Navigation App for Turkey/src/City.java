import java.util.ArrayList;

/**
 * Represents a city in Turkey.
 * @author Mehmet Kaan Unsel
 * @since Date: 23.03.2024
 */
public class City {
    public String cityName;
    public int x;
    public int y;
    public ArrayList<City> connections;
    public boolean visited = false;
    public int id;
    public City prev_node = null;
    public double distance = Double.MAX_VALUE;

    /**
     *
     * @param cityName name of the city
     * @param x x coordinate of the city
     * @param y y coordinate of the city
     * @param id id of the city
     */
    public City(String cityName, int x, int y, int id){
        this.cityName = cityName;
        this.x = x;
        this.y = y;
        this.id = id;
        connections = new ArrayList<>();
    }

}
