package Message;

import java.io.Serializable;
public class Message implements Serializable{
    /**
     *
     */
    public enum job{
        login,signup,signup_official,login_gov,Vac_update, Time, Vac_status
    }
    private static final long serialVersionUID = 1L;
    public String name;
    public String username;
    public String password;
    public String email;
    public long num;
    public int k;
    public job t;
    public Message(String u, String n,String e,String pass, int i,int j){
        username = u;
        name = n;
        password = pass;
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
        }else if(j == 10){
            t=job.signup_official;
        }
        else if(j == 7){
            t=job.Vac_status;
        }
        else{
            t=job.Time;
        }
    }
    public String toString(){
        return String.format("UserName: %s\nPassword: %s",username,password);
    }
}