package com.example.ahntalk.model;

import java.util.HashMap;
import java.util.Map;

public class ChatModel {
    /**
     *  users: 채팅방 유저들.
     *  comments: 채팅방 내용들.
     */
    public Map<String, Boolean> users = new HashMap<>();
    public Map<String, Comment> comments = new HashMap<>();

    public static class Comment {
        public String uid;
        public String message;
        public Object timestamp;
        public Map<String, Object> readUsers = new HashMap<>();
    }
}
