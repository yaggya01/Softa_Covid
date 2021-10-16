package Message;

import java.io.Serializable;
public class Message implements Serializable{
    /**
     *
     */
    public enum job{
        login,signup,login_gov,Vac_update, Time
    }
    private static final long serialVersionUID = 1L;
    public String name;
    public String password;
    public String email;
    public long num;
    public int k;
    public job t;
    public Message(String n,String p,String e, int i,int j){
        name = n;
        password = p;
        email=e;
        k=i;
        if(j==0){
            t = job.login;
        }
        else if(j==1){
            t=job.signup;
        }
        else if(j==2){
            t=job.login_gov;
        }
        else if(j==3){
            t=job.Vac_update;
        }
        else{
            t=job.Time;
        }
    }
    public String toString(){
        return String.format("UserName: %s\nPassword: %s",name,password);
    }
}