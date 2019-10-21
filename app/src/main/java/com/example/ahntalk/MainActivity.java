package com.example.ahntalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.ahntalk.fragment.ChatFragment;
import com.example.ahntalk.fragment.PeopleFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

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

        passPushTokenToServer();
    }

    /**
     *  토큰 생성 후,
     *  DB의 사용자 정보에 저장.
     */
    void passPushTokenToServer() {


        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(!task.isSuccessful()) {
                    return;
                }
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Map<String, Object> map = new HashMap<>();
                // Get New Instance ID Token.

                String token = task.getResult().getToken();
                map.put("pushToken", token);
                Log.d("my_text", "핼로우");
                Log.d("my_text", token);
                /**
                 *  주의!
                 *
                 *  setValue는 기존데이터를 덮어쓰우게 되므로,
                 *  updateChildren으로 사용함.
                 */
                FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);
            }
        });
    }

}
