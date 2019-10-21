package com.example.ahntalk.model;

public class NotificationModel {
    public String to;
    public Notification notification = new Notification();
    public Data data = new Data();
    public static class Notification {
        public String title;
        public String text;
    }
    /**
     *  참고!
     *
     *  Foreground는 "data" 객체명 사용
     *  Background는 "notification" 객체명 사용
     */
    public static class Data {
        public String title;
        public String text;
    }
}
