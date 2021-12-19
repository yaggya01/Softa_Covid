package Server;

import Message.Returned_SearchMessage;
import Message.SearchMessage;
import Message.Hosp_info;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

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
                if(m.which_operation == SearchMessage.job.search_slots){
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
                    Returned_SearchMessage obj = new Returned_SearchMessage(SearchMessage.job.search_slots, "", "");
                    while(rs.next()){
                        Hosp_info tmp_obj = new Hosp_info();
                        tmp_obj.HID = rs.getInt("HID");
                        tmp_obj.hospital_name = rs.getString("name");
                        tmp_obj.address = rs.getString("address") + "," + rs.getString("city") + "," +  rs.getString("state");
                        Integer hid = rs.getInt("HID");
                        String q2 = "Select * from vaccine_cnt where HID=";
                        q2 = q2 + hid;
                        q2 = q2 + ';';
                        PreparedStatement preSat2;
                        preSat2 = connection.prepareStatement(q2);
                        ResultSet rs2 = preSat2.executeQuery();
                        while(rs2.next()){
                            String vaccine_name = rs2.getString("vaccine_name");
                            int cnt = rs2.getInt("remaining");
                            tmp_obj.vac.add(vaccine_name + ": " + cnt);
                        }
                        obj.ans.add(tmp_obj);
                    }
                    op.writeObject(obj);
                    op.flush();
                }else if(m.which_operation == SearchMessage.job.book_slots){
                    String url = "jdbc:mysql://localhost:3306/Covid";
                    Connection connection = DriverManager.getConnection(url, "root", "");
                    String q = "Select count(*) from doses where username=";
                    q = q + '"';
                    q = q + m.user.getUsername();
                    q = q + '"';
                    q = q + " and done=1";
                    q = q + ';';
                    System.out.println(q);
                    PreparedStatement preSat;
                    preSat = connection.prepareStatement(q);
                    ResultSet rs = preSat.executeQuery();
                    int count_done=0;
                    if(rs.next()){
                        count_done = rs.getInt("count(*)");  // count of vaccines taken by him
                    }
                     q = "Select count(*) from doses where username=";
                    q = q + '"';
                    q = q + m.user.getUsername();
                    q = q + '"';
                    q = q + " and done=0";
                    q = q + ';';
                    preSat = connection.prepareStatement(q);
                    rs = preSat.executeQuery();
                    int count_notdone = 1000;
                    if(rs.next()){
                        count_notdone = rs.getInt("count(*)");  // count of slots booked   by him
                    }
                    q = "Select * from doses where username=";
                    q = q + '"';
                    q = q + m.user.getUsername();
                    q = q + '"';
                    q = q + " and done=1 order by date desc";
                    q = q + ';';
                    preSat = connection.prepareStatement(q);
                    rs = preSat.executeQuery();
                    String last_vaccine="";                          // to get name and date of last vaccine taken
                    Date last_date = java.util.Calendar.getInstance().getTime();
                    int needed=0, days_diff=0, remaining=0;                      // to check if days b/w 2 days is good or is already vaccinated
                    if(rs.next()){
                         last_vaccine = rs.getString("vaccine_name"); // to get name and date of last vaccine taken
                         last_date = rs.getDate("date");

                    }
                    String q2 = "Select * from vaccine where vaccine_name = "; // to see its needed value and days diff
                    q2 += '"';
                    q2 += m.vaccine_name;
                    q2 += '"';
                    q2 += ';';
                    PreparedStatement preSat2;
                    preSat2 = connection.prepareStatement(q2);
                    ResultSet rs2 = preSat2.executeQuery();
                    if(rs2.next()){
                        needed = rs2.getInt("doses_needed");
                        days_diff = rs2.getInt("days_diff");
                    }
                    String q3 = "Select * from vaccine_cnt where hid = ";
                    q3 += m.hid;
                    q3 += " and vaccine_name = ";
                    q3 += '"';
                    q3 += m.vaccine_name;
                    q3 += '"';
                    q3 += ';';
                    PreparedStatement preSat3;
                    preSat3 = connection.prepareStatement(q3);
                    ResultSet rs3 = preSat3.executeQuery();
                    if(rs3.next()){
                        remaining = rs3.getInt("remaining");
                    }
                    Date date=java.util.Calendar.getInstance().getTime();
                    int diffInDays = (int) ((date.getTime() - last_date.getTime()) / (1000 * 60 * 60 * 24));

                    System.out.println("sdkj" + count_done + " " + needed + " " + count_notdone + "needed"+ days_diff + " " +  remaining + " ");
                    System.out.println(last_vaccine + " " + m.vaccine_name);

                    if(count_done >= needed || (count_done>0 && days_diff<(diffInDays)) || count_notdone>0 || remaining == 0 || (count_done>0 && last_vaccine != m.vaccine_name)){
                        Returned_SearchMessage obj = new Returned_SearchMessage(SearchMessage.job.book_slots, "Not Done", "");
                        System.out.println(obj.StatusOfBookingOperation);
                        op.writeObject(obj);
                        op.flush();
                    }else{
                        String q4 = "update vaccine_cnt set remaining = remaining - 1 where vaccine_name = ";
                        q4 += '"';
                        q4 += m.vaccine_name;
                        q4 += '"';
                        q4 += " and hid = ";
                        q4 += m.hid;
                        q4 += ';';
                        PreparedStatement preSat4;
                        preSat4 = connection.prepareStatement(q4);
                        preSat4.executeUpdate();

                        int count_of_doses=0;
                        q4 = "Select count(*) from vaccine;"; // to get DID = no. of doses already present + 1
                        preSat4 = connection.prepareStatement(q4);
                        ResultSet rs4 = preSat4.executeQuery();
                        if(rs4.next()){
                            count_of_doses = rs4.getInt("count(*)");
                        }
                        q4 = "Insert into doses values (?,?,?,?,?,?,?)";
                        preSat4 = connection.prepareStatement(q4);
                        preSat4.setString(1, m.user.getUsername());
                        preSat4.setLong(2, count_of_doses+1);
                        preSat4.setString(3, m.vaccine_name);
                        preSat4.setInt(4, count_done+1);
                        preSat4.setInt(5, m.hid);
                        preSat4.setBoolean(6, false);
                        java.sql.Timestamp date2 = new java.sql.Timestamp(new java.util.Date().getTime());
                        preSat4.setTimestamp(7, date2);
                        preSat4.execute();
                        Returned_SearchMessage obj = new Returned_SearchMessage(SearchMessage.job.book_slots, "DONE" ,"");
                        System.out.println(obj.StatusOfBookingOperation);
                        op.writeObject(obj);
                        op.flush();
                    }
                }else{
                    String url = "jdbc:mysql://localhost:3306/Covid";
                    Connection connection = DriverManager.getConnection(url, "root", "");
                    String q = "Select count(*) from doses where username=";
                    q = q + '"';
                    q = q + m.user.getUsername();
                    q = q + '"';
                    q = q + " and done=1";
                    q = q + ';';
                    System.out.println(q);
                    PreparedStatement preSat;
                    preSat = connection.prepareStatement(q);
                    ResultSet rs = preSat.executeQuery();
                    int count_done = 0;
                    if(rs.next()){
                        rs.getInt("count(*)");  // count of vaccines taken by him
                    }
                    q = "Select * from doses where username=";
                    q = q + '"';
                    q = q + m.user.getUsername();
                    q = q + '"';
                    q = q + " and done=1 order by date desc";
                    q = q + ';';
                    preSat = connection.prepareStatement(q);
                    rs = preSat.executeQuery();
                    String last_vaccine="";                          // to get name and date of last vaccine taken
                    if(rs.next()){
                        last_vaccine = rs.getString("vaccine_name");
                    }
                    Returned_SearchMessage obj = new Returned_SearchMessage(SearchMessage.job.initialize, "", "");
                    if(count_done==0){
                        obj.vaccineStatusOfUser = "No vaccines taken by you";

                    }else{
                        obj.vaccineStatusOfUser = Integer.toString(count_done) + " doses of " + last_vaccine + " taken by you ";
                    }
                    System.out.println(obj.vaccineStatusOfUser);
                    op.writeObject(obj);
                    op.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
