package me.nathan.smsabuse_sf;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SmsManager;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import android.util.Log;
import android.net.Uri;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.*;
import java.io.*;

import me.nathan.smsabuse_sf.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {
    public int untitledCount = 0;
    public static FloatingActionButton fab;
    public static HashMap<String, List<String>> numbers = new HashMap<>();
    int requestCode = 1;
    Tab currentTab = Tab.NUMBERS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_SMS) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.READ_SMS}, 2);
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

        // Setup the FloatingActionButton
        fab = findViewById(R.id.fab);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        fab.setOnClickListener(view -> {
            // Check to see what tab the user is currently on
            Log.i("SMSAbuse", "Application state: " + currentTab);
            switch (currentTab) {
                case MESSAGES:
                    sendMessage();
                    break;
                case NUMBERS:
                    requestNumbers();
                    break;
            }
        });
    }

    /**
     * Sends the message to all the numbers selected
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sendMessage() {
        String message = ((TextView) findViewById(R.id.message)).getText().toString();
        Log.i("SMSAbuse", "stuffs");

        runOnUiThread(() -> {
            for (List<String> numbers : numbers.values()) {
                for (String number : numbers) {
                    sendSMS(number, message);
                }
                break;
            }
        });
    }

    private void sendSMS(String phoneNumber, String message)
    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Log.i("SMSAbuse", "Send successful");
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        try {
            sms.sendTextMessage(validateNumber(phoneNumber), null, message, sentPI, deliveredPI);
        } catch (IllegalArgumentException ignored) {
            Toast.makeText(getApplicationContext(), "Bad phone number " + phoneNumber, Toast.LENGTH_LONG).show();
        }
    }

    private String validateNumber(String number) {
        StringBuilder sb = new StringBuilder();
        sb.append(number);
        if (sb.toString().startsWith("+64") || sb.toString().startsWith("64")) {
            sb.substring(0, 2);
        }
        if (sb.toString().startsWith("0")) {
            sb.insert(0, "0");
        }
        return sb.toString();
    }

    /**
     * Prompts the user to select a CSV file with phone numbers
     */
    private void requestNumbers() {
        Log.i("SMSAbuse", "Starting number request");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/*");
        startActivityForResult(Intent.createChooser(intent, "Chose CSV file for numbers"),
                requestCode);
    }

    /**
     * Function to handle when a activity finnishes, in this case, used for when the user selects a file
     * @param requestCode The response code specified when [startActivityForResult] was called
     * @param resultCode Weather the activity failed or succeed
     * @param intent The intent for the Activity, contains returned information
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.i("SMSAbuse", "Response recieved");
        if (requestCode == this.requestCode) {
            if (resultCode == RESULT_OK) {
                try {
                    String file = readFile(intent);
                    file = file.replaceAll("[a-zA-Z. \n\t]+", "");

                    Log.i("SMSAbuse", file);

                    List<String> numbers = new ArrayList<>(Arrays.asList(file.split(",")));

                    for (String number : numbers) {
                        Log.i("SMSAbuse", number);
                    }

                    MainActivity.numbers.put("Untitled" + untitledCount, numbers);
                    untitledCount++;
                    
                } catch (IOException e) {
                    if (e instanceof FileNotFoundException) {
                        Toast.makeText(this, "Unable to find file", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Unable to read file", Toast.LENGTH_LONG).show();
                    }
                } catch (NullPointerException e) {
                    Toast.makeText(this, "Internal error", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "File choose failed", Toast.LENGTH_SHORT).show();
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

        return file.toString();
    }

    /**
     * Set [currentTab] to the value of position
     * @param position The index of the current tab
     */
    void setTab(int position) {
        switch (position) {
            case 0:
                currentTab = Tab.NUMBERS;
                break;
            case 1:
                currentTab = Tab.MESSAGES;
                break;
            default:
                currentTab = Tab.BLACK_MAGIC;
        }
    }

    /**
     * Enum to store the tab that the user is on, was [SectionsPageAdapter.State] in previous commit,
     * after some experimentation, I have decided to move it to here seeing as the tab was not updated
     * in the way I thought that it was.
     */
    public enum Tab {
        BLACK_MAGIC(-1),
        NUMBERS(0),
        MESSAGES(1);

        int state;

        Tab(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }
    }
}