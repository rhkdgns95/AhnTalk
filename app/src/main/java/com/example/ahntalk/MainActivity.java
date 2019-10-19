package com.example.ahntalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.ahntalk.fragment.ChatFragment;
import com.example.ahntalk.fragment.PeopleFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.mainactivity_bottomnavigationview);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_people:
                        /**
                         *  주의!
                         *
                         *  .getSupportFragmentManager 사용하면 v4.fragment로 사용해야함.
                         *  이전 버전
                         *  getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout, new PeopleFragment()).commit();
                         */
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout, new PeopleFragment()).commit();
                        return true;

                    case R.id.action_chat:
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout, new ChatFragment()).commit();
                        return true;
//                    case R.id.action_account:
                }

                return false;
            }
        });
    }
}
