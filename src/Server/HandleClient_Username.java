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
                        String q = "Select * from USER where Number=";
                        q = q + '"';
                        q = q + m.name;
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

                                PreparedStatement preSat1;
                                String q1 = "SELECT VACSTATUS FROM VACCINATION WHERE NUMBER=" + m.name + ";";
                                preSat1 = connection.prepareStatement(q1);
                                ResultSet result1 = preSat1.executeQuery();
                                if(result.next()) {
                                    user.setVaccinationStatus(result1.getInt("VACSTATUS"));
                                }

                                k = new Message_otp(otp, user);
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
                        String q = "Select * from USER where Number=";
                        q+=m.num;
                        q+=";";
                        PreparedStatement preSat;
                        preSat = connection.prepareStatement(q);
                        ResultSet result = preSat.executeQuery();
                        if (result.next()) {
                            System.out.println("User Name: " + result.getString("Name") + " Password: " + result.getString("Password") + " Email: " + result.getString("Email"));
                            op.writeObject(new Message_otp(1));
                            return;
                        }
                        String query1 = "Insert into USER values (?,?,?,?,?,?)";
                        preSat = connection.prepareStatement(query1);
                        preSat.setString(1, m.name);
                        preSat.setLong(2, m.num);
                        preSat.setString(3, m.email);
                        preSat.setString(4, m.password);
                        preSat.setBytes(5, null);
                        preSat.setBytes(6, null);
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
                        String q = "Select * from USER where Name=";
                        q = q + '"';
                        q = q + m.name;
                        q = q + '"';
                        q = q + ';';
                        System.out.println(q);
                        PreparedStatement preSat;
                        preSat = connection.prepareStatement(q);
                        ResultSet result = preSat.executeQuery();
                        Message_otp k = null;
                        if (result.next()) {
                            String g = result.getString("Password");
                            if (m.password.equals(g)) {
                                String p = "Select * from Government where Number=";
                                p = p + '"';
                                p = p + result.getString("Number");;
                                p = p + '"';
                                p = p + ';';
                                System.out.println(p);
                                PreparedStatement l;
                                l = connection.prepareStatement(p);
                                ResultSet r = l.executeQuery();
                                if(r.next()){
                                    k = new Message_otp(0);
                                }
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
                else if(m.t==Message.job.Vac_update){
                    System.out.println("HIIII");
                    String url = "jdbc:mysql://localhost:3306/Covid";
                    Connection connection = DriverManager.getConnection(url, "root", "");
                    String q = "Select * from Vaccination where Number=";
                    q = q + '"';
                    q = q + m.num;
                    q = q + '"';
                    q = q + ';';
                    System.out.println(q);
                    PreparedStatement preSat;
                    preSat = connection.prepareStatement(q);
                    ResultSet result = preSat.executeQuery();
                    if(result.next()){

                        op.writeObject(new Message_otp(result.getInt("VacStatus")));

                        if(result.getInt("VacStatus")==2){
                            System.out.println("popop");
                            return;
                        }
                        System.out.println("popoqqq");
                        Message_otp k = (Message_otp) oi.readObject();
                        System.out.println(k);
                        String t = "Update Vaccination set VacStatus = ";
                        t+=k.otp;
                        t+=" Where Number=";
                        t = t + '"';
                        t = t + m.num;
                        t = t + '"';
                        t = t + ';';
                        System.out.println(t);
                        preSat = connection.prepareStatement(t);
                        preSat.executeUpdate();
                    }
                    else{
                        op.writeObject(new Message_otp(0));
                        Message_otp k = (Message_otp) oi.readObject();
                        System.out.println(k);
                        SimpleDateFormat ft =
                                new SimpleDateFormat("yyyy-MM-dd");
                        Date dateobj = new Date();
                        System.out.println(ft.format(dateobj));
                        String query1 = "Insert into Vaccination values (?,?,?)";
                        preSat = connection.prepareStatement(query1);
                        preSat.setLong(1, m.num);
                        preSat.setDate(2, java.sql.Date.valueOf((ft.format(dateobj))));
                        preSat.setInt(3, k.otp);
                        System.out.println(query1);
                        preSat.execute();
                    }
                }
                else if(m.t==Message.job.Time){
                    String url = "jdbc:mysql://localhost:3306/Covid";
                    Connection connection = DriverManager.getConnection(url, "root", "");
                    if(m.k==0){
                        String q="Select Count(Distinct(Date)) As Ans from Vaccination";
                        System.out.println(q);
                        PreparedStatement preSat;
                        preSat = connection.prepareStatement(q);
                        ResultSet result = preSat.executeQuery();
                        if(result.next()){
                            Message_Time_Graph k = new Message_Time_Graph(result.getInt("Ans")+1);
                            q="Select Count(Distinct(Number)) as Ans, Date From Vaccination As v where exists( Select Distinct(Date) As Date from Vaccination);";
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
                        String q="Select Count(Distinct(Number)) as Ans, Date From Vaccination As v where exists( Select Distinct(Date) As Date from Vaccination Order by Date DESC);";
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
                        String q="Select Count(Distinct(Number)) as Ans, Date From Vaccination As v where exists( Select Distinct(Date) As Date from Vaccination Order by Date DESC);";
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
                case "Name":
                    user.setName(resultSet.getString(i));
                    break;
                case "Number":
                    user.setNumber(resultSet.getString(i));
                    break;
                case "email":
                    user.setEmail(resultSet.getString(i));
                    break;
                case "IdProof":
                    user.setIdProof(resultSet.getBytes(i));
                    break;
                case "Photo":
                    user.setPhoto(resultSet.getBytes(i));
                    break;
            }
        }

        return user;
    }

}
