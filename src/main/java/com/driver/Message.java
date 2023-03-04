package com.driver;

import java.util.Date;

public class Message {
    private int id;
    private String content;
    private Date timestamp;

    public Message(int id){
        this.id = id;
        timestamp = new Date();
    }

    public Message(int id, String content){
        this.id = id;
        this.content = content;
        timestamp = new Date();
    }

    public Message(int id, String content, Date timestamp){
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public void setContent(String content){
        this.content = content;
    }

    public String getContent(){
        return this.content;
    }

    public void setTimestamp(Date timestamp){
        this.timestamp = timestamp;
    }

    public Date getTimestamp(){
        return this.timestamp;
    }

}
