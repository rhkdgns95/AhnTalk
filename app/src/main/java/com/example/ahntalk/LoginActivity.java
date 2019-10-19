package com.example.ahntalk;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class LoginActivity extends AppCompatActivity {

    private Button login;
    private Button signup;

    private FirebaseRemoteConfig mFirebaseRemoteConfig; // 원격 데이터 테마구성하기 위한 객체.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /**
         *  (1) 원격 데이터로 테마 구성.
         *
         *  먼저, 객체를 생성한다.
         *  원격 데이터의 key-value를 통해 데이터를 가지고 온다.
         *  버튼에 테마 적용하기.
         */
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        String splash_background = mFirebaseRemoteConfig.getString("splash_background");
        boolean caps = mFirebaseRemoteConfig.getBoolean("splash_message_caps");
        String splash_message = mFirebaseRemoteConfig.getString("splash_message");

        // SDK 버전에 따라서 실행될 수 있도록한다.
        getWindow().setStatusBarColor(Color.parseColor(splash_background));

        login = (Button)findViewById(R.id.loginActivity_button_login);
        signup = (Button)findViewById(R.id.loginActivity_button_signup);

        login.setBackgroundColor(Color.parseColor(splash_background));
        signup.setBackgroundColor(Color.parseColor(splash_background));

    }
}
