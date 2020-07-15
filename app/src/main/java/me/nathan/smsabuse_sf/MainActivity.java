package me.nathan.smsabuse_sf;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import me.nathan.smsabuse_sf.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    public static FloatingActionButton fab;
    public static List<List<String>> numbers = new ArrayList<>();
    public int requestCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        fab.setOnClickListener(view -> {
            // Check to see what tab the user is currently on
            switch (SectionsPagerAdapter.state) {
                case MESSAGE:
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
    private void sendMessage() {
    }

    /**
     * Prompts the user to select a CSV file with phone numbers
     */
    private void requestNumbers() {

    }
}