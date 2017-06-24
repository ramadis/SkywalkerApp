package app.g3.skywalker;

/**
 * Created by rama on 24/06/17.
 */

public class Utils {
    private static Utils instance;
    String cityId;

    private Utils(){
        this.cityId = "BUE";
    }

    public static Utils get() {
        if (instance == null) instance = new Utils();
        return instance;
    }
}
