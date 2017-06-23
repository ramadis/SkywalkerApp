package app.g3.skywalker;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        personViewHolder.city.setText(deals.get(i).city);
        personViewHolder.price.setText(context.getResources().getString(R.string.price_deal_tag) + deals.get(i).price.toString());
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
