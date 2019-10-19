package com.example.ahntalk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.ahntalk.fragment.PeopleFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         *  주의!
         *
         *  .getSupportFragmentManager 사용하면 v4.fragment로 사용해야함.
         *  이전 버전
         *  getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout, new PeopleFragment()).commit();
         */
        getSupportFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout, new PeopleFragment()).commit();
    }
}
