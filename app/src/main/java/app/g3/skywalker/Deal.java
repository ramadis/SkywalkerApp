package app.g3.skywalker;

/**
 * Created by rama on 23/06/17.
 */

public class Deal {
    private City city;
    private Integer id;
    private Double price;

    Deal(Integer id, City city, Double price) {
        this.city = city;
        this.id = id;
        this.price = price;
    }

    public City getCity() {
        return this.city;
    }

    public Double getPrice() {
        return this.price;
    }
}