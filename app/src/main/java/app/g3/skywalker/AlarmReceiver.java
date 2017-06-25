package app.g3.skywalker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by rama on 25/06/17.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        MenuActivity m = new MenuActivity();
        m.notificationService();
    }
}