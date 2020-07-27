package me.nathan.smsabuse_sf.ui.main;

import android.os.Bundle;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.*;
import android.widget.*;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import me.nathan.smsabuse_sf.MainActivity;
import me.nathan.smsabuse_sf.R;

/**
 * The number selection menu, nothing special atm
 */
public class Numbers extends Fragment {
    static ArrayList<String> numbers = new ArrayList<>();
    public Numbers() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_numbers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FloatingActionButton fab = view.findViewById(R.id.numbers);
        fab.setOnClickListener((v) -> MainActivity.self.requestNumbers());
        ListView lv = MainActivity.self.findViewById(R.id.numberList);
        lv.setAdapter(new ArrayAdapter<String>(MainActivity.self.getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, numbers));
        Spinner dropdown = MainActivity.self.findViewById(R.id.numbersDropdown);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView  = (TextView) view;

                numbers = new ArrayList<>(Objects.requireNonNull(MainActivity.numbers.get(textView.getText())));
                synchronized (lv) {
                    lv.notify();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                numbers = new ArrayList<>();
                synchronized (lv) {
                    lv.notify();
                }
            }
        });
        updateDropdown();
    }

    public static void updateDropdown() {
        Spinner dropdown = MainActivity.self.findViewById(R.id.numbersDropdown);
        ArrayList<String> items = new ArrayList<>(MainActivity.numbers.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.self.getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, items.toArray(new String[0]));
        dropdown.setAdapter(adapter);
    }
}