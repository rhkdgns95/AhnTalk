package com.example.ahntalk.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ahntalk.R;
import com.example.ahntalk.model.ChatModel;
import com.example.ahntalk.model.NotificationModel;
import com.example.ahntalk.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

/**
 *  (1) 채팅창을 클릭할 경우
 *
 *   Message 이벤트가 뜨도록 한다.
 */
public class MessageActivity extends AppCompatActivity {

    private String destinationUid;
    private Button button;
    private EditText editText;

    private String uid;
    private String chatRoomUid;

    private RecyclerView recyclerView;
    private UserModel destinationUserModel;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    /**
     *  [ databaseReference, valueEventListener ]
     *  채팅방 들어갔다 나오는경우, 읽음표시가 계속되는 버그 잡기.
     */
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    int people_count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        /**
         *  uid: 채팅을 요구하는 사용자.(로그인 유저, 본인)
         *  destinationUid: 채팅을 요청받은 사용자.
         */

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        destinationUid = getIntent().getStringExtra("destinationUid");

        button = (Button) findViewById(R.id.messageActivity_button);
        editText = (EditText) findViewById(R.id.messageActivity_editText);
        recyclerView = (RecyclerView) findViewById(R.id.messageActivity_recyclerview);

        /**
         *  (2) 채팅 메시지 전송 - [전송] 클릭시
         *
         *  채팅창이 먼저 만들어 있으면, 단순히 글자만 전송.
         *  그렇지 않으면, 채팅창을 같이 만들어 준 다음, 전송.
         *
         */
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatModel chatModel = new ChatModel();
                chatModel.users.put(uid, true);
                chatModel.users.put(destinationUid, true);

                /**
                 *  (3) 채팅방 생성
                 *
                 *  push()를 추가해야 채팅방 이름이
                 *  임의적으로 생성됨.
                 */
                if(chatRoomUid == null) {
                    button.setEnabled(false);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkChatRoom();
                        }
                    });
                } else {
                    ChatModel.Comment comment = new ChatModel.Comment();
                    comment.uid = uid;
                    comment.message = editText.getText().toString();
                    comment.timestamp = ServerValue.TIMESTAMP;

                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            sendFcm();
                            editText.setText("");
                        }
                    });
                }

            }
        });
        checkChatRoom();
    }

    /**
     *  Push 알림 (네트워크 전송의 헤더와 바디부분)
     *
     *  메시지 받는 유저의 pushToken에
     *  메시지를 전달한다.
     *  참고!
     *  * header("Content-Type", "application/json; charset=UTF-8")
     *  * 이전서버 키
     *  * fcm url
     */
    void sendFcm() {
        Gson gson = new Gson();

        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        NotificationModel notificationModel = new NotificationModel();
        notificationModel.to = destinationUserModel.pushToken;
        Log.d("my_error", "사용자 Token값: "+ notificationModel.to);
        notificationModel.data.title = userName;
        notificationModel.data.text = editText.getText().toString();

        // deprecated!
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"), gson.toJson(notificationModel));

        Log.d("my_error", "데이터: " + requestBody);
        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("Authorization", "key=AIzaSyAh2PTcrU_jxUJGfBECiMMotOmuPp2uovM")
                .url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            }
        });
    }

    void checkChatRoom() {
        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()) {
                    ChatModel chatModel = item.getValue(ChatModel.class);
                    if(chatModel.users.containsKey(destinationUid) && chatModel.users.size() == 2) {
                        chatRoomUid = item.getKey();
                        button.setEnabled(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                        recyclerView.setAdapter(new RecyclerViewAdapter());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<ChatModel.Comment> comments;


        public RecyclerViewAdapter() {
            comments = new ArrayList<>();

            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    destinationUserModel = dataSnapshot.getValue(UserModel.class);
                    getMessageList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        /**
         *
         *  getMessageList 채팅메시지 가져오기
         *
         *  서버에 채팅방을 요청해서 가져온다.
         *  추가적으로, 채팅내용을 읽은것으로 표시해서 전달한다.
         */
        void getMessageList() {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments");
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
                    // BugFixed
                    if(comments.size() == 0) {
                        return;
                    }
                    if(!comments.get(comments.size() - 1).readUsers.containsKey(uid)) {
                        // 읽지 않은 경우
                        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").updateChildren(readUsersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            MessageViewHolder messageViewHolder = ((MessageViewHolder) holder);
            /**
             *  송신자와 수신자의 메시지 말풍선을 분리
             *
             *  comments.get(position).uid : 메시지를 보낸사람의 uid
             *  uid : 현재 로그인 사람의 uid
             *
             */

            if(comments.get(position).uid.equals(uid)) {
                // 내가 보낸 메시지.
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.message_bubble_right);
                messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE); // 자신의 정보는 가릴수 있도록 한다.
                messageViewHolder.textView_message.setTextSize(25);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
                setReadCounter(position, messageViewHolder.textView_readCounter_left);
            } else {
                // 상대방이 보낸 메시지.
                Glide.with(holder.itemView.getContext())
                        .load(destinationUserModel.profileImageUrl)
                        .apply(new RequestOptions().circleCrop())
                        .into(messageViewHolder.imageView_profile);
                messageViewHolder.textview_name.setText(destinationUserModel.userName);
                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.message_bubble_left);
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setTextSize(25);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
                setReadCounter(position, messageViewHolder.textView_readCounter_right);
            }
            long unixTime = (long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);
            messageViewHolder.textView_timestamp.setText(time);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
            return new MessageViewHolder(view); // View를 재사용할때 사용하는 클래스.
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        void setReadCounter(final int position, final TextView textView) {
            if(people_count == 0) {
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Boolean> users = (Map<String, Boolean>) dataSnapshot.getValue();
                        people_count = users.size();
                        int count = people_count - comments.get(position).readUsers.size();
                        if(count > 0) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(String.valueOf(count));
                        } else {
                            textView.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                int count = people_count - comments.get(position).readUsers.size();
                if(count > 0) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(String.valueOf(count));
                } else {
                    textView.setVisibility(View.INVISIBLE);
                }
            }

        }

        private class MessageViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_message;
            public TextView textview_name;
            public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public TextView textView_timestamp;
            public TextView textView_readCounter_left;
            public TextView textView_readCounter_right;

            public MessageViewHolder(View view) {
                super(view);
                textView_message = (TextView) view.findViewById(R.id.messageItem_textview_message);
                textview_name = (TextView) view.findViewById(R.id.messageItem_textview_name);
                imageView_profile = (ImageView) view.findViewById(R.id.messageItem_imageview_profile);
                linearLayout_destination = (LinearLayout) view.findViewById(R.id.messageItem_linearlayout_destination);
                linearLayout_main = (LinearLayout) view.findViewById(R.id.messageItem_linearlayout_main);
                textView_timestamp = (TextView) view.findViewById(R.id.messageItem_textview_timestamp);
                textView_readCounter_left = (TextView) view.findViewById(R.id.messageItem_textview_readCounter_left);
                textView_readCounter_right = (TextView) view.findViewById(R.id.messageItem_textview_readCounter_right);

            }
        }
    }

    /**
     *  채팅방에서 뒤로가기시,
     *  애니메이션 효과 유지되도록 한 것,
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(valueEventListener != null) { // Bug Fixed.
            databaseReference.removeEventListener(valueEventListener);
            finish();
            overridePendingTransition(R.anim.fromleft, R.anim.toright);
        }
    }
}
