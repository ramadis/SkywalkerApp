package app.g3.skywalker;

import android.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MapDealActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<Deal> deals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_deal);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void getDeals() {


        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://hci.it.itba.edu.ar/v1/api/booking.groovy?method=getflightdeals&from=BUE";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //Log.d("test", "Response is: "+ response.substring(0,500));

                        try {
                            JSONObject root = new JSONObject(response);
                            String dealsString = root.getJSONArray("deals").toString();
                            Type listType = new TypeToken<ArrayList<DealRequest>>(){}.getType();
                            List<DealRequest> newDealsRequest = new Gson().fromJson(dealsString, listType);
                            List<Deal> newDeals = new ArrayList<>();

                            for (DealRequest d: newDealsRequest) {
                                Deal deal = new Deal(1, d.city, d.price);
                                LatLng m = new LatLng(deal.getCity().latitude, deal.getCity().longitude);
                                mMap.addMarker(new MarkerOptions().position(m).title("Viaja a " + deal.getCity().name + " a solo USD" + deal.getPrice()));
                            }

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
        // ENDPOINT: http://hci.it.itba.edu.ar/v1/api/booking.groovy?method=getflightdeals&from=BUE
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getDeals();

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("50% de descuento a Sydeny via LATAM"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }
}
