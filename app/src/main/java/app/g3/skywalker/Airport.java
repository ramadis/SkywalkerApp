package app.g3.skywalker;

import java.io.Serializable;

/**
 * Created by rama on 24/06/17.
 */

public class Airport implements Serializable {
    String id;
    String description;
    String time_zone;
    Double latitude;
    Double longitude;
    City city;
    String terminal;
    String gate;
    String baggage;
}
