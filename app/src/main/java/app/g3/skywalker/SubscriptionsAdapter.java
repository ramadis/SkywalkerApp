package app.g3.skywalker;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by rama on 23/06/17.
 */


public class SubscriptionsAdapter extends RecyclerView.Adapter<SubscriptionsAdapter.FlightViewHolder>{

    List<Flight> flights;
    View rootView;
    Context context;
    SubscriptionsAdapter(List<Flight> flights, Context context){
        this.flights = flights;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return flights.size();
    }

    @Override
    public SubscriptionsAdapter.FlightViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_unsuscribe, viewGroup, false);
        SubscriptionsAdapter.FlightViewHolder pvh = new SubscriptionsAdapter.FlightViewHolder(v, this);
        return pvh;
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

            if (newFlights.size() <= 0) {
                // TODO: How to access view from adapter?
                try {
                    ((Activity) context).findViewById(R.id.noResultsMessage).setVisibility(View.VISIBLE);
                    ((Activity) context).findViewById(R.id.planeImageSubs).setVisibility(View.VISIBLE);
                    ((Activity) context).findViewById(R.id.subscriptionsRV).setVisibility(View.GONE);
                    ((Activity) context).findViewById(R.id.explanationSubscription).setVisibility(View.GONE);
                } catch (Throwable ignore) {}
                return;
            }

            flights.clear();
            flights.addAll(newFlights);
            notifyDataSetChanged();
        } catch(Throwable e) {
            Log.d("error", e.toString());
        }
    }

    public void removeSubscription(String id) {
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

            newFlights.remove(toDeleteFlight);
            storeSubscriptions(storage, newFlights);
        } catch(Throwable e) {}
    }

    public void storeSubscriptions(Storage storage, List<Flight> flights) {
        boolean dirExists = storage.isDirectoryExists("Skywalker");
        if (!dirExists) storage.createDirectory("Skywalker");
        boolean fileExists = storage.isFileExist("Skywalker", "Subscriptions");
        Log.d("Existe", fileExists? "existe" : "No");
        if(fileExists) storage.deleteFile("Skywalker", "Subscriptions");

        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(flights);
            //Log.d("sofar", "sogoodd");
            //so.flush();
            storage.createFile("Skywalker", "Subscriptions", bo.toByteArray());
            //Log.d("Creado", "creado");
        } catch(Throwable e) {
            Log.d("error", e.toString());
        }
    }

    @Override
    public void onBindViewHolder(SubscriptionsAdapter.FlightViewHolder personViewHolder, int i) {
        Log.d("name", flights.get(i).airline.name);
        Flight f = flights.get(i);
        personViewHolder.airlineName.setText(flights.get(i).airline.name);
        personViewHolder.fromToShort.setText(flights.get(i).departure.airport.id + " to " + flights.get(i).arrival.airport.id);
        personViewHolder.btn.setOnClickListener(personViewHolder);
        personViewHolder.flightCode.setText(flights.get(i).airline.id + flights.get(i).number);
        personViewHolder.btn.setTag(flights.get(i).airline.id + flights.get(i).number.toString());
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
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class FlightViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        CardView cv;
        TextView airlineName;
        TextView fromToShort;
        SubscriptionsAdapter adapter;
        TextView result_time_dep;
        TextView result_time_arr;
        TextView result_term_dep;
        TextView result_term_arr;
        TextView result_gate_arr;
        TextView small_time;
        TextView flightCode;
        TextView result_gate_dep;
        Button btn;

        public void onClick(final View v) {

            new AlertDialog.Builder(adapter.context)
                .setMessage(adapter.context.getString(R.string.unsuscribe_message))
                .setPositiveButton(R.string.unsuscribe_action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.removeSubscription(v.getTag().toString());
                        adapter.getSubscriptions();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();

        }

        FlightViewHolder(View itemView) {
            // TODO: Receive also state for the button to know which text and style to show?
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cardResultElement);
            btn = (Button) cv.findViewById(R.id.subscribe_button);
            airlineName = (TextView)itemView.findViewById(R.id.airline_name);
            fromToShort = (TextView)itemView.findViewById(R.id.from_to_shorts);
        }

        FlightViewHolder(View itemView, SubscriptionsAdapter adapter) {
            // TODO: Receive also state for the button to know which text and style to show?
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cardResultElement);
            btn = (Button) cv.findViewById(R.id.subscribe_button);
            airlineName = (TextView)itemView.findViewById(R.id.airline_name);
            result_time_dep = (TextView)itemView.findViewById(R.id.result_time_dep);
            result_time_arr = (TextView)itemView.findViewById(R.id.result_time_arr);
            result_term_dep = (TextView)itemView.findViewById(R.id.result_term_dep);
            result_term_arr = (TextView)itemView.findViewById(R.id.result_term_arr);
            result_gate_arr = (TextView)itemView.findViewById(R.id.result_gate_arr);
            result_gate_dep = (TextView)itemView.findViewById(R.id.result_gate_dep);
            small_time = (TextView)itemView.findViewById(R.id.small_time);
            fromToShort = (TextView)itemView.findViewById(R.id.from_to_shorts);
            flightCode = (TextView)itemView.findViewById(R.id.flightCode);
            this.adapter = adapter;
        }
    }
}
