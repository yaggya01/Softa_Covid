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
                if (m.t == Message.job.login) {
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
                            if (m.password.equals(g)) {
                                Random rand = new Random();
                                int otp= rand.nextInt(9999);
                                JavaMailUtil.sendMail(result.getString("Email"),otp);
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
                                q3 = q3 + " and done = 0";
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
                else if (m.t == Message.job.signup) {
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
                }else if (m.t == Message.job.signup_official) {
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
                else if (m.t == Message.job.login_gov) {
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
                else if(m.t == Message.job.Vac_status){
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
                    //JavaMailUtil.sendMail(result.getString("Email"),10000);
                }
                else if(m.t==Message.job.Time){
                    String url = "jdbc:mysql://localhost:3306/Covid";
                    Connection connection = DriverManager.getConnection(url, "root", "");
                    if(m.k==0){
                        String q="Select Count(Distinct(Date)) As Ans from doses where done = 1";
                        System.out.println(q);
                        PreparedStatement preSat;
                        preSat = connection.prepareStatement(q);
                        ResultSet result = preSat.executeQuery();
                        if(result.next()){
                            Message_Time_Graph k = new Message_Time_Graph(result.getInt("Ans")+2);
                            q="Select Count(Distinct(username)) as Ans, Date From doses As v where exists( Select Distinct(Date) As Date from doses where done = 1);";
                            preSat = connection.prepareStatement(q);
                            result = preSat.executeQuery();
                            int i=0;
                            k.a[i][0]="2021-10-15";
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
                        Message_Time_Graph k = new Message_Time_Graph(31);
                        String q="Select Count(Distinct(username)) as Ans, Date From doses As v where exists( Select Distinct(Date) As Date from doses Order by Date DESC);";
                        PreparedStatement preSat;
                        preSat = connection.prepareStatement(q);
                        ResultSet result = preSat.executeQuery();
                        int i=0;
                        k.a[i][0]="2021-10-15";
                        k.a[i][1]="0";
                        i++;
                        while (result.next()&&i<31){
                            k.a[i][0]=result.getString("Date");
                            k.a[i][1]=result.getString("Ans");
                            i++;
                        }
                        k.num=i;
                        op.writeObject(k);
                        op.flush();
                    }
                    else if(m.k==2){
                        Message_Time_Graph k = new Message_Time_Graph(8);
                        String q="Select Count(Distinct(username)) as Ans, Date From doses As v where exists( Select Distinct(Date) As Date from doese Order by Date DESC);";
                        PreparedStatement preSat;
                        preSat = connection.prepareStatement(q);
                        ResultSet result = preSat.executeQuery();
                        int i=0;
                        k.a[i][0]="2021-10-15";
                        k.a[i][1]="0";
                        i++;
                        while (result.next()&&i<8){
                            k.a[i][0]=result.getString("Date");
                            k.a[i][1]=result.getString("Ans");
                            i++;
                        }
                        k.num=i;
                        op.writeObject(k);
                        op.flush();
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
