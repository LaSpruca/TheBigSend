package me.nathan.smsabuse_sf;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.io.*;
import java.util.*;

import me.nathan.smsabuse_sf.ui.main.*;

public class MainActivity extends AppCompatActivity {
    /**
     * Stores the number of lists created, used to create default list name
     */
    public int untitledCount = 0;

    /**
     * Stores all the lists of numbers
     */
    public static Map<String, List<String>> numbers = new HashMap<>();

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
     *   - Shows the popup if this is the first time that the user has opened the app
     *   - Requests permissions
     *   - Calls default onCreate method and sets the view to the [activity_main.xml] layout
     *   - Sets up the buttons, View Pager, Tab View, buttons etc,
     *   - Loads numbers list from json file
     *   - Sets self
     * This is a function called by android to tell it what to do when an instance of [MainActvity]
       is created
     * @param savedInstanceState Parameter passed in from android
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get the shared preferences used to store basic information in key value pairs
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);

        // Check to see if the app has SMS permissions
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS}, 1);
        }

        // Check to see weather the user has ever opened the app
        if (!preferences.contains("firstOpen")) {
            // Create a popup dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Create TextView to display the information
            TextView view = new TextView(this);
            view.setText(R.string.starting_message);
            view.setTextColor(getResources().getColor(R.color.white));

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

        // Create a SectionPageAdapter for the ViewPager
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        // Setup the ViewPager and TabLayout
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

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
                    numbers = new HashMap<>();
                }
            } catch (IOException ex) {
                // Report if there was any error opening the file
                Toast.makeText(getApplicationContext(), "Unable to open config file", Toast.LENGTH_LONG).show();
            }
        });

        self = this;
    }

    /**
     * Sends the message to all the numbers selected
     */
    public void sendMessage() {
        // Get the message
        String message = ((TextView) findViewById(R.id.message)).getText().toString();

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
                // Get the numbers in the current list
                for (String number : Objects.requireNonNull(numbers.get(currentList))) {
                    // Send a sms message to the number with the message
                    sendSMS(number, message);
                }
            } catch (NullPointerException ex) {
                // If the app fails to get the list at the index of current list, give an error stating that
                Toast.makeText(this, "Error initializing send", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Sends a message to a phone number
     * @param phoneNumber The phone number in question
     * @param message The message to send to the phone number
     */
    private void sendSMS(String phoneNumber, String message)
    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        // Create a listener for when the sms messages are sent
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        // Create a listner for when the SMS
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        // When the message has been sent
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
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
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
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
            sms.sendTextMessage(validateNumber(phoneNumber), null, message, sentPI, deliveredPI);
        } catch (IllegalArgumentException ignored) {
            // If it did not work
            Toast.makeText(getApplicationContext(), "Bad phone number " + phoneNumber, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Function to check that the number is valid, this only supports nz country code
     * @param number The phone number that you want to check
     * @return The validated number
     */
    private String validateNumber(String number) {
        // Create a new string builder
        StringBuilder sb = new StringBuilder();
        sb.append(number);

        // Remove the country code
        if (sb.toString().startsWith("64")) {
            sb.substring(0, 2);
        } else if (sb.toString().startsWith("+64")) {
            sb.substring(0, 3);
        }

        // Add 0 to start of number if not present
        if (!sb.toString().startsWith("0")) {
            sb.insert(0, "0");
        }

        // Return the validated string
        return sb.toString();
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
     * @param requestCode The [requestCode] code specified when [startActivityForResult] was called
     * @param resultCode Weather the activity failed or succeed
     * @param intent The intent for the Activity, contains returned information
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        // If it was the file choose intent
        if (requestCode == 1) {
            // If all went well
            if (resultCode == RESULT_OK) {
                try {
                    // Read in the file
                    String file = readFile(intent);

                    // Remove all unwanted characters using a regex
                    file = file.replaceAll("[a-zA-Z. \n\t]+", "");

                    // Create a new ArrayList with the numbers
                    List<String> numbers = new ArrayList<>(Arrays.asList(file.split(",")));

                    // Remove the number is it is blank
                    numbers.removeIf(s -> s.length() < 1);

                    // Validate all the numbers
                    for (int i = 0; i < numbers.size(); i++) {
                        numbers.set(i, validateNumber(numbers.get(i)));
                    }

                    // Add it to the list of numbers with the name Untitled plus the untitledCount,
                    // this is why it is saved at the start of the program
                    MainActivity.numbers.put("Untitled" + untitledCount, numbers);

                    // Increment the untitled count
                    untitledCount++;

                    // Save the new untitledCount
                    SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
                    preferences.edit().putInt("untitledCount", untitledCount).apply();

                    // Save the application state to the json file
                    saveState();
                    // Update the dropdown
                    Numbers.updateDropdown();
                } catch (IOException e) {
                    // Catch any IOException

                    // If it was a FileNotFound exception
                    if (e instanceof FileNotFoundException) {
                        // Tell the user that the file could not be found
                        Toast.makeText(this, "Unable to find file", Toast.LENGTH_LONG).show();
                    } else {
                        // Report that the file could not be read
                        Toast.makeText(this, "Unable to read file", Toast.LENGTH_LONG).show();
                    }
                } catch (NullPointerException e) {
                    // If there was an error with the intent
                    Toast.makeText(this, "Internal error", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * Gets the contents of a file
     * @param intent The intent congaing the data
     * @return The contents of the file specified in [intent]
     * @throws NullPointerException If the intent's getData returns null
     * @throws FileNotFoundException If the file is not found, it kinda does what it says on the box
     * @throws IOException If there is a problem with reading the file
     */
    String readFile(Intent intent) throws NullPointerException, FileNotFoundException, IOException {
        Log.i("SMSAbuse", "Reading file");

        // Getting the location
        Uri location = intent.getData();
        if (location == null) {
            throw new NullPointerException();
        }
        BufferedReader br;

        // Opening the file
        br = new BufferedReader(new
                InputStreamReader(
                        Objects.requireNonNull(
                                getContentResolver().openInputStream(location))));

        // Reading the file
        StringBuilder file = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            file.append(line);
        }

        // Return the state
        return file.toString();
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
