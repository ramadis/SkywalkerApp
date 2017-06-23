package app.g3.skywalker;

/**
 * Created by rama on 23/06/17.
 */

public class City {
    public String id;
    public String name;
    public Double latitude;
    public Double longitude;
    public Country country;

    public String toString(){
        return name;
    }
}