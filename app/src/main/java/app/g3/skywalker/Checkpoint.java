package app.g3.skywalker;

import java.io.Serializable;

/**
 * Created by rama on 24/06/17.
 */

public class Checkpoint implements Serializable {
    Airport airport;
    String scheduled_time;
    String actual_time;
    String actual_get_time;
    String gate_delay;
    String estimate_runway_time;
    String actual_runway_time;
    String runway_delay;
}
