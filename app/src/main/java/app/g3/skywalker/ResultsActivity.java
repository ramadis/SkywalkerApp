package app.g3.skywalker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {

    private List<Flight> flights;

    // This method creates an ArrayList that has three Person objects
// Checkout the project associated with this tutorial on Github if
// you want to use the same images.
    private void initializeData() {
        flights = new ArrayList<>();/*
        persons.add(new Person("Emma Wilson", "23 years old"));
        persons.add(new Person("Lavery Maiss", "25 years old"));
        persons.add(new Person("Lillie Watts", "35 years old"));
        persons.add(new Person("Lillie Watt=s", "35 years old"));
        persons.add(new Person("Lillie Wat", "35 years old"));*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Get activity params
        Bundle b = getIntent().getExtras();
        String searchValue = b.getString("value");

        // Initialize items
        initializeData();

        // Define recycleview
        RecyclerView rv = (RecyclerView) findViewById(R.id.resultsRV);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        ResultsAdapter adapter = new ResultsAdapter(flights, this, searchValue);

        // Hide not flights message
        findViewById(R.id.noResultsMessage).setVisibility(View.GONE);
        findViewById(R.id.paperPlane).setVisibility(View.GONE);
        findViewById(R.id.resultsRV).setVisibility(View.VISIBLE);

        //api call
        adapter.getResults();

        // set adapter
        rv.setAdapter(adapter);
    }
}


