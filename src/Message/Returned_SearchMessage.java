package Message;

import java.io.Serializable;
import java.util.ArrayList;

public class Returned_SearchMessage implements Serializable{
    public ArrayList<Hosp_info> ans;
    public SearchMessage.job type_of_operation;
    public String StatusOfBookingOperation;
    public String vaccineStatusOfUser;
    public Returned_SearchMessage(){
    }
    public Returned_SearchMessage(SearchMessage.job operation_type, String book_status, String vac_status){
        ans = new ArrayList<Hosp_info>();
        type_of_operation=operation_type;
        StatusOfBookingOperation=book_status;
        vaccineStatusOfUser = vac_status;
    }
}

