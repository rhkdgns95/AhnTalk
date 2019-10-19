# Ahn Talk
- Firebase를 활용하여 Android 채팅톡 만들기.
> API 23: Android 6.0 (Marshmallow)
> 아이콘 파일 다운로드: https://www.iconfinder.com에서 상단의 SearchBar 검색 후 [free]선택하여 다운로드

- Firebase 연동하기
> 1. https://console.firebase.google.com Firebase 생성하기 프로젝트 명 'AhnTalk'.
> 2. 앱 등록하기 - https://console.firebase.google.com/project/ahntalk/overview
> > * 구성파일 다운로드 - google-service.json 다운로드 후 app/google-service.json처럼 app폴더 안에 저장.
> > * Firebase SDK 추가.
> 3. 안드로이드 스튜디오 [Tools] - Firebase 클릭 -> Set up Firebase Remote Config.
> 4. Connect your app to Firebase클릭.
> 5. Add Remote Config to your app 클릭.
> 6. Set in-app default parameter values코드 복사 후 app/res/xml/default_config 생성하여 복사하기.
> 7. https://console.firebase.google.com/project/ahntalk/config 에서 원격 데이터 Key-value 저장하기.
> > * 매개변수(1) splash_background: "#ed95ff"
> > * 매개변수(2) splash_cap: false // 먼저, true로 설정하여 서버점검중 메시지 확인 이후 종료되는지 확인해본다음, false로 설정 할 것.
> > * 매개변수(3) splash_background: "서버 점검중입니다."
> 8. [변경사항 게시] 클릭.

- Design 추가
> * build.gralde(Module: app)에 implementation 'com.google.android.material:material:1.0.0' 추가.

- Firebase 로그인 계정 설정
> 1. [Tools] - Firebase 클릭
> 2. Authentication
> > 1. Connect your app to Firebase. - Connected
> > 2. Add Firebase Authentication to your app. - [Accept Changed] 클릭.
> > 3. Firebase 웹 페이지 - 개발 - [Authentication] - [로그인 방법] - [이메일/비밀번호] - 사용여부 설정.

- Realtime Database 설정
> 1. Firebase 웹 페이지 - [Database]클릭 - [Realtime Database]생성하기 클릭.
> 2. 안드로이드 스튜디오 - [Tools] - Firebase 클릭
> 3. Realtime Database
> > * Connect your app to Firebase
> > * Add the Realtime Database to your app. - [Accpet Changed] 클릭.

- Storage 연동
> 1. Firebase 웹 페이지 - [Storage]클릭 - [시작하기]클릭.
> 2. 안드로이드 스튜디오 - [Tools] - Firebase 클릭
> 3. Storage
> > * Connect your app to Firebase
> > * Add Cloud Storage to your app. - [Accept Changed] 클릭.

## Todo
- [x] 'Ahn' Project 생성 및 MainActivity -> SplashActivity 이름변경.
- [x] 로딩 아이콘 파일 다운로드 및 Firebase연동하기.
- [x] LoginActivity (Empty Activity생성)생성 후 원격 데이터로 테마적용하기 - 로그인 레이아웃.
- [x] SignupActivity - Firebase로 계정생성 후 Database에 계정의 UID와 함께 유저정보 저장 - 회원가입 레이아웃.
- [x] SignupActivity - Firebase의 Storage에 프로필 사진 저장 - 회원가입 레이아웃 - 2.