package com.example.ahntalk.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.ahntalk.R;
import com.example.ahntalk.model.ChatModel;
import com.example.ahntalk.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.nio.channels.Channel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GroupMessageActivity extends AppCompatActivity {

    /**
     *  (1) users: Map<String, UserModel>
     *
     *  단체 채팅방이므로,
     *  여러인원의 정보를 가져오도록 한다.
     */
    Map<String, UserModel> users = new HashMap<>();
    String destinationRoom;
    String uid;
    EditText editText;

    /**
     *  (2) [ databaseReference, valueEventListener ]
     *  채팅방 들어갔다 나오는경우, 읽음표시가 계속되는 버그 잡기.
     */
    private UserModel destinationUserModel;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    List<ChatModel.Comment> comments = new ArrayList<>();

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message);
        destinationRoom = getIntent().getStringExtra("destinationRoom");
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        editText = (EditText) findViewById(R.id.groupMessageActivity_editText);
//        button = (Button) findViewById(R.id.groupMessageActivity_button);

        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users = (Map<String, UserModel>) dataSnapshot.getValue();
                init();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.groupMessageActivity_recyclerview);
        recyclerView.setAdapter(new GroupMessageRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    void init() {
        Button button = (Button) findViewById(R.id.groupMessageActivity_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatModel.Comment comment = new ChatModel.Comment();
                comment.uid = uid;
                comment.message = editText.getText().toString();
                comment.timestamp = ServerValue.TIMESTAMP;

                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        editText.setText("");
                    }
                });
            }
        });
    }
    class GroupMessageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public GroupMessageRecyclerViewAdapter() {
            getMessageList();

        }
        /**
         *
         *  getMessageList 채팅메시지 가져오기
         *
         *  서버에 채팅방을 요청해서 가져온다.
         *  추가적으로, 채팅내용을 읽은것으로 표시해서 전달한다.
         */
        void getMessageList() {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("comments");
            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    comments.clear(); // clear를 하지 않으면, 데이터 갱신시 같은 데이터가 중복되서 쌓이게 됨.
                    Map<String, Object> readUsersMap = new HashMap<>();

                    for(DataSnapshot item : dataSnapshot.getChildren()) {
                        String key = item.getKey();
                        ChatModel.Comment comment_origin = item.getValue(ChatModel.Comment.class);
                        ChatModel.Comment comment_modify = item.getValue(ChatModel.Comment.class);


                        comment_modify.readUsers.put(uid, true);
                        readUsersMap.put(key, comment_modify);

                        comments.add(comment_origin);
                    }

                    if(!comments.get(comments.size() - 1).readUsers.containsKey(uid)) {
                        // 읽지 않은 경우
                        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("comments").updateChildren(readUsersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                notifyDataSetChanged(); // 새로운 데이터를 갱신.
                                recyclerView.scrollToPosition(comments.size() - 1); // 채팅방 스크롤을 하단으로 이동시키기.
                            }
                        });
                    } else {
                        // 메시지를 읽은경우
                        notifyDataSetChanged(); // 새로운 데이터를 갱신.
                        recyclerView.scrollToPosition(comments.size() - 1); // 채팅방 스크롤을 하단으로 이동시키기.
                    }



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
            return new GroupMessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class GroupMessageViewHolder extends RecyclerView.ViewHolder {
            public GroupMessageViewHolder(View view) {
                super(view);

            }
        }
    }
}
