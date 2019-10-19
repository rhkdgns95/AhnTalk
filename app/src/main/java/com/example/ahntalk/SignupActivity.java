package com.example.ahntalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ahntalk.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.firebase.storage.UploadTask;

public class SignupActivity extends AppCompatActivity {

    private static final int PICK_FROM_ALBUM = 10;
    private EditText email;
    private EditText name;
    private EditText password;
    private Button signup;
    private String splash_background;
    private ImageView profile;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        splash_background = mFirebaseRemoteConfig.getString(getString(R.string.rc_color));

        getWindow().setStatusBarColor(Color.parseColor(splash_background));

        profile = (ImageView) findViewById(R.id.signupActivity_imageview_profile);

        /**
         *  [앨범 열기] 이벤트 추가
         *
         *  프로필 클릭 시,
         *  앨범열기 후 선택한사진을
         *  Storage에 저장시키기.
         *
         */
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM);
            }
        });
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
                if(imageUri == null) {
                    Toast.makeText(getApplicationContext(), "프로필 사진을 등록해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            /**
                             *  (2) Firebase의 Realtime Database / Storage.
                             *
                             *  [tools] - [Firebase] - [Realtime Database]를 추가.
                             *  [tools] - [Firebase] - [Storage]를 추가.
                             *
                             *  회원가입 버튼 클릭 - 1
                             *  Storage에 프로필 사진 저장 - 2
                             *  UserModel객체 생성 후 회원정보 저장하기 - 3
                             *  고유의 UID에 해당하는 값에 UserMdoel 객체 저장하기.
                             *  결과: [Database]에서 ahntalk/users/생성된계정의_UID/userModel
                             */
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                final String uid = task.getResult().getUser().getUid();
                                final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child("userImages").child(uid);
                                profileImageRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                        /**
                                         *  Image Load 변경.
                                         *
                                         *  이전 소스코드
                                         *  String imageUrl = profileImageRef.getDownloadUrl().toString();
                                         */
                                        Task<Uri> uriTask = profileImageRef.getDownloadUrl();
                                        while(!uriTask.isSuccessful());
                                        Uri downloadUrl = uriTask.getResult();
                                        String imageUrl = String.valueOf(downloadUrl);


                                        UserModel userModel = new UserModel();
                                        userModel.userName = name.getText().toString();
                                        userModel.profileImageUrl = imageUrl;

                                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel);
                                    }
                                });
                            }
                        });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            profile.setImageURI(data.getData()); // 가운데 뷰를 바꾼다.
            imageUri = data.getData(); // 이미지 경로 원본
        }
//        super.onActivityResult(requestCode, resultCode, data);
    }
}
