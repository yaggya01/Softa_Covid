package Server;

import Message.*;
import User.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Vector;

public class HandleClient_Username implements Runnable {
    final private Socket socket;
    ObjectInputStream oi;
    ObjectOutputStream op;

    public HandleClient_Username(Socket s) {
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
                Message m = (Message) oi.readObject();
                System.out.println(m);
                if (m.t == Message.job.login) {                                             //to login user
                    String url = "jdbc:mysql://localhost:3306/Covid";
                    Connection connection = DriverManager.getConnection(url, "root", "");
                    if (m.k == 0) {
                        String q = "Select * from USER where username=";
                        q = q + '"';
                        q = q + m.username;
                        q = q + '"';
                        q = q + ';';
                        System.out.println(q);
                        PreparedStatement preSat;
                        preSat = connection.prepareStatement(q);
                        ResultSet result = preSat.executeQuery();
                        Message_otp k;
                        if (result.next()) {
                            String g = result.getString("Password");
                            ResultSetMetaData metadata = result.getMetaData();
                            int columnCount = metadata.getColumnCount();
                            if (m.password.equals(g)) {                             //checking password
                                Random rand = new Random();
                                int otp= rand.nextInt(9999-1000)+1000;
                                JavaMailUtil.sendMail(result.getString("Email"),otp);           //sending otp via email
                                User user = getUser(result);
                                String q2 = "Select * from doses where username=";
                                q2 = q2 + '"';
                                q2 = q2 + m.username;
                                q2 = q2 + '"';
                                q2 = q2 + " and done = 0";
                                q2 = q2 + ';';
                                PreparedStatement preSat2;
                                preSat2 = connection.prepareStatement(q2);
                                ResultSet result2 = preSat2.executeQuery();

                                String q3 = "Select * from doses where username=";
                                q3 = q3 + '"';
                                q3 = q3 + m.username;
                                q3 = q3 + '"';
                                q3 = q3 + " and done = 2";
                                q3 = q3 + ';';
                                PreparedStatement preSat3;
                                preSat3 = connection.prepareStatement(q3);
                                ResultSet result3 = preSat3.executeQuery();
                                if(result2.next()){
                                    String temp = "1 booking of " + result2.getString("vaccine_name") + " on " + result2.getDate("date");
                                    user.setBooking_status(temp);
                                }else if(result3.next()){
                                    String temp = "1 waitlist of " + result3.getString("vaccine_name") + " on " + result3.getDate("date");
                                    user.setBooking_status(temp);
                                }

                                String q4 = "Select d.*,h.name from doses d,hospital h where username=";
                                q4 = q4 + '"';
                                q4 = q4 + m.username;
                                q4 = q4 + '"';
                                q4 = q4 + " and done = 1 and d.hid=h.hid";
                                q4 = q4 + " order by date";
                                q4 = q4 + ';';
                                PreparedStatement preSat4;
                                preSat4 = connection.prepareStatement(q4);
                                ResultSet result4 = preSat4.executeQuery();

                                Vector<Vector<String>> doses = new Vector<Vector<String>>();
                                while(result4.next()) {
                                    Vector<String> td = new Vector<String>();
                                    td.add(result4.getString("vaccine_name"));
                                    td.add(result4.getString("which_dose"));
                                    td.add(result4.getDate("date").toString());
                                    td.add(result4.getString("name"));
                                    doses.add(td);
                                    System.out.println(result4.getString("vaccine_name") + " " + result4.getString("which_dose") + " " + result4.getDate("date") + " " + result4.getString("name"));
                                }
                                user.setDoses(doses);

                                if(result2.next()){
                                    String temp = "1 booking of " + result2.getString("vaccine_name") + " on " + result2.getDate("date");
                                    user.setBooking_status(temp);
                                }else if(result3.next()){
                                    String temp = "1 waitlist of " + result3.getString("vaccine_name") + " on " + result3.getDate("date");
                                    user.setBooking_status(temp);
                                }

                                k = new Message_otp(otp, user);
                                int cnt=0;

                                System.out.println("HandleClient_Username -> " + k);
                            } else {
                                k = new Message_otp(2);
                            }
                        } else {
                            k = new Message_otp(1);
                        }
                        op.writeObject(k);
                        op.flush();
                    }
                }
                else if (m.t == Message.job.signup) {                                           //for user signup
                    String url = "jdbc:mysql://localhost:3306/Covid";
                    Connection connection = DriverManager.getConnection(url, "root", "");
                    if (m.k == 0) {
                        String q = "Select * from USER where username=";
                        q += '"';
                        q+=m.username;
                        q += '"';
                        q+=";";
                        PreparedStatement preSat;
                        preSat = connection.prepareStatement(q);
                        ResultSet result = preSat.executeQuery();
                        if (result.next()) {
                            System.out.println("User Name: " + result.getString("Name") + " Password: " + result.getString("Password") + " Email: " + result.getString("Email"));
                            op.writeObject(new Message_otp(1));
                            return;
                        }
                        String query1 = "Insert into USER values (?,?,?,?,?,?,?,?)";
                        preSat = connection.prepareStatement(query1);
                        preSat.setString(1, m.username);
                        preSat.setString(2, m.name);
                        preSat.setLong(3, m.num);
                        preSat.setString(4, m.email);
                        preSat.setString(5, m.password);
                        preSat.setBytes(6, null);
                        preSat.setBytes(7, null);
                        preSat.setBytes(8, null);
                        System.out.println(query1);
                        preSat.execute();
                        op.writeObject(new Message_otp(0));
                        return;
                    }
                }else if (m.t == Message.job.signup_official) {                                 //for official signup
                    String url = "jdbc:mysql://localhost:3306/Covid";
                    Connection connection = DriverManager.getConnection(url, "root", "");
                    if (m.k == 0) {
                        String q = "Select * from govt_official where username=";
                        q += '"';
                        q+=m.username;
                        q += '"';
                        q+=";";
                        PreparedStatement preSat;
                        preSat = connection.prepareStatement(q);
                        ResultSet result = preSat.executeQuery();
                        if (result.next()) {
                            System.out.println("User Name: " + result.getString("uername") + " Password: " + result.getString("Password") + " Email: " + result.getString("Email"));
                            op.writeObject(new Message_otp(1));
                            return;
                        }
                        String query1 = "Insert into govt_official values (?,?,?,?)";
                        preSat = connection.prepareStatement(query1);
                        preSat.setString(1, m.username);
                        preSat.setLong(2, m.num);
                        preSat.setString(3, m.email);
                        preSat.setString(4, m.password);
                        System.out.println(query1);
                        preSat.execute();
                        op.writeObject(new Message_otp(0));
                        return;
                    }
                }
                else if (m.t == Message.job.login_gov) {                                        //login government official
                    String url = "jdbc:mysql://localhost:3306/Covid";
                    Connection connection = DriverManager.getConnection(url, "root", "");
                    if (m.k == 0) {
                        String q = "Select * from govt_official where username=";
                        q = q + '"';
                        q = q + m.username;
                        q = q + '"';
                        q = q + ';';
                        System.out.println(q);
                        PreparedStatement preSat;
                        preSat = connection.prepareStatement(q);
                        ResultSet result = preSat.executeQuery();
                        Message_otp k = null;
                        if (result.next()) {
                            String g = result.getString("Password");
                            ResultSetMetaData metadata = result.getMetaData();
                            int columnCount = metadata.getColumnCount();
                            if (m.password.equals(g)) {
                                Random rand = new Random();
                                int otp= rand.nextInt(9999)+3;
                                JavaMailUtil.sendMail(result.getString("Email"),otp);
                                User user = getUser(result);
                                if(user == null)
                                System.out.println("User is null");
                                k = new Message_otp(otp, user);
                                System.out.println("HandleClient_Username -> " + k);
                            } else {
                                k = new Message_otp(2);
                            }
                        } else {
                            System.out.println("Resultset is null");
                            k = new Message_otp(1);
                        }
                        op.writeObject(k);
                        op.flush();
                    }
                }
                else if(m.t == Message.job.Vac_status){                                         //
                    System.out.println("In Vac_status of Handleclient");
                    String url = "jdbc:mysql://localhost:3306/Covid";
                    Connection connection = DriverManager.getConnection(url, "root", "");
                    String q = "Select * from doses where username=";
                    q = q + '"';
                    q = q + m.username;
                    q = q + '"';
                    q = q + ';';
                    System.out.println(q);
                    PreparedStatement preSat;
                    preSat = connection.prepareStatement(q);
                    ResultSet result = preSat.executeQuery();
                    Status_Message obj = new Status_Message();
                    while(result.next()){
                        String s = "";
                        s = s + "Vaccine: ";
                        s = s + result.getString("vaccine_name");
                        s = s + " Which Dose: ";
                        s = s + result.getInt("which_dose");
                        s = s + " Date: ";
                        s = s + result.getDate("date");
                        s = s + " Status: ";
                        s = s + (result.getBoolean("done") ? "Taken\n" : "Not taken\n");
                        System.out.println(s);
                        obj.v.add(s);
                    }
                    op.writeObject(obj);
                    op.flush();
                }
                else if(m.t==Message.job.Vac_update){
                    System.out.println("HIIII");
                    String url = "jdbc:mysql://localhost:3306/Covid";
                    Connection connection = DriverManager.getConnection(url, "root", "");
                    String q = "Update doses set done = 1 where username = ";
                    q = q + '"';
                    q = q + m.username;
                    q = q + '"';
                    q = q + ';';
                    System.out.println(q);
                    PreparedStatement preSat;
                    preSat = connection.prepareStatement(q);
                    preSat.executeUpdate();

                    q = "SELECT email from user where username = ";
                    q = q + '"';
                    q = q + m.username;
                    q = q + '"';
                    q = q + ';';
                    System.out.println(q);
                    preSat = connection.prepareStatement(q);
                    ResultSet result = preSat.executeQuery();
                    if(result.next()) {
                        JavaMailUtil.sendMail(result.getString("Email"), 10000);
                    }

                }
                else if(m.t==Message.job.Time){
                    String url = "jdbc:mysql://localhost:3306/Covid";
                    Connection connection = DriverManager.getConnection(url, "root", "");
                    if(m.k==0){
                        String q="Select Count(username) As Ans,date,done from doses group by date having done = 1;";
                        System.out.println(q);
                        PreparedStatement preSat;
                        preSat = connection.prepareStatement(q);
                        ResultSet result = preSat.executeQuery();
                        if(result.next()){
                            Message_Time_Graph k = new Message_Time_Graph(result.getInt("Ans")+2);

                            int i=0;
                            k.a[i][0]="2021-12-21";
                            k.a[i][1]="0";
                            i++;
                            while (result.next()){
                                k.a[i][0]=result.getString("Date");
                                k.a[i][1]=result.getString("Ans");
                                i++;
                            }
                            op.writeObject(k);
                            op.flush();
                        }
                    }
                    else if(m.k==1){
                        String q="Select Count(*) As Ans,date,done from doses group by date having done = 1 and date >";
                        q+='"';
                        q+="2021-11-27";
                        q+='"';
                        q+=';';
                        System.out.println(q);
                        PreparedStatement preSat;
                        preSat = connection.prepareStatement(q);
                        ResultSet result = preSat.executeQuery();
                        if(result.next()) {
                            Message_Time_Graph k = new Message_Time_Graph(31);
                            int i = 0;
                            k.a[i][0] = "2021-11-27";
                            k.a[i][1] = "0";
                            i++;
                            while (result.next() && i < 31) {
                                k.a[i][0] = result.getString("Date");
                                k.a[i][1] = result.getString("Ans");
                                System.out.println(k.a[i][0] + " " + k.a[i][1]);
                                i++;
                            }
                            k.num = i;
                            op.writeObject(k);
                            op.flush();
                        }
                    }
                    else if(m.k==2){
                        String q="Select Count(*) As Ans,date,done from doses group by date having done = 1 and date >";
                        q+='"';
                        q+="2021-12-20";
                        q+='"';
                        q+=';';
                        System.out.println(q);
                        PreparedStatement preSat;
                        preSat = connection.prepareStatement(q);
                        ResultSet result = preSat.executeQuery();
                        if(result.next()) {
                            Message_Time_Graph k = new Message_Time_Graph(31);
                            int i = 0;
                            k.a[i][0] = "2021-12-20";
                            k.a[i][1] = "0";
                            i++;
                            while (result.next() && i < 8) {
                                k.a[i][0] = result.getString("Date");
                                k.a[i][1] = result.getString("Ans");
                                i++;
                            }
                            k.num = i;
                            op.writeObject(k);
                            op.flush();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    User getUser(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metadata = resultSet.getMetaData();
        int numberOfCols = metadata.getColumnCount();

        User user = new User();

        for(int i=1; i <= numberOfCols; i ++) {
            switch (metadata.getColumnName(i)) {
                case "username":
                    user.setUsername(resultSet.getString(i));
                    break;
                case "name":
                    user.setName(resultSet.getString(i));
                    break;
                case "number":
                    user.setNumber(resultSet.getString(i));
                    break;
                case "email":
                    user.setEmail(resultSet.getString(i));
                    break;
                case "idproof":
                    user.setIdProof(resultSet.getBytes(i));
                    break;
                case "photo":
                    user.setPhoto(resultSet.getBytes(i));
                    break;
                default:
                    System.out.println("Unexpected value : " + metadata.getColumnName(i));
            }
        }

        return user;
    }

}
