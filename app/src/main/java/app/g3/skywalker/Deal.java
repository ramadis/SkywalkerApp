package app.g3.skywalker;

/**
 * Created by rama on 23/06/17.
 */

public class Deal {
    String city;
    Integer id;
    Double price;

    Deal(Integer id, String city, Double price) {
        this.city = city;
        this.id = id;
        this.price = price;
    }
}