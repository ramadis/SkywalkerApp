package app.g3.skywalker;

/**
 * Created by rama on 23/06/17.
 */

public class Deal {
    City city;
    Integer id;
    Double price;

    Deal(Integer id, City city, Double price) {
        this.city = city;
        this.id = id;
        this.price = price;
    }
}