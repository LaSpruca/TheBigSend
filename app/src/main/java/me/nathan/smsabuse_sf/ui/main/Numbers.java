package me.nathan.smsabuse_sf.ui.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.*;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import me.nathan.smsabuse_sf.MainActivity;
import me.nathan.smsabuse_sf.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * The number selection menu, nothing special atm
 */
public class Numbers extends Fragment {
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

        Spinner dropdown = MainActivity.self.findViewById(R.id.numbersDropdown);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateListView(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateListView(-1);
            }
        });
        updateDropdown();

        ImageButton editButton = view.findViewById(R.id.editButton);
        editButton.setOnClickListener((view_) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.self);
            builder.setTitle("New name for list");

            // Set up the input
            final EditText input = new EditText(MainActivity.self);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Spinner spinner = view.findViewById(R.id.numbersDropdown);
                    int position = spinner.getSelectedItemPosition();
                    String newName = input.getText().toString();
                    String key = MainActivity.numbers.keySet().toArray(new String[0])[position];
                    List<String> numbers = MainActivity.numbers.get(key);
                    MainActivity.numbers.remove(key);
                    MainActivity.numbers.put(newName, numbers);
                    MainActivity.saveState();
                    updateDropdown();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

        });
        ImageButton deleteButton = view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.self);
            Spinner spinner = view.findViewById(R.id.numbersDropdown);
            if (MainActivity.numbers.size() > 0) {
                int position = spinner.getSelectedItemPosition();
                String key = MainActivity.numbers.keySet().toArray(new String[0])[position];
                MainActivity.numbers.remove(key);
            }
            if (MainActivity.numbers.size() > 0) {
                updateListView(0);
                spinner.setSelection(0);
            } else {
                updateListView(-1);
                updateDropdown();
            }
            MainActivity.saveState();
        });
    }

    public static void updateListView(int position) {
        ListView lv = MainActivity.self.findViewById(R.id.numberList);

        if (lv == null) {
            return;
        }

        if (position != -1) {
            String key = MainActivity.numbers.keySet().toArray(new String[0])[position];
            ArrayList<String> numbers = new ArrayList<>(Objects.requireNonNull(MainActivity.numbers.get(key)));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.self.getApplicationContext(), R.layout.spinner_list_item, numbers);
            lv.setAdapter(adapter);
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.self.getApplicationContext(), R.layout.spinner_list_item, new ArrayList<>());
            lv.setAdapter(adapter);
        }
    }

    public static void updateDropdown() {
        Spinner dropdown = MainActivity.self.findViewById(R.id.numbersDropdown);

        if (dropdown == null) {
            return;
        }

        ArrayList<String> items = new ArrayList<>(MainActivity.numbers.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.self.getApplicationContext(), R.layout.spinner_list_item, items.toArray(new String[0]));
        dropdown.setAdapter(adapter);
    }
}