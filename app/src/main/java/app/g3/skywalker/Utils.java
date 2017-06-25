package app.g3.skywalker;

/**
 * Created by rama on 24/06/17.
 */

public class Utils {
    private static Utils instance;
    String cityId;
    Integer interval;

    private Utils(){
        this.interval = 5;
        this.cityId = "BUE";
    }

    public boolean equalsWithNulls(Object a, Object b) {
        if (a==b) return true;
        if ((a==null)||(b==null)) return false;
        return a.equals(b);
    }

    public static Utils get() {
        if (instance == null) instance = new Utils();
        return instance;
    }
}
