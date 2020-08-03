package me.nathan.smsabuse_sf.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import me.nathan.smsabuse_sf.MainActivity;
import me.nathan.smsabuse_sf.R;
/**
 * The message menu
 */
public class Message extends Fragment {
    public Message() {
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
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    /**
     * Called by android when the view is created, it:
     *    - Sets up the fab
     * @param view The view of the fragment
     * @param savedInstanceState The saved instance state
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get the floating action button
        FloatingActionButton fab = view.findViewById(R.id.sendButton);

        // Set it to send messages
        fab.setOnClickListener((v) -> MainActivity.self.sendMessage());
    }
}