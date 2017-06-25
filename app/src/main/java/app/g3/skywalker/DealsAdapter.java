package app.g3.skywalker;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rama on 23/06/17.
 */

public class DealsAdapter extends RecyclerView.Adapter<DealsAdapter.DealViewHolder>{

    List<Deal> deals;
    Context context;

    DealsAdapter(List<Deal> deals, Context context){
        this.deals = deals;
        this.context = context;
    }

    public void getDeals() {


        RequestQueue queue = Volley.newRequestQueue(this.context);
        String url ="http://hci.it.itba.edu.ar/v1/api/booking.groovy?method=getflightdeals&from=" + Utils.get().cityId;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //Log.d("test", "Response is: "+ response.substring(0,500));
                        deals.clear();

                        try {
                            JSONObject root = new JSONObject(response);

                            if (root.has("error")) return;

                            String dealsString = root.getJSONArray("deals").toString();
                            Type listType = new TypeToken<ArrayList<DealRequest>>(){}.getType();
                            List<DealRequest> newDealsRequest = new Gson().fromJson(dealsString, listType);
                            List<Deal> newDeals = new ArrayList<>();

                            for (DealRequest d: newDealsRequest) {
                                newDeals.add(new Deal(1, d.city, d.price));
                            }

                            deals.addAll(newDeals);
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
        // ENDPOINT: http://hci.it.itba.edu.ar/v1/api/booking.groovy?method=getflightdeals&from=BUE
    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    @Override
    public DealViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_deal, viewGroup, false);
        DealViewHolder pvh = new DealViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(DealViewHolder personViewHolder, int i) {
        personViewHolder.city.setText(deals.get(i).getCity().toString());
        personViewHolder.price.setText(context.getResources().getString(R.string.price_deal_tag) + deals.get(i).getPrice().toString());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class DealViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView city;
        TextView price;

        DealViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cardDealElement);
            price = (TextView) itemView.findViewById(R.id.deal_price);
            city = (TextView)itemView.findViewById(R.id.deal_city);
        }
    }

}
