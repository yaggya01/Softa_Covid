package Server;

import Message.Returned_SearchMessage;
import Message.SearchMessage;

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
                ResultSet result = preSat.executeQuery();
                Returned_SearchMessage k;
                v = new ArrayList<String>();
//                if (result.next()) {
                    System.out.println("gfhgv ghrjk");
//                    String g = result.getString("Password");
//                    if (m.password.equals(g)) {
//                        Random rand = new Random();
//                        int otp= rand.nextInt(9999);
//                        JavaMailUtil.sendMail(result.getString("Email"),otp);
//                        k = new Message_otp(otp);
//                        System.out.println(k);
//                    } else {
//                        k = new Message_otp(2);
//                    }
                    while(result.next()){
                        //Retrieve by column name
                        String s = "";
                        s = s + result.getString("Name");
                        s = s + result.getString("City");
                        s = s + result.getString("State");
                        System.out.println(s);
                        v.add(s);
                    }
                    k = new Returned_SearchMessage(v);
//                } else {
//                    k = new Returned_SearchMessage(v);
//                }
                op.writeObject(k);
                op.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
