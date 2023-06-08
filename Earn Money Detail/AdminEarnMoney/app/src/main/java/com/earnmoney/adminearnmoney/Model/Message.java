package com.earnmoney.adminearnmoney.Model;



public class Message
{
    private String message, type, from, to,msg_key;
    private long timestamp;

    public Message()
    {

    }

    public Message(String message, String type, String from, String to, long timestamp,String msg_key)
    {
        this.message = message;
        this.type = type;
        this.msg_key = msg_key;
        this.from = from;
        this.to = to;
        this.timestamp = timestamp;
    }

    public String getMsg_key() {
        return msg_key;
    }

    public void setMsg_key(String msg_key) {
        this.msg_key = msg_key;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getFrom()
    {
        return from;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

    public String getTo()
    {
        return to;
    }

    public void setTo(String to)
    {
        this.to = to;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }
}
