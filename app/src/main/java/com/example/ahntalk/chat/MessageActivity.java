package com.example.ahntalk.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.ahntalk.R;

/**
 *  (1) 채팅창을 클릭할 경우
 *
 *   Message 이벤트가 뜨도록 한다.
 */
public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
    }
}
