package com.commandcenter.devchat.Model;

/**
 * Created by Command Center on 11/19/2017.
 */

public class Request {

    private String userID;
    private String userName;
    private String imgUrl;
    private String request_code;
    private String request_date;

    public Request() {
    }

    public Request(String userID, String userName, String imgUrl, String request_code, String request_date) {
        this.userID = userID;
        this.userName = userName;
        this.imgUrl = imgUrl;
        this.request_code = request_code;
        this.request_date = request_date;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getRequest_code() {
        return request_code;
    }

    public void setRequest_code(String request_code) {
        this.request_code = request_code;
    }

    public String getDate() {
        return request_date;
    }

    public void setDate(String request_date) {
        this.request_date = request_date;
    }
}
