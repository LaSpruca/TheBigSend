package me.nathan.thebigsend.ui.main;

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

import me.nathan.thebigsend.MainActivity;
import me.nathan.thebigsend.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * The number selection menu
 */
public class Numbers extends Fragment {
    public Numbers() {
        // Required empty public constructor
    }

    /**
     * Used by android to create an instance of the Numbers fragment
     * @param savedInstanceState The saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * Used by android to create the view for the app
     * @param inflater The app [LayoutInflator]
     * @param container The [ViewGroup] for the fragment
     * @param savedInstanceState The saved instance state
     * @return the inflated view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_numbers, container, false);
    }

    /**
     * Called by android when the view is created, it:
     *    - Sets up the fab
     *    - Updates and sets up the dropdown
     *    - Sets up edit and delete buttons
     * @param view The view of the fragment
     * @param savedInstanceState The saved instance state
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Rig the Floating Action Button to get the numbers list
        FloatingActionButton fab = view.findViewById(R.id.numbers);
        fab.setOnClickListener((v) -> MainActivity.self.requestNumbers());

        // Update the dropdown
        updateDropdown();

        // Setup the dropdown with to update the list view when a new value is selected
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

        // Get the edit button
        ImageButton editButton = view.findViewById(R.id.editButton);

        // Setup an On Click Listener to pull up a edit name dialog box
        editButton.setOnClickListener((view_) -> {
            // Check to make sure the list can be edited
            if (MainActivity.numbers.isEmpty()) { return; }

            // Create a dialog box builder
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.self);
            // Set the title
            builder.setTitle("New name for list");

            // Set up the input
            final EditText input = new EditText(MainActivity.self);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
            builder.setView(input);

            // Set up the buttons
            // Ok Button
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Get the dropdown and current position of the spinner
                    Spinner spinner = view.findViewById(R.id.numbersDropdown);
                    int position = spinner.getSelectedItemPosition();

                    // Get the name provided by the user
                    String newName = input.getText().toString();

                    // Get the old name
                    String key = MainActivity.numbers.keySet().toArray(new String[0])[position];

                    // Get the current list of numbers
                    List<String> numbers = MainActivity.numbers.get(key);

                    // Remove the list
                    MainActivity.numbers.remove(key);

                    // Re-add it under the new name
                    MainActivity.numbers.put(newName, numbers);

                    // Save the changes
                    MainActivity.saveState();

                    // Update the dropdown
                    updateDropdown();
                }
            });
            // Cancel button
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Dismiss the dialog with making any changes
                    dialog.cancel();
                }
            });

            builder.show();
        });

        // Get the delete button
        ImageButton deleteButton = view.findViewById(R.id.deleteButton);

        // Setup the onclick listener
        deleteButton.setOnClickListener(v -> {
            // Get the spinner
            Spinner spinner = view.findViewById(R.id.numbersDropdown);

            // Check that there are actual numbers in the list
            if (!MainActivity.numbers.isEmpty()) {
                // Get the position of the spinner
                int position = spinner.getSelectedItemPosition();

                // Get the key of the current list
                String key = MainActivity.numbers.keySet().toArray(new String[0])[position];

                // Remove it
                MainActivity.numbers.remove(key);
            }

            // Update the dropdown to make sure the data is correct
            updateDropdown();

            // Is empty check must be re-run on the modified list to insure no errors
            if (!MainActivity.numbers.isEmpty()) {
                // Update the listview and dropdown
                updateListView(0);
                spinner.setSelection(0);
            } else {
                // Clear the list view and update the dropdown
                updateListView(-1);
            }

            // Save the state
            MainActivity.saveState();
        });
    }

    /**
     * Updates the list view to reflect the selected list
     * @param position The position of the spinner
     */
    public static void updateListView(int position) {
        // Get the listview
        ListView lv = MainActivity.self.findViewById(R.id.numberList);

        // Check that it is not a null reference
        if (lv == null) {
            return;
        }

        // Check that the list should not be cleared
        if (position != -1) {
            // Get the key for the current list
            String key = MainActivity.numbers.keySet().toArray(new String[0])[position];

            // Get the numbers
            ArrayList<String> numbers = new ArrayList<>(Objects.requireNonNull(MainActivity.numbers.get(key)));

            // Create an ArrayAdapter aground the numbers list
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.self.getApplicationContext(), R.layout.spinner_list_item, numbers);

            // Update the list view
            lv.setAdapter(adapter);

            // Set currentList to the current key
            MainActivity.currentList = key;

            // Change the text on the current select list text view to reflect that of the actually selected list
            ((TextView) MainActivity.self.findViewById(R.id.listNameView)).setText(key);
        } else {
            // Clear the list view
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.self.getApplicationContext(), R.layout.spinner_list_item, new ArrayList<>());
            lv.setAdapter(adapter);

            // Set the current list to black
            MainActivity.currentList = "";

            // Clear the text on the current select list text view
            ((TextView) MainActivity.self.findViewById(R.id.listNameView)).setText("");
        }
    }

    /**
     * Used to update the dropdown menu
     */
    public static void updateDropdown() {
        // Get the dropdown
        Spinner dropdown = MainActivity.self.findViewById(R.id.numbersDropdown);

        // Check that the dropdown is not null
        if (dropdown == null) {
            return;
        }

        // Create an ArrayList of the keys
        ArrayList<String> items = new ArrayList<>(MainActivity.numbers.keySet());

        // Set the dropdown values to be the keys
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.self.getApplicationContext(), R.layout.spinner_list_item, items.toArray(new String[0]));
        dropdown.setAdapter(adapter);
    }
}