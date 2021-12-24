package Message;

import User.User;

import java.io.Serializable;

public class SearchMessage implements Serializable{
    /**
     *
     */
    public enum job{
        initialize, search_slots, book_slots, cancel_booking
    }
    public String city;
    public String state;
    public int hid;
    public String vaccine_name;
    public User user;
    public job which_operation;
    public SearchMessage(String n, String p, int h, String v, User u, int b){
        state = n; city = p;
        hid=h; vaccine_name=v; user=u;
        if(b == 0) which_operation = job.initialize;
        else if(b==1) which_operation=job.search_slots;
        else if(b==2) which_operation = job.book_slots;
        else which_operation = job.cancel_booking;
    }
    public String toString(){
        return String.format("State: %s  City: %s  ",state,city);
    }
}