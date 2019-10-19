package com.example.ahntalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class LoginActivity extends AppCompatActivity {

    private EditText id;
    private EditText password;

    private Button login;
    private Button signup;

    private FirebaseRemoteConfig mFirebaseRemoteConfig; // 원격 데이터 테마구성하기 위한 객체.
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

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

        String splash_background = mFirebaseRemoteConfig.getString(getString(R.string.rc_color));
        boolean caps = mFirebaseRemoteConfig.getBoolean("splash_message_caps");
        String splash_message = mFirebaseRemoteConfig.getString("splash_message");

        login = (Button)findViewById(R.id.loginActivity_button_login);
        signup = (Button)findViewById(R.id.loginActivity_button_signup);

        login.setBackgroundColor(Color.parseColor(splash_background));
        signup.setBackgroundColor(Color.parseColor(splash_background));

        // SDK 버전에 따라서 실행될 수 있도록한다.
        getWindow().setStatusBarColor(Color.parseColor(splash_background));

        /**
         *  (2) 회원가입 화면 이동
         */
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        /**
         *  (3) 로그인 정보 확인.
         */
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        id = (EditText) findViewById(R.id.loginActivity_edittext_id);
        password = (EditText) findViewById(R.id.loginActivity_edittext_password);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginEvent();
            }
        });
        /**
         *  (4) 로그인 인터페이스 리스터
         *
         *   상태가 Changeed되는 2가지의 경우가 있다.
         *   * 로그인
         *   * 로그아웃
         */
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    // 로그인 된 경우
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    // 인텐트로 다른화면을 띄우고, 현재 화면은 닫아주도록 한다.
                    finish();
                } else {
                    // 로그아웃 된 경우

                }
            }
        };
    }

    /**
     *  로그인 성공여부 확인 이벤트 생성
     *
     *  성공 - 로그인 다음화면으로 이동하기.
     *  실패 - Toast 메시지 띄우기.
     */
    void loginEvent() {
        firebaseAuth.signInWithEmailAndPassword(id.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}
