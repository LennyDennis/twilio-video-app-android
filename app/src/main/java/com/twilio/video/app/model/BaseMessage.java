package com.twilio.video.app.model;

import java.util.Date;

public class BaseMessage {
    private String text;
    private MemberData memberData;
    private long created;
    private String messageId;
    private String room;

    public BaseMessage(String id, String text, MemberData memberData, long created, String room){
        this.text = text;
        this.memberData = memberData;
        this.created = created;
        this.messageId = id;
        this.room = room;
    }

    public BaseMessage(String id, String text, String name, String senderId, long created, String room){
        this.text = text;
        this.memberData = new MemberData(name, senderId);
        this.messageId = id;
        this.created = created;
        this.room = room;
    }

    public BaseMessage(String id, String text, MemberData memberData){
        this.text = text;
        this.memberData = memberData;
        this.created = new Date().getTime();
        this.messageId = id;
    }

    public String getText(){
        return text;
    }

    public MemberData getMemberData(){
        return memberData;
    }

    public long getCreated(){
        return created;
    }
}
