package Message;
import java.io.Serializable;
public class Message_otp implements Serializable{
    public int otp;
    public Message_otp(int o){
       otp=o;
    }
    public String toString(){
        return String.format("otp:",otp);
    }
}