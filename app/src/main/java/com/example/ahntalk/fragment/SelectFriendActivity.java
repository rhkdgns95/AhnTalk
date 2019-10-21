package com.example.ahntalk.fragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ahntalk.R;
import com.example.ahntalk.chat.MessageActivity;
import com.example.ahntalk.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 *  SelectFriendActivity
 *
 *  (친구목록 추가)
 *  Button 클릭 시,
 *  대화방에 초대할 Checkbox가능한
 *  친구목록 확인.
 */
public class SelectFriendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend);

        RecyclerView recyclerView = findViewById(R.id.selectFriendActivity_recyclerview);
        recyclerView.setAdapter(new SelectFriendActivityAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    class SelectFriendActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<UserModel> userModels;
        public SelectFriendActivityAdapter() {
            userModels = new ArrayList<>();
            final String myUid = FirebaseAuth.getInstance().getUid();
            FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // 데이터 모델이 바뀔수도있다.
                    // 누적되는 데이터를 없애준다.
                    userModels.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserModel userModel = snapshot.getValue(UserModel.class);

                        if(userModel.uid.equals(myUid)) {
                            continue;
                        }
                        userModels.add(userModel);
                    }
                    // 데이터가 쌓이고나서 새로고침을 해야 친구목록이 뜬다.
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_select, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return userModels.size();
        }

        /**
         *  (2) 유저의 이미지 추가하기.
         */
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position, @NonNull List<Object> payloads) {
            Glide.with(holder.itemView.getContext())
                    .load(userModels.get(position).profileImageUrl)
                    .apply(new RequestOptions().circleCrop())
                    .into(((CustomViewHolder)holder).imageView);
            ((CustomViewHolder)holder).textView.setText(userModels.get(position).userName);

            /**
             *  (3) 유저 클릭시, 채팅창 띄우기.
             *
             *  애니메이션 효과 추가.
             */
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), MessageActivity.class);
                    intent.putExtra("destinationUid", userModels.get(position).uid);
                    /**
                     *  주의!
                     *
                     *  버전: JELLY_BEAN 이상.
                     */
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fromright, R.anim.toleft);
                    startActivity(intent, activityOptions.toBundle());
                }
            });
            /**
             *  user가 comment를 작성한 경우만
             *  띄우도록 하기.
             */
            if(userModels.get(position).comment != null) {
                ((CustomViewHolder) holder).textView_comment.setText(userModels.get(position).comment);
            }
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView;
            public TextView textView_comment;
            public CheckBox checkbox;

            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.frienditem_imageview);
                textView = (TextView) view.findViewById(R.id.frienditem_textview);
                textView_comment = (TextView) view.findViewById(R.id.frienditem_textview_comment);
                checkbox = (CheckBox) view.findViewById(R.id.friendItem_checkbox);
            }
        }
    }
}
