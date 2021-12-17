package Message;

import java.io.Serializable;

public class Status_updated implements Serializable {
    public String status_update;
    public Status_updated(){status_update = "NOT updated";}
    public String toString(){
        return String.format("Status report: %s\n",status_update);
    }
}
