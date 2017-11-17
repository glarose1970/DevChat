package com.commandcenter.devchat.Model;

import java.sql.Time;
import java.util.Date;

/**
 * Created by Command Center on 11/8/2017.
 */

public class ChatboxMessage {

    private String User;
    private String ChatMessage;
    private String Rank;
    private String Date;
    private String Time;
    private String Avatar_Url;


    public ChatboxMessage() {
    }

    public ChatboxMessage(String user, String chatMessage, String rank, String date, String time, String avatar_Url) {
        User = user;
        ChatMessage = chatMessage;
        Rank = rank;
        Date = date;
        Time = time;
        Avatar_Url = avatar_Url;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getChatMessage() {
        return ChatMessage;
    }

    public void setChatMessage(String chatMessage) {
        ChatMessage = chatMessage;
    }

    public String getRank() {
        return Rank;
    }

    public void setRank(String rank) {
        Rank = rank;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getAvatar_Url() {
        return Avatar_Url;
    }

    public void setAvatar_Url(String avatar_Url) {
        Avatar_Url = avatar_Url;
    }
}
