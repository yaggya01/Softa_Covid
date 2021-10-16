package Message;

import java.io.Serializable;
import java.util.ArrayList;

public class Returned_SearchMessage implements Serializable {
    public ArrayList<String> v;
    public Returned_SearchMessage(ArrayList<String> a){
        v=a;
    }
}
