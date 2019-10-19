package com.example.ahntalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class SplashActivity extends AppCompatActivity {
    private LinearLayout linearLayout;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout = (LinearLayout) findViewById(R.id.splashactivity_linearlayout);

        /**
         *  (1) Android에서 Firebase 원격 구성시작하기. - 원격구성 객체 가져오기
         *
         *  소스코드 참고
         *  https://firebase.google.com/docs/remote-config/use-config-android?authuser=0
         *
         */
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        /**
         *  (2) 인앱 매개변수 기본값 설정
         *
         *  res/xml 폴더에 저장된 XML 리소스 파일을 사용하여 매개변수 이름과 매개변수 기본값 집합을 정의.
         *  원격 구성 빠른 시작 샘플 앱은 XML 파일을 사용하여 매개변수 이름과 기본값을 정의.
         *
         */
        mFirebaseRemoteConfig.setDefaults(R.xml.default_config);

        /**
         *  (3) 서비스에서 값을 가져와서 활성화시키기
         *
         *  원격구성 서비스에서 데이터를 가져와서 활성화.
         *  fetch() 메서드를 통해서 매개변수값 가져오기.
         *
         */
        mFirebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mFirebaseRemoteConfig.activateFetched();
                        } else {

                        }
                        displayMessage();
                    }
                });
    }

    /**
     *  원격 데이터 가져온뒤, 해당 값 설정하기.
     *
     *  원격저장소에 해당 키 값에 대해서
     *  미리 값이 정의되어 있어야 올바르게 가져온다.
     *
     */
    void displayMessage() {
        String splash_background = mFirebaseRemoteConfig.getString("splash_background");
        boolean caps = mFirebaseRemoteConfig.getBoolean("splash_message_caps");
        String splash_message = mFirebaseRemoteConfig.getString("splash_message");

        linearLayout.setBackgroundColor(Color.parseColor(splash_background));

        /**
         *  caps: 서버점검중인 경우 (boolean)
         *
         *  true - finish(); 종료.
         */
        if(caps) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(splash_message).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.create().show();
        }
    }
}
