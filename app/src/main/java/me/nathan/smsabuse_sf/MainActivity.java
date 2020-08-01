package me.nathan.smsabuse_sf;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.*;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.*;
import java.util.*;

import me.nathan.smsabuse_sf.ui.main.Numbers;
import me.nathan.smsabuse_sf.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {
    public int untitledCount = 0;
    public static Map<String, List<String>> numbers = new HashMap<>();
    public static MainActivity self;
    public static String currentList;
    public static File jsonFile;
    static final Gson gson = new Gson();
    int requestCode = 1;

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

        runOnUiThread(() -> {
            try {
                jsonFile = new File(getApplicationContext().getFilesDir(), "data.json");
                if (!jsonFile.exists()) {
                    jsonFile.createNewFile();
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(jsonFile)));
                String line;
                String configString;
                StringBuilder sb = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                configString = sb.toString();
                Log.i("SMSAbuse", configString);
                numbers = gson.fromJson(configString, numbers.getClass());

                if (numbers == null) {
                    numbers = new HashMap<>();
                }
            } catch (IOException ex) {
                Toast.makeText(getApplicationContext(), "Unable to open config file", Toast.LENGTH_LONG).show();
                Log.e("SMSABUSE", "Not a brrrru");
            }
        });

        self = this;
    }

    /**
     * Sends the message to all the numbers selected
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendMessage() {
        String message = ((TextView) findViewById(R.id.message)).getText().toString();
        Log.i("SMSAbuse", "stuffs");

        runOnUiThread(() -> {
            for (String number : Objects.requireNonNull(numbers.get(currentList))) {
                String num = validateNumber(number);
                Log.i("SMSAbuse", "Sending message to " + num);
                sendSMS(num, message);
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
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure sending message",
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
                    default:
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
        if (sb.toString().startsWith("64")) {
            sb.substring(0, 2);
        } else if (sb.toString().startsWith("+64")) {
            sb.substring(0, 3);
        }
        if (!sb.toString().startsWith("0")) {
            sb.insert(0, "0");
        }
        return sb.toString();
    }

    /**
     * Prompts the user to select a CSV file with phone numbers
     */
    public void requestNumbers() {
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

                    numbers.removeIf(s -> s.length() < 1);

                    MainActivity.numbers.put("Untitled" + untitledCount, numbers);
                    untitledCount++;
                    saveState();
                    Numbers.updateDropdown();
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

    public static void saveState() {
        String data = gson.toJson(MainActivity.numbers);
        try {
            BufferedWriter bf = new BufferedWriter(new FileWriter(jsonFile));
            if (jsonFile.canWrite()) {
                bf.write(data);
                bf.flush();
            }
        } catch (IOException ex) {
            Toast.makeText(self.getApplicationContext(), "Error writing to file", Toast.LENGTH_SHORT).show();
        }
    }

}
