package com.twilio.video.app.model;

public class MemberData {

    private String name;
    private String id;

    public MemberData(String name, String id){
        this.name = name;
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public String getId(){
        return id;
    }
}
