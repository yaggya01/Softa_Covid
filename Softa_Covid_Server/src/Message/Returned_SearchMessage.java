package Message;

import User.User;

import java.io.Serializable;
import java.util.ArrayList;

public class Returned_SearchMessage implements Serializable{
    public ArrayList<Hosp_info> ans;
    public SearchMessage.job type_of_operation;
    public String StatusOfBookingOperation;
    public String vaccineStatusOfUser;
    public User user;
    public Returned_SearchMessage(){
    }
    public Returned_SearchMessage(SearchMessage.job operation_type, String book_status, String vac_status, User user){
        ans = new ArrayList<Hosp_info>();
        this.user = user;
        type_of_operation=operation_type;
        StatusOfBookingOperation=book_status;
        vaccineStatusOfUser = vac_status;
    }
}

