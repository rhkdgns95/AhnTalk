package com.example.ahntalk.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.ahntalk.R;
import com.example.ahntalk.model.ChatModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 *  (1) 채팅창을 클릭할 경우
 *
 *   Message 이벤트가 뜨도록 한다.
 */
public class MessageActivity extends AppCompatActivity {

    private String destinationUid;
    private Button button;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        destinationUid = getIntent().getStringExtra("destinationUid");
        button = (Button) findViewById(R.id.messageActivity_button);
        editText = (EditText) findViewById(R.id.messageActivity_editText);

        /**
         *  (2) 채팅 메시지 전송 - [전송] 클릭시
         *
         *  채팅창이 먼저 만들어 있으면, 단순히 글자만 전송.
         *  그렇지 않으면, 채팅창을 같이 만들어 준 다음, 전송.
         *
         *   uid: 본인 uid
         *   destinationUid: 상대유저 uid
         */
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatModel chatModel = new ChatModel();
                chatModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                chatModel.destinationUid = destinationUid;

                /**
                 *  (3) 채팅방 생성
                 *
                 *  push()를 추가해야 채팅방 이름이
                 *  임의적으로 생성됨.
                 */
                FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel);
            }
        });
    }
}
