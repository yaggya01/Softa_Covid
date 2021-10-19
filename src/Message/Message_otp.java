package Message;

import User.User;

import java.io.Serializable;

public class Message_otp implements Serializable{
    public int otp;
    public User user;
    public Message_otp(int o){
       otp=o;
    }
    public Message_otp(int o, User u){
        otp=o;
        user=u;
    }
    public String toString() {
        return "OTP: " + otp + "\n" + user.toString();
    }
}