package me.nathan.thebigsend;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    /**
     * Stores the number of lists created, used to create default list name
     */
    public static int untitledCount = 0;

    /**
     * Stores all the lists of numbers
     */
    public static List<NumberList> numbers = new ArrayList<>();

    /**
     * A reference to the current instance of MainActivity
     */
    public static MainActivity self;

    /**
     * Stores the name of the current list, updated by [Numbers.updateListView()]
     */
    public static String currentList;

    /**
     * The file that stores all the numbers after the app is closed
     */
    public static File jsonFile;

    /**
     * Gosn
     */
    static final Gson gson = new Gson();

    /**
     * This function creates a new instance of the MainActivity, it:
     * - Shows the popup if this is the first time that the user has opened the app
     * - Requests permissions
     * - Calls default onCreate method and sets the view to the [activity_main.xml] layout
     * - Sets up the buttons, View Pager, Tab View, buttons etc,
     * - Loads numbers list from json file
     * - Sets self
     * This is a function called by android to tell it what to do when an instance of [MainActvity]
     * is created
     *
     * @param savedInstanceState Parameter passed in from android
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get the shared preferences used to store basic information in key value pairs
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);

        // Check to see if the app has SMS permissions
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }

        // Check to see weather the user has ever opened the app
        if (!preferences.contains("firstOpen")) {
            // Create a popup dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Create TextView to display the information
            TextView view = new TextView(this);
            view.setText(R.string.starting_message);
            view.setTextColor(getResources().getColor(R.color.primaryTextColor));

            // Add the text to the dialog box
            builder.setView(view);

            // Add an OK button
            builder.setPositiveButton("Ok", (dialog, y) -> dialog.dismiss());

            // Show the dialog box
            builder.show();

            // Save the fact that the user has already opened the app
            preferences.edit().putBoolean("firstOpen", false).apply();
        }

        // Get the untiled count to avoid list overwriting
        if (preferences.contains("untitledCount")) {
            untitledCount = preferences.getInt("untitledCount", 0);
        }

        // Call the default onCreate method
        super.onCreate(savedInstanceState);

        // Set the view to activity_main.xml
        setContentView(R.layout.activity_main);

        // Read the numbers file and update the numbers variable
        // Is run on new thread to stop the app slowing down or crashing during the loading process
        runOnUiThread(() -> {
            try {
                // Get the config file
                jsonFile = new File(getApplicationContext().getFilesDir(), "data.json");

                // Check to see if the file actual exist
                if (!jsonFile.exists()) {
                    // Create a new file
                    jsonFile.createNewFile();
                }

                // Create a buffered reader to read the file
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(jsonFile)));

                String line;
                String configString;
                StringBuilder sb = new StringBuilder();

                // Read the file line by line
                while ((line = reader.readLine()) != null) {
                    // Append the line to the string builder
                    sb.append(line);
                }

                // Convert the StringBuilder to a String
                configString = sb.toString();

                // Convert the string into a HashMap and save it in numbers
                numbers = gson.fromJson(configString, numbers.getClass());

                // If there is no data, set numbers to be a new instance of HashMap
                if (numbers == null) {
                    numbers = new ArrayList<>();
                }
            } catch (IOException ex) {
                // Report if there was any error opening the file
                Toast.makeText(getApplicationContext(), "Unable to open config file", Toast.LENGTH_LONG).show();
            }
        });

        ((TextView) findViewById(R.id.SelectedList)).setText("None");

        Button btn = findViewById(R.id.SelectButton);
        btn.setOnClickListener((currentView) -> {
            startActivity(new Intent(this, NumbersActivity.class));
        });

        self = this;
    }

    /**
     * Sends the message to all the numbers selected
     */
    public void sendMessage() {
        // Get the message
        String message = "Ye.";

        // On a new thread, send a message to each phone number
        // New thread helps stop the app hanging during sendage
        runOnUiThread(() -> {
            try {
                // If no list is selected
                if (currentList.equals("")) {
                    // Notify the user and exit the function
                    Toast.makeText(this, "Please select a list", Toast.LENGTH_LONG).show();
                    return;
                }
                // Get the current list
                for (NumberList list : numbers.stream().filter(a -> a.getName().equals(currentList)).collect(Collectors.toList())) {
                    for (Number num : list.numbers) {
                        sendSMS(num.phoneNumber, message);
                    }
                }
            } catch (NullPointerException ex) {
                // If the app fails to get the list at the index of current list, give an error stating that
                Toast.makeText(this, "Error initializing send", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Sends a message to a phone number
     *
     * @param phoneNumber The phone number in question
     * @param message     The message to send to the phone number
     */
    private void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        // Create a listener for when the sms messages are sent
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        // Create a listner for when the SMS
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        // When the message has been sent
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    // If there was some generic error
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure sending message",
                                Toast.LENGTH_SHORT).show();
                        break;

                    // If there is no service
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;

                    // If no PDU is provided
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;

                    // If radio was explicitly turned off
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        }, new IntentFilter(SENT));

        // When the sms message was received
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    // If the message was sent do nothing
                    case Activity.RESULT_OK:
                        break;

                    // If the message could not be delivered
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        // Get the SMSManager to send messages with
        SmsManager sms = SmsManager.getDefault();

        try {
            // Send a message
            sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
        } catch (IllegalArgumentException ignored) {
            // If it did not work
            Toast.makeText(getApplicationContext(), "Bad phone number " + phoneNumber, Toast.LENGTH_LONG).show();
        }
    }

    public static void saveState() {
        // Serialize the numbers array to a json string
        String data = gson.toJson(MainActivity.numbers);
        try {
            // Create a new buffered reader to write to the jsonFile
            BufferedWriter bf = new BufferedWriter(new FileWriter(jsonFile));

            // Check that there are write permissions
            if (jsonFile.canWrite()) {
                // Write the data
                bf.write(data);
                // Flush the stream
                bf.flush();
            }
        } catch (IOException ex) {
            // Tell the user if there was a problem writing to the file
            Toast.makeText(self.getApplicationContext(), "Error writing to file", Toast.LENGTH_SHORT).show();
        }
    }
}
