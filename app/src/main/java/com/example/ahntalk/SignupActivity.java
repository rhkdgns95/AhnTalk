package com.example.ahntalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.ahntalk.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class SignupActivity extends AppCompatActivity {

    private EditText email;
    private EditText name;
    private EditText password;
    private Button signup;
    private String splash_background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        splash_background = mFirebaseRemoteConfig.getString(getString(R.string.rc_color));

        getWindow().setStatusBarColor(Color.parseColor(splash_background));

        email = (EditText) findViewById(R.id.signupActivity_edittext_email);
        name = (EditText) findViewById(R.id.signupActivity_edittext_name);
        password = (EditText) findViewById(R.id.signupActivity_edittext_password);
        signup = (Button) findViewById(R.id.signupActivity_button_signup);

        signup.setBackgroundColor(Color.parseColor(splash_background));

        /**
         *  (1) 회원가입.
         *
         *  Firebase로 인증할 수 있는 계정 생성.
         */
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString() == null
                        || password.getText().toString() == null
                        || name.getText().toString() == null
                ) {
                    return;
                }
                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            /**
                             *  (2) Firebase의 Realtime Database.
                             *
                             *  [tools] - [Firebase] - [Realtime Database]를 추가.
                             *  회원가입후 - UserModel 객체 생성 후,
                             *  Firebase의 데이터베이스에 유저의 child로
                             *  고유의 UID에 해당하는 값에 UserMdoel 객체 저장하기.
                             *  결과: [Database]에서 ahntalk/users/생성된계정의_UID/userModel
                             */
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                UserModel userModel = new UserModel();
                                userModel.userName = name.getText().toString();

                                String uid = task.getResult().getUser().getUid();
                                FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel);
                            }
                        });
            }
        });
    }
}
