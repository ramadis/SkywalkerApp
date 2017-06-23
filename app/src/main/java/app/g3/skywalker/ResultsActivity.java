package app.g3.skywalker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {

    private List<Person> persons;

    // This method creates an ArrayList that has three Person objects
// Checkout the project associated with this tutorial on Github if
// you want to use the same images.
    private void initializeData() {
        persons = new ArrayList<>();
        persons.add(new Person("Emma Wilson", "23 years old"));
        persons.add(new Person("Lavery Maiss", "25 years old"));
        persons.add(new Person("Lillie Watts", "35 years old"));
        persons.add(new Person("Lillie Watt=s", "35 years old"));
        persons.add(new Person("Lillie Wat", "35 years old"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        initializeData();

        RecyclerView rv = (RecyclerView) findViewById(R.id.resultsRV);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        ResultsAdapter adapter = new ResultsAdapter(persons);
        rv.setAdapter(adapter);
    }
}


