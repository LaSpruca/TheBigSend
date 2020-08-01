package me.nathan.smsabuse_sf.ui.main;

import android.os.Bundle;

import androidx.annotation.*;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

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
        lv.setAdapter(new ArrayAdapter<String>(MainActivity.self.getApplicationContext(), R.layout.spinner_list_item, numbers));
        Spinner dropdown = MainActivity.self.findViewById(R.id.numbersDropdown);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateListView(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                numbers = new ArrayList<>();
            }
        });
        updateDropdown();
        if (MainActivity.numbers.size() > 0) {
            updateListView(0);
        }

        ImageButton editButton = view.findViewById(R.id.editButton);
        editButton.setOnClickListener((view_) -> {
            LayoutInflater layoutInflater = (LayoutInflater) MainActivity.self.getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.edit_popup, null);

            CoordinatorLayout coordinatorLayout = MainActivity.self.findViewById(R.id.main_layout);

            PopupWindow popupWindow = new PopupWindow(container, 500, 500, true);
            popupWindow.showAtLocation(coordinatorLayout, Gravity.CENTER, 0, 0);

            container.setOnTouchListener((view1, motionEvent) -> {
                view1.performClick();
                popupWindow.dismiss();
                MainActivity.self.findViewById(R.id.main_layout).setEnabled(true);
                MainActivity.self.findViewById(R.id.main_layout).setAlpha(0.5f);
                return true;
            });

            MainActivity.self.findViewById(R.id.main_layout).setEnabled(false);
        });
    }

    public static void updateListView(int position) {
        ListView lv = MainActivity.self.findViewById(R.id.numberList);
        if (lv == null) {
            return;
        }String key = MainActivity.numbers.keySet().toArray(new String[0])[position];
        ArrayList<String> numbers = new ArrayList<>(Objects.requireNonNull(MainActivity.numbers.get(key)));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.self.getApplicationContext(), R.layout.spinner_list_item, numbers);
        lv.setAdapter(adapter);
        MainActivity.currentList = key;
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