package app.g3.skywalker;

import java.io.Serializable;

/**
 * Created by rama on 23/06/17.
 */

public class City implements Serializable {
    public String id;
    public String name;
    public Double latitude;
    public Double longitude;
    public Country country;

    public String toString(){
        String[] parts = name.split(",");
        return parts[0];
    }
}