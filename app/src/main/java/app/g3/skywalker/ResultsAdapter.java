package app.g3.skywalker;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rama on 23/06/17.
 */


public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.FlightViewHolder>{

    List<Flight> flights;
    Context context;
    String searchValue;

    // TODO: Receive context to get resources.
    ResultsAdapter(List<Flight> flights, Context context){
        this.flights = flights;
        this.context = context;
    }

    ResultsAdapter(List<Flight> flights, Context context, String searchValue){
        this.flights = flights;
        this.context = context;
        this.searchValue = searchValue;
    }

    @Override
    public int getItemCount() {
        return flights.size();
    }

    public void getResults() {
        Pattern p = Pattern.compile("^[a-zA-Z0-9]{2} ?\\d{4,6}$");
        Matcher m = p.matcher(this.searchValue);
        boolean isFlight = m.matches();

        if (isFlight) {
            String airlineId = this.searchValue.substring(0,2);
            String flightNumber = this.searchValue.substring(2);
            Log.d("airline", airlineId);
            Log.d("flight", flightNumber);
            getResultsWithFlight(flightNumber, airlineId);
        }
    }

    @Override
    public FlightViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_result, viewGroup, false);
        FlightViewHolder pvh = new FlightViewHolder(v);
        return pvh;
    }

    public void getResultsWithFlight(String flightId, String airlineId) {
        RequestQueue queue = Volley.newRequestQueue(this.context);
        String url ="http://hci.it.itba.edu.ar/v1/api/status.groovy?method=getflightstatus&airline_id=" + airlineId + "&flight_number=" + flightId;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        flights.clear();
                        Log.d("checkpoint",response);
                        try {
                            JSONObject root = new JSONObject(response);
                            String flightString = root.getJSONObject("status").toString();
                            Flight flight = new Gson().fromJson(flightString, Flight.class);
                            List<Flight> newFlights = new ArrayList<>();
                            newFlights.add(flight);
                            flights.addAll(newFlights);
                            notifyDataSetChanged();
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

    @Override
    public void onBindViewHolder(FlightViewHolder personViewHolder, int i) {
        Log.d("name", flights.get(i).airline.name);
        personViewHolder.airlineName.setText(flights.get(i).airline.name);
        personViewHolder.fromToShort.setText(flights.get(i).departure.airport.id + " to " + flights.get(i).arrival.airport.id);
        personViewHolder.btn.setOnClickListener(personViewHolder);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class FlightViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        CardView cv;
        TextView airlineName;
        TextView fromToShort;
        Button btn;

        public void onClick(View v) {
            btn.setText("DESUSCRIBIRSE");
            btn.setBackgroundColor(Color.parseColor("#d8ad56"));
            Log.d("test", "test");
        }

        FlightViewHolder(View itemView) {
            // TODO: Receive also state for the button to know which text and style to show?
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cardResultElement);
            btn = (Button) cv.findViewById(R.id.subscribe_button);
            airlineName = (TextView)itemView.findViewById(R.id.airline_name);
            fromToShort = (TextView)itemView.findViewById(R.id.from_to_shorts);
        }
    }

}
