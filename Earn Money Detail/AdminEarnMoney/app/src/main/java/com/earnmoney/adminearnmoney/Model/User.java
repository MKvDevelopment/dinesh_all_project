package com.earnmoney.adminearnmoney.Model;



public class User
{
    private String token, name, email, status, image,last_msg;
    private String date,seen;

    public User()
    {

    }

    public User(String token, String name, String email, String status,String seen, String image,String last_msg, String date)
    {
        this.token = token;
        this.name = name;
        this.email = email;
        this.status = status;
        this.image = image;
        this.date = date;
        this.seen = seen;
        this.last_msg = last_msg;
    }

    public String getLast_msg() {
        return last_msg;
    }

    public void setLast_msg(String last_msg) {
        this.last_msg = last_msg;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }
}
