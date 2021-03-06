package app.g3.skywalker;

import android.app.Fragment;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private List<Deal> deals;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        initializeData();

        // Create rv
        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.dealViewRV);

        // Assign linear layout
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        // Attach adapter
        DealsAdapter adapter = new DealsAdapter(deals, getActivity());
        adapter.getDeals();
        rv.setAdapter(adapter);
        rv.setHasFixedSize(true);
        rv.setItemViewCacheSize(3);
        rv.setDrawingCacheEnabled(true);
        rv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        // Search button behaviour
        AutoCompleteTextView field = (AutoCompleteTextView) rootView.findViewById(R.id.searchField);
        final Button btn = (Button) rootView.findViewById(R.id.searchButtonAction);
        btn.setEnabled(false);

        field.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // String cityRegex = "|(^[a-zA-Z ,.]+$)";
                Pattern p = Pattern.compile("(^[a-zA-Z0-9]{2} ?\\d{1,6}$)");
                Matcher m = p.matcher(s.toString());
                boolean b = m.matches();

                //btn.setEnabled(s.toString().trim().length()!=0);
                btn.setEnabled(b);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), ResultsActivity.class);
                String value = ((AutoCompleteTextView) rootView.findViewById(R.id.searchField)).getText().toString();
                myIntent.putExtra("value", value);
                getActivity().startActivity(myIntent);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's state here
    }

    //ENDPOINT: http://hci.it.itba.edu.ar/v1/api/misc.groovy?method=getairlines

    private void initializeData() {
        deals = new ArrayList<>();
        /*deals.add(new Deal(1,"Emma Wilson", 500.0));
        deals.add(new Deal(2,"Lavery Maiss", 600.0));
        deals.add(new Deal(3,"Lillie Watts", 700.0));
        deals.add(new Deal(4,"Lillie Watt=s", 800.0));
        deals.add(new Deal(5,"Lillie Wat", 900.0));*/
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSearchFragmentInteraction(Uri uri);
    }
}



    // This method creates an ArrayList that has three Person objects
// Checkout the project associated with this tutorial on Github if
// you want to use the same images.
