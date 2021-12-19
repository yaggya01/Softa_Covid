package Message;

import java.io.Serializable;

public class Message_Time_Graph implements Serializable {
    public int num;
    public String a[][];
    public Message_Time_Graph(int n){
        num=n;
        a=new String[n][2];
    }
    public String toString(){
        return "Message_Time_Graph";
    }
}
