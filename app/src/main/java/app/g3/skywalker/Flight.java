package app.g3.skywalker;

import java.io.Serializable;

/**
 * Created by rama on 24/06/17.
 */

public class Flight implements Serializable {
    Integer id;
    Integer number;
    Airline airline;
    String status;
    Checkpoint departure;
    Checkpoint arrival;

    @Override
    public boolean equals(Object f) {
        if(f instanceof Flight) {
            Flight flight = (Flight) f;
            return flight.id.equals(this.id);
        }
        return false;
    }
}
