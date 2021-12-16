package Message;

import java.io.Serializable;

public class Temp implements Serializable {
    public String hospital_name;
    public String address;
    public int covaxin_cnt;
    public int covishield_cnt;
    public Temp(){ hospital_name = ""; address = ""; covaxin_cnt = 0; covishield_cnt = 0;}
    public String toString(){
        return String.format("Hospital Name : %s  Address: %s   Covaxin : %d  Covishield : %d ",hospital_name,address, covaxin_cnt, covishield_cnt);
    }
}
