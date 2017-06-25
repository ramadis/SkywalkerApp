package app.g3.skywalker;

import android.app.Activity;
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
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rama on 23/06/17.
 */


public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.FlightViewHolder>{

    List<Flight> flights;
    List<Flight> subscribed;
    Context context;
    String searchValue;

    ResultsAdapter(List<Flight> flights, Context context, String searchValue){
        this.flights = flights;
        this.context = context;
        this.searchValue = searchValue;
        subscribed = new ArrayList<>();
    }

    public void getSubscriptions() {
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
            if (newFlights.size() <= 0) return;
            subscribed = newFlights;
        } catch(Throwable e) {
            Log.d("error", e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return flights.size();
    }

    public void getResults() {
        Pattern p = Pattern.compile("^[a-zA-Z0-9]{2} ?\\d{1,6}$");
        Matcher m = p.matcher(this.searchValue);
        boolean isFlight = m.matches();

        if (isFlight) {
            String airlineId = this.searchValue.substring(0,2).toUpperCase();
            String flightNumber = this.searchValue.substring(2).trim();
            Log.d("airline", airlineId);
            Log.d("flight", flightNumber);
            getResultsWithFlight(flightNumber, airlineId);
        }
    }

    @Override
    public FlightViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_result, viewGroup, false);
        FlightViewHolder pvh = new FlightViewHolder(v, this);
        return pvh;
    }

    public void unsuscribeFromCode(String id) {
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

            Flight toDeleteFlight = null;
            for(Flight f: newFlights) {
                String fId = f.airline.id + f.number.toString();

                if (fId.equals(id)) {
                    toDeleteFlight = f;
                    break;
                }
            }
            if (toDeleteFlight == null) return;

            storage.deleteFile("Skywalker", "Subscriptions");


            newFlights.remove(toDeleteFlight);
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(newFlights);
            storage.createFile("Skywalker", "Subscriptions", bo.toByteArray());
        } catch(Throwable e) {
            Log.d("error", e.toString());
        }
    }

    public void storeResultFromCode(String id) {
        Storage storage = SimpleStorage.getInternalStorage(context);
        boolean dirExists = storage.isDirectoryExists("Skywalker");
        if (!dirExists) storage.createDirectory("Skywalker");
        boolean fileExists = storage.isFileExist("Skywalker", "Subscriptions");
        // TODO: if the file exists, load it and update arraylist.

        // Find the flight to add
        Flight toAddFlight = null;
        for(Flight f: flights) {
            String fId = f.airline.id + f.number.toString();

            if (fId.equals(id)) {
                toAddFlight = f;
                break;
            }
        }
        if (toAddFlight == null) return;


        if (fileExists) {
            try {
                byte[] bytes = storage.readFile("Skywalker", "Subscriptions");
                ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
                ObjectInputStream si = new ObjectInputStream(bi);
                List<Flight> newFlights = (List<Flight>) si.readObject();

                if (newFlights.contains(toAddFlight)) return;

                storage.deleteFile("Skywalker", "Subscriptions");

                newFlights.add(toAddFlight);
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                ObjectOutputStream so = new ObjectOutputStream(bo);
                so.writeObject(newFlights);
                storage.createFile("Skywalker", "Subscriptions", bo.toByteArray());
            } catch(Throwable e) {
                Log.d("error", e.toString());
            }
        }

        if (!fileExists) {
            try {
                List<Flight> newFlights = new ArrayList<>();

                newFlights.add(toAddFlight);

                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                ObjectOutputStream so = new ObjectOutputStream(bo);
                so.writeObject(newFlights);
                storage.createFile("Skywalker", "Subscriptions", bo.toByteArray());
            } catch(Throwable e) {
                Log.d("error", e.toString());
            }
        }
    }

    // Load single flight into results list
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

                            if (root.has("error")) {
                                ((ResultsActivity) context).findViewById(R.id.noResultsMessage).setVisibility(View.VISIBLE);
                                ((ResultsActivity) context).findViewById(R.id.paperPlane).setVisibility(View.VISIBLE);
                                ((ResultsActivity) context).findViewById(R.id.resultsRV).setVisibility(View.GONE);
                                return;
                            }

                            // If there are results, load alredy subscribed flights into subscriptions.
                            getSubscriptions();

                            String flightString = root.getJSONObject("status").toString();
                            Flight flight = new Gson().fromJson(flightString, Flight.class);

                            List<Flight> newFlights = new ArrayList<>();
                            newFlights.add(flight);

                            //storeResults(newFlights);

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
        Flight f = flights.get(i);
        Log.d("name", flights.get(i).airline.name);
        personViewHolder.airlineName.setText(flights.get(i).airline.name.toUpperCase());
        personViewHolder.fromToShort.setText(flights.get(i).departure.airport.id + " to " + flights.get(i).arrival.airport.id);
        personViewHolder.flightCode.setText(flights.get(i).airline.id + flights.get(i).number);

        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            Date date = parser.parse(f.departure.scheduled_time);
            String formattedDate = formatter.format(date);
            personViewHolder.result_time_dep.setText(context.getString(R.string.result_time_dep) + ": " + formattedDate);
        } catch(Throwable ignore) {
            personViewHolder.result_time_dep.setText(context.getString(R.string.result_time_dep) + ": -");
        }

        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            Date date = parser.parse(f.arrival.scheduled_time);
            String formattedDate = formatter.format(date);
            personViewHolder.result_time_arr.setText(context.getString(R.string.result_time_arr) + ": " + formattedDate);
        } catch(Throwable ignore) {
            personViewHolder.result_time_arr.setText(context.getString(R.string.result_time_arr) + ": -");
        }

        personViewHolder.result_term_dep.setText(context.getString(R.string.result_term_dep) + ": " + Utils.get().camulfage("-", f.departure.airport.terminal));
        personViewHolder.result_term_arr.setText(context.getString(R.string.result_term_arr) + ": " +  Utils.get().camulfage("-", f.arrival.airport.terminal));
        personViewHolder.result_gate_dep.setText(context.getString(R.string.result_gate_dep) + ": " +  Utils.get().camulfage("-", f.departure.airport.gate));
        personViewHolder.result_gate_arr.setText(context.getString(R.string.result_gate_arr) + ": " +  Utils.get().camulfage("-", f.arrival.airport.gate));

        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            Date date = parser.parse(f.arrival.scheduled_time);
            Date date2 = parser.parse(f.departure.scheduled_time);
            String formattedDate = formatter.format(date);
            String formattedDate2 = formatter.format(date2);
            personViewHolder.small_time.setText(formattedDate2 + " - " + formattedDate);
        } catch(Throwable ignore) {
            personViewHolder.small_time.setText("");
        }

        personViewHolder.btn.setOnClickListener(personViewHolder);

        if (subscribed.contains(flights.get(i))) {
            personViewHolder.toggleButton();
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class FlightViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        CardView cv;
        TextView airlineName;
        TextView fromToShort;
        TextView flightCode;
        TextView result_time_dep;
        TextView result_time_arr;
        TextView result_term_dep;
        TextView result_term_arr;
        TextView result_gate_arr;
        TextView small_time;
        TextView result_gate_dep;
        boolean btnState;
        Button btn;
        ResultsAdapter context;

        public void toggleButton() {
            btnState = !btnState;
            btn.setText(btnState ? context.context.getString(R.string.unsuscribe_action) : context.context.getString(R.string.subscribe_action) );
            btn.setSelected(btnState);
            if (btnState) {
                context.storeResultFromCode(flightCode.getText().toString());
            } else {
                context.unsuscribeFromCode(flightCode.getText().toString());
            }
        }

        public void onClick(View v) {
            toggleButton();
        }

        FlightViewHolder(View itemView, ResultsAdapter context) {
            // TODO: Receive also state for the button to know which text and style to show?
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cardResultElement);
            btn = (Button) cv.findViewById(R.id.subscribe_button);
            airlineName = (TextView)itemView.findViewById(R.id.airline_name);
            fromToShort = (TextView)itemView.findViewById(R.id.from_to_shorts);
            flightCode = (TextView)itemView.findViewById(R.id.flightCode);
            result_time_dep = (TextView)itemView.findViewById(R.id.result_time_dep);
            result_time_arr = (TextView)itemView.findViewById(R.id.result_time_arr);
            result_term_dep = (TextView)itemView.findViewById(R.id.result_term_dep);
            result_term_arr = (TextView)itemView.findViewById(R.id.result_term_arr);
            result_gate_arr = (TextView)itemView.findViewById(R.id.result_gate_arr);
            result_gate_dep = (TextView)itemView.findViewById(R.id.result_gate_dep);
            small_time = (TextView)itemView.findViewById(R.id.small_time);
            btnState = false;
            this.context = context;
        }
    }

}
