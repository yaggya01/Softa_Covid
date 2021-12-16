package Server;

import Message.Returned_SearchMessage;
import Message.SearchMessage;
import Message.Temp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class HandleClient_SearchByUser implements Runnable {
    final private Socket socket;
    ObjectInputStream oi;
    ObjectOutputStream op;
    public ArrayList<String> v;
    public HandleClient_SearchByUser(Socket s) {
        this.socket = s;
        try {
            oi = new ObjectInputStream(socket.getInputStream());
            op = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                SearchMessage m = (SearchMessage) oi.readObject();
                System.out.println(m);
                String url = "jdbc:mysql://localhost:3306/Covid";
                Connection connection = DriverManager.getConnection(url, "root", "");
                String q = "Select * from HOSPITAL where State=";
                q = q + '"';
                q = q + m.state;
                q = q + '"';
                q = q + " and City=";
                q = q + '"';
                q = q + m.city;
                q = q + '"';
                q = q + ';';
                System.out.println(q);
                PreparedStatement preSat;
                preSat = connection.prepareStatement(q);
                ResultSet rs = preSat.executeQuery();
                Returned_SearchMessage obj = new Returned_SearchMessage();
                while(rs.next()){
                    Temp tmp_obj = new Temp();
                    tmp_obj.hospital_name = rs.getString("name");
                    tmp_obj.address = rs.getString("address") + rs.getString("city") + rs.getString("state");
                    Integer hid = rs.getInt("HID");
                    String q2 = "Select * from vaccine_cnt where HID=";
                    q2 = q2 + hid;
                    q2 = q2 + ';';
                    PreparedStatement preSat2;
                    preSat2 = connection.prepareStatement(q2);
                    ResultSet rs2 = preSat2.executeQuery();
                    while(rs2.next()){
                        String vaccine_name = rs2.getString("vaccine_name");
                        int cnt = rs2.getInt("rem");
                        if(vaccine_name.equals("covaxin")){
                            tmp_obj.covaxin_cnt = cnt;
                        }else{
                            tmp_obj.covishield_cnt = cnt;
                        }
                    }
                    obj.ans.add(tmp_obj);
                }
                op.writeObject(obj);
                op.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
