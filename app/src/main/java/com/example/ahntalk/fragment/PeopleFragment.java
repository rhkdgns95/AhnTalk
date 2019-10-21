package com.example.ahntalk.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PeopleFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.peoplefragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new PeopleFragmentRecyclerViewAdapter());

        return view;
    }

    /**
     *  (1) Recycler View 생성
     *
     *  * myUid
     *  현재 로그인한 user의 uid이며,
     *  자신의 데이터는 채팅목록에서
     *  제외 시켜주면 된다.
     */
    class PeopleFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<UserModel> userModels;
        public PeopleFragmentRecyclerViewAdapter() {
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
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

            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.frienditem_imageview);
                textView = (TextView) view.findViewById(R.id.frienditem_textview);
                textView_comment = (TextView) view.findViewById(R.id.frienditem_textview_comment);
            }
        }
    }
}
