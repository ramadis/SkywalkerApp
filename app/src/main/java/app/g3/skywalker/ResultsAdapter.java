package app.g3.skywalker;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
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
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rama on 23/06/17.
 */


public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.PersonViewHolder>{

    List<Person> persons;
    Context context;

    // TODO: Receive context to get resources.
    ResultsAdapter(List<Person> persons, Context context){
        this.persons = persons;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_result, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

   /* public void getDeals() {


        RequestQueue queue = Volley.newRequestQueue(this.context);
        String url ="http://hci.it.itba.edu.ar/v1/api/booking.groovy?method=getflightdeals&from=BUE";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //Log.d("test", "Response is: "+ response.substring(0,500));
                        persons.clear();

                        try {
                            JSONObject root = new JSONObject(response);
                            String dealsString = root.getJSONArray("deals").toString();
                            Type listType = new TypeToken<ArrayList<DealRequest>>(){}.getType();
                            List<DealRequest> newDealsRequest = new Gson().fromJson(dealsString, listType);
                            List<Deal> newDeals = new ArrayList<>();

                            for (DealRequest d: newDealsRequest) {
                                newDeals.add(new Deal(1, d.city, d.price));
                            }

                            persons.addAll(newDeals);
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
        // ENDPOINT: http://hci.it.itba.edu.ar/v1/api/booking.groovy?method=getonewayflights&from=BUE&to=TUC&dep_date=2017-12-25&adults=1&children=0&infants=0
    }*/

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        personViewHolder.personName.setText(persons.get(i).name);
        personViewHolder.personAge.setText(persons.get(i).age);
        personViewHolder.btn.setOnClickListener(personViewHolder);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        CardView cv;
        TextView personName;
        TextView personAge;
        Button btn;

        public void onClick(View v) {
            btn.setText("DESUSCRIBIRSE");
            btn.setBackgroundColor(Color.parseColor("#d8ad56"));
            Log.d("test", "test");
        }

        PersonViewHolder(View itemView) {
            // TODO: Receive also state for the button to know which text and style to show?
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cardResultElement);
            btn = (Button) cv.findViewById(R.id.subscribe_button);
            personName = (TextView)itemView.findViewById(R.id.person_name);
            personAge = (TextView)itemView.findViewById(R.id.person_age);
        }
    }

}
