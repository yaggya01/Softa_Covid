package Message;

import java.io.Serializable;
import java.util.ArrayList;

public class Hosp_info implements Serializable {
    public int HID;
    public String hospital_name;
    public String address;
    public ArrayList<String> vac;
    public Hosp_info(){ HID = -1; hospital_name = ""; address = ""; vac = new ArrayList<String>();}
    public String toString(){
        String str="";
        str = str + " HID: ";
        str = str + HID;
        str = str + " Hospital: ";
        str = str + hospital_name;
        str = str + " ";
        str = str + address;
        for(int i=0; i<vac.size(); i++){
            str = str + vac.get(i) + " ";
        }
        return str;
    }
}
