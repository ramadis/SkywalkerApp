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

import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Created by rama on 23/06/17.
 */


public class SubscriptionsAdapter extends RecyclerView.Adapter<SubscriptionsAdapter.FlightViewHolder>{

    List<Flight> flights;
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
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_result, viewGroup, false);
        SubscriptionsAdapter.FlightViewHolder pvh = new SubscriptionsAdapter.FlightViewHolder(v);
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
            flights.addAll(newFlights);
            notifyDataSetChanged();
        } catch(Throwable e) {}
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
            // TODO: Remove element
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
        personViewHolder.airlineName.setText(flights.get(i).airline.name);
        personViewHolder.fromToShort.setText(flights.get(i).departure.airport.id + " to " + flights.get(i).arrival.airport.id);
        personViewHolder.btn.setOnClickListener(personViewHolder);
        personViewHolder.btn.setTag(flights.get(i).airline.id + flights.get(i).number.toString());
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
            // TODO: Lookup how the hell to reference this methods.
            removeSubscription(v.getTag().toString());
            getSubscriptions();
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
