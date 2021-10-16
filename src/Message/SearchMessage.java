package Message;

import java.io.Serializable;

public class SearchMessage implements Serializable{
    /**
     *
     */
//    public enum job{
//        login,signup,login_gov
//    }
    public String city;
    public String state;
    public SearchMessage(String n, String p){
        state = n; city = p;
    }
    public String toString(){
        return String.format("State: %s\nCity: %s",state,city);
    }
}