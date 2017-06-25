package app.g3.skywalker;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.MapView;
import com.google.gson.Gson;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment fragment;
    FragmentManager fragmentManager = getFragmentManager(); // For AppCompat use getSupportFragmentManager
    Handler handler;
    Integer notificationId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            fragment = getFragmentManager().getFragment(savedInstanceState, "fragment");
        }


        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        handler = new Handler();
        notificationId = 0;

        // Fixing Later Map loading Delay
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MapView mv = new MapView(getApplicationContext());
                    mv.onCreate(null);
                    mv.onPause();
                    mv.onDestroy();
                }catch (Exception ignored){

                }
            }
        }).start();

        // Notification thread
        Intent notifyIntent = new Intent(MenuActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MenuActivity.this, 0, notifyIntent,0);
        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        // alarm.setRepeating(AlarmManager.RTC, 0, Utils.get().interval * 60 * 1000, pendingIntent);
        alarm.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + Utils.get().interval, 1000, pendingIntent);

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {
                        Thread.sleep(Utils.get().interval * 60 * 1000);
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                // Write your code here to update the UI.
                                notificationService();
                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();*/

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //window.setStatusBarColor(Color.parseColor("#ffffff"));
        }


        LocationManager locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        boolean isGPSEnabledboolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabledboolean) {
            try {
                Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Utils u = Utils.get();
                String latitude = ((Double) loc.getLatitude()).toString();
                String longitude = ((Double) loc.getLongitude()).toString();
                getCity(latitude, longitude);
            } catch (SecurityException t) {}
        }

        /*fragment = new SearchFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.content_menu, fragment)
                .commit();*/
    }

    public void notificationService() {
        final Intent emptyIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, -1, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_audiotrack)
                        .setContentTitle("Test")
                        .setContentText("test Message")
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId++, mBuilder.build());
        ConnectivityManager cn=(ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo nf=cn.getActiveNetworkInfo();
        //if(nf != null && nf.isConnected()==true ) notifySubscriptedFlights();
        //else Toast.makeText(this, getString(R.string.not_connected_message), Toast.LENGTH_SHORT).show();
    }

    public void notifySubscriptedFlights() {
        Storage storage = SimpleStorage.getInternalStorage(this);
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
        RequestQueue queue = Volley.newRequestQueue(this);
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
        Storage storage = SimpleStorage.getInternalStorage(this);
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
        PendingIntent pendingIntent = PendingIntent.getActivity(this, -1, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

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

        messages.put("changedStatus", getString(R.string.notification_message_status));
        messages.put("changedArrivalTime", getString(R.string.notification_message_arrival_time));
        messages.put("changedDepartureTime", getString(R.string.notification_message_departure_time));
        messages.put("changedArrivalTerminal", getString(R.string.notification_message_arrival_terminal));
        messages.put("changedDepartureTerminal", getString(R.string.notification_message_departure_terminal));
        messages.put("changedArrivalGate", getString(R.string.notification_message_arrival_gate));
        messages.put("changedDepartureGate", getString(R.string.notification_message_departure_gate));
        messages.put("changedBaggage", getString(R.string.notification_message_baggage));

        values.put("changedStatus", newFlight.status);
        values.put("changedArrivalTime", newFlight.arrival.scheduled_time);
        values.put("changedDepartureTime", newFlight.departure.scheduled_time);
        values.put("changedArrivalTerminal", newFlight.arrival.airport.terminal);
        values.put("changedDepartureTerminal", newFlight.departure.airport.terminal);
        values.put("changedArrivalGate", newFlight.arrival.airport.gate);
        values.put("changedDepartureGate", newFlight.departure.airport.gate);
        values.put("changedBaggage", newFlight.arrival.airport.baggage);

        status.put("L",getString(R.string.flights_status_l));
        status.put("R",getString(R.string.flights_status_r));
        status.put("S",getString(R.string.flights_status_s));
        status.put("A",getString(R.string.flights_status_a));
        status.put("C",getString(R.string.flights_status_c));

        for(String key: updated.keySet()) {
            shouldUpdate = shouldUpdate || updated.get(key);
            if (updated.get(key)) {
                message += messages.get(key) + (key.equals("changedStatus") ? status.get(values.get(key)) : values.get(key)) + ".\n";
            }
        }

        if (!shouldUpdate) return false;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_audiotrack)
                        .setContentTitle(getString(R.string.the_flight) + " " + newFlight.airline.id + newFlight.number.toString() + " " + getString(R.string.modified_message))
                        .setContentText(message)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId++, mBuilder.build());

        return shouldUpdate;
    }

    public void getCity(String latitude, String longitude) {
        RequestQueue queue = Volley.newRequestQueue(this);
        // http://hci.it.itba.edu.ar/v1/api/geo.groovy?method=getairportsbyposition&latitude=-34.60&longitude=-58.38&radius=40
        String url ="http://hci.it.itba.edu.ar/v1/api/geo.groovy?method=getairportsbyposition&latitude="+latitude+"&longitude="+longitude;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject root = new JSONObject(response);
                            String airportsString = root.getJSONArray("airports").toString();
                            List<Airport> airports = (List<Airport>) new Gson().fromJson(airportsString, Airport.class);

                            if (airports.size() <= 0) return;

                            String city = airports.get(0).city.id;
                            Utils.get().cityId = city;
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
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_subscriptions) {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.getMenu().getItem(2).setChecked(true);

            fragment = new SubscriptionsFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_menu, fragment)
                    .commit();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the fragment's instance
        getFragmentManager().putFragment(outState, "fragment", fragment);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            fragment = new SearchFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_menu, fragment)
                    .commit();

        } else if (id == R.id.nav_gallery) {
            Intent myIntent = new Intent(MenuActivity.this, MapDealActivity.class);
            //myIntent.putExtra("key", value); //Optional parameters
            MenuActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_slideshow) {
            fragment = new SubscriptionsFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_menu, fragment)
                    .commit();
        } else if (id == R.id.nav_manage) {
            fragment = new SettingsFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_menu, fragment)
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
