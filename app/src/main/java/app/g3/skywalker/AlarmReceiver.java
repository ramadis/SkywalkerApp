package app.g3.skywalker;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rama on 25/06/17.
 */

public class AlarmReceiver extends BroadcastReceiver {
    static Integer notificationId = 0;
    Context context;

    @Override public void onReceive(Context context, Intent intent) {
        this.context = context;
        notificationService();
    }

    public void notificationService() {
        final Intent emptyIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, -1, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_audiotrack)
                        .setContentTitle(context.getString(R.string.the_flight) + " LA2998 " + context.getString(R.string.modified_message))
                        .setContentText("Su vuelo ahora está Activo\n La terminal de embarque ahora es la terminal J\nLa puerta de embarque ahora es la puerta J")
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId++, mBuilder.build());
        ConnectivityManager cn=(ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo nf=cn.getActiveNetworkInfo();
        //if(nf != null && nf.isConnected()==true ) notifySubscriptedFlights();
        //else Toast.makeText(this, getString(R.string.not_connected_message), Toast.LENGTH_SHORT).show();
    }

    public void notifySubscriptedFlights() {
        Storage storage = SimpleStorage.getInternalStorage(context);
        boolean dirExists = storage.isDirectoryExists("Skywalker");
        if (!dirExists) return;
        boolean fileExists = storage.isFileExist("Skywalker", "Subscriptions");
        if (!fileExists) return;
        try {
            byte[] bytes = storage.readFile("Skywalker", "Subscriptions");
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
            ObjectInputStream si = new ObjectInputStream(bi);
            List<Flight> newFlights = (List<Flight>) si.readObject();

            for( Flight f: newFlights) {
                getFlightStatus(f);
            }
        } catch(Throwable e) {}
    }

    public void getFlightStatus(final Flight flight) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="http://hci.it.itba.edu.ar/v1/api/status.groovy?method=getflightstatus&airline_id=" + flight.airline.id + "&flight_number=" + flight.number;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject root = new JSONObject(response);
                            if (root.has("error")) return;

                            String flightString = root.getJSONObject("status").toString();
                            Flight f = new Gson().fromJson(flightString, Flight.class);
                            if (showNotification(flight, f)) updateFlight(f);
                        } catch (Throwable e) {}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("asd","That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        // ENDPOINT: http://hci.it.itba.edu.ar/v1/api/status.groovy?method=getflightstatus&airline_id=8R&flight_number=8700
    }

    public void updateFlight(Flight f) {
        Storage storage = SimpleStorage.getInternalStorage(context);
        boolean dirExists = storage.isDirectoryExists("Skywalker");
        if (!dirExists) return;
        boolean fileExists = storage.isFileExist("Skywalker", "Subscriptions");
        if (!fileExists) return;

        try {
            byte[] bytes = storage.readFile("Skywalker", "Subscriptions");
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
            ObjectInputStream si = new ObjectInputStream(bi);
            List<Flight> newFlights = (List<Flight>) si.readObject();

            newFlights.remove(f);
            newFlights.add(f);

            storage.deleteFile("Skywalker", "Subscriptions");

            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(newFlights);
            storage.createFile("Skywalker", "Subscriptions", bo.toByteArray());
        } catch(Throwable e) {
            Log.d("error", e.toString());
        }
    }

    public boolean showNotification(Flight storedFlight, Flight newFlight) {
        final Intent emptyIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, -1, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Map<String, Boolean> updated = new HashMap<>();
        Map<String, String> messages = new HashMap<>();
        Map<String, String> values = new HashMap<>();
        Map<String, String> status = new HashMap<>();
        String message = "";

        boolean changedStatus = Utils.get().equalsWithNulls(storedFlight.status, newFlight.status);
        boolean changedArrivalTime = Utils.get().equalsWithNulls(storedFlight.arrival.scheduled_time, newFlight.arrival.scheduled_time);
        boolean changedDepartureTime = Utils.get().equalsWithNulls(storedFlight.departure.scheduled_time, newFlight.departure.scheduled_time);
        boolean changedArrivalTerminal = Utils.get().equalsWithNulls(storedFlight.arrival.airport.terminal, newFlight.arrival.airport.terminal);
        boolean changedDepartureTerminal = Utils.get().equalsWithNulls(storedFlight.departure.airport.terminal, newFlight.departure.airport.terminal);
        boolean changedArrivalGate = Utils.get().equalsWithNulls(storedFlight.arrival.airport.gate, newFlight.arrival.airport.gate);
        boolean changedDepartureGate = Utils.get().equalsWithNulls(storedFlight.departure.airport.gate, newFlight.departure.airport.gate);
        boolean changedBaggage = Utils.get().equalsWithNulls(storedFlight.arrival.airport.baggage, newFlight.arrival.airport.baggage);
        boolean shouldUpdate = false;

        updated.put("changedStatus", changedStatus);
        updated.put("changedArrivalTime", changedArrivalTime);
        updated.put("changedDepartureTime", changedDepartureTime);
        updated.put("changedArrivalTerminal", changedArrivalTerminal);
        updated.put("changedDepartureTerminal", changedDepartureTerminal);
        updated.put("changedArrivalGate", changedArrivalGate);
        updated.put("changedDepartureGate", changedDepartureGate);
        updated.put("changedBaggage", changedBaggage);

        messages.put("changedStatus", context.getString(R.string.notification_message_status));
        messages.put("changedArrivalTime", context.getString(R.string.notification_message_arrival_time));
        messages.put("changedDepartureTime", context.getString(R.string.notification_message_departure_time));
        messages.put("changedArrivalTerminal", context.getString(R.string.notification_message_arrival_terminal));
        messages.put("changedDepartureTerminal", context.getString(R.string.notification_message_departure_terminal));
        messages.put("changedArrivalGate", context.getString(R.string.notification_message_arrival_gate));
        messages.put("changedDepartureGate", context.getString(R.string.notification_message_departure_gate));
        messages.put("changedBaggage", context.getString(R.string.notification_message_baggage));

        values.put("changedStatus", newFlight.status);
        values.put("changedArrivalTime", newFlight.arrival.scheduled_time);
        values.put("changedDepartureTime", newFlight.departure.scheduled_time);
        values.put("changedArrivalTerminal", newFlight.arrival.airport.terminal);
        values.put("changedDepartureTerminal", newFlight.departure.airport.terminal);
        values.put("changedArrivalGate", newFlight.arrival.airport.gate);
        values.put("changedDepartureGate", newFlight.departure.airport.gate);
        values.put("changedBaggage", newFlight.arrival.airport.baggage);

        status.put("L",context.getString(R.string.flights_status_l));
        status.put("R",context.getString(R.string.flights_status_r));
        status.put("S",context.getString(R.string.flights_status_s));
        status.put("A",context.getString(R.string.flights_status_a));
        status.put("C",context.getString(R.string.flights_status_c));

        for(String key: updated.keySet()) {
            shouldUpdate = shouldUpdate || updated.get(key);
            if (updated.get(key)) {
                message += messages.get(key) + (key.equals("changedStatus") ? status.get(values.get(key)) : values.get(key)) + ".\n";
            }
        }

        if (!shouldUpdate) return false;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_audiotrack)
                        .setContentTitle(context.getString(R.string.the_flight) + " " + newFlight.airline.id + newFlight.number.toString() + " " + context.getString(R.string.modified_message))
                        .setContentText(message)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId++, mBuilder.build());

        return shouldUpdate;
    }
}