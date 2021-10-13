package Server;

import Message.*;

import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.*;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
                            if (m.password.equals(g)) {
                                Random rand = new Random();
                                int otp= rand.nextInt(9999);
                                JavaMailUtil.sendMail(result.getString("Email"),otp);
                                k = new Message_otp(otp);
                                System.out.println(k);
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
                        String query1 = "Insert into USER values (?,?,?,?)";
                        preSat = connection.prepareStatement(query1);
                        preSat.setString(1, m.name);
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
