package me.nathan.thebigsend;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.nathan.thebigsend.MainActivity.saveState;
import static me.nathan.thebigsend.MainActivity.untitledCount;

public class NumbersActivity extends AppCompatActivity {
    List<View> cards = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numbers);
        ImageButton backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener((view) -> {
            finish();
        });
        Button loadCSV =  findViewById(R.id.ImportCsv);
        loadCSV.setOnClickListener(view -> {
            requestNumbers();
        });

        Button loadGSheets = findViewById(R.id.ImportSheets);
        loadGSheets.setOnClickListener(view ->
                new MaterialAlertDialogBuilder(this)
                    .setTitle("Not yet supported")
                    .setMessage("In the current version, you can not load a list from google docs")
                    .setNeutralButton("Ok", (a, b) -> {})
                    .show()
        );
        updateList();
    }

    /**
     * Prompts the user to select a CSV file with phone numbers
     */
    public void requestNumbers() {
        // Create a new intent
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // Set the type to any form of text file
        intent.setType("text/*");
        // Get the user to pick a file
        startActivityForResult(Intent.createChooser(intent, "Chose CSV file for numbers"), 1);
    }

    /**
     * Function to handle when a activity finishes, in this case, used for when the user selects a file
     *
     * @param requestCode The [requestCode] code specified when [startActivityForResult] was called
     * @param resultCode  Weather the activity failed or succeed
     * @param intent      The intent for the Activity, contains returned information
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        // If it was the file choose intent
        if (requestCode == 1) {
            // If all went well
            if (resultCode == RESULT_OK) {
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Question")
                        .setMessage("Does this CSV have a header?\n"+
                                "I.E. The first line is the column names")
                        .setNegativeButton("No", (dialog, which) -> {
                            Uri location = intent.getData();
                            if (location == null) {
                                Toast.makeText(this, "Location is null", Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    List<Number> numbers = new ArrayList<>();

                                    InputStreamReader isr = new InputStreamReader(getContentResolver().openInputStream(location));
                                    CSVParser parser = CSVFormat.DEFAULT.parse(isr);
                                    for (CSVRecord record : parser) {
                                        numbers.add(new Number(record.get(0), new HashMap<>()));
                                    }

                                    MainActivity.numbers.add(new NumberList("Untitled" + untitledCount, numbers));
                                    untitledCount++;
                                    Log.i("TheBigSend", "Numbers: " +  numbers.toString());
                                    saveState();
                                    updateList();
                                } catch (IOException e) {
                                    Toast.makeText(this, "Could not read file", Toast.LENGTH_LONG).show();
                                    Log.e("TheBigSend", "Error reading file", e);
                                }
                            }
                        })
                        .setPositiveButton("Yes", (DialogInterface dialogInterface, int i) -> {
                            runOnUiThread(() -> {
                                Uri location = intent.getData();
                                if (location == null) {
                                    Toast.makeText(this, "Location is null", Toast.LENGTH_LONG).show();
                                } else {
                                    try {
                                        List<Number> numbers = new ArrayList<>();

                                        InputStreamReader isr = new InputStreamReader(getContentResolver().openInputStream(location));
                                        CSVParser parser = CSVFormat.DEFAULT.withHeader().parse(isr);
                                        String phoneNumberHeader = parser.getHeaderNames().get(0);
                                        for (CSVRecord record : parser) {
                                            Map<String, String> mergeValues = record.toMap();
                                            mergeValues.remove(phoneNumberHeader);
                                            numbers.add(new Number(record.get(phoneNumberHeader), mergeValues));
                                        }

                                        MainActivity.numbers.add(new NumberList("Untitled" + untitledCount, numbers));
                                        untitledCount++;
                                        Log.i("TheBigSend", "Numbers: " +  MainActivity.numbers.toString());
                                        saveState();
                                        updateList();
                                    } catch (IOException e) {
                                        Toast.makeText(this, "Could not read file", Toast.LENGTH_LONG).show();
                                        Log.e("TheBigSend", "Error reading file", e);
                                    }
                                }
                            });
                        }
                        )
                        .setNeutralButton("Cancel", (dialog, which) -> { })
                        .show();
            } else {
                Toast.makeText(this, "Error getting file", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void updateList() {
        RadioGroup layout = findViewById(R.id.NumbersLayout);
        layout.removeAllViews();

        for (NumberList list : MainActivity.numbers) {
            RadioButton button = new RadioButton(this);
            button.setText(list.getName());
            button.setPadding(0, 8, 0, 8);
            button.setTextSize(24);
            layout.addView(button);
        }
    }
}