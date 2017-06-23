package app.g3.skywalker;

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

import java.util.List;

/**
 * Created by rama on 23/06/17.
 */


public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.PersonViewHolder>{

    List<Person> persons;

    // TODO: Receive context to get resources.
    ResultsAdapter(List<Person> persons){
        this.persons = persons;
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
