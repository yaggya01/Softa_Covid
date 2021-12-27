package Server;

import Message.Returned_SearchMessage;
import Message.SearchMessage;
import Message.Hosp_info;
import User.User;

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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
                    Returned_SearchMessage obj = new Returned_SearchMessage(SearchMessage.job.search_slots, "", "", m.user);
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
                            tmp_obj.vac.add(vaccine_name + " : ");
                            if(cnt == 0){
                                String q3 = "Select count(*) from doses where HID=";
                                q3 = q3 + hid;
                                q3 = q3 + " and vaccine_name = ";
                                q3 = q3 + '"';
                                q3 = q3 + vaccine_name;
                                q3 = q3 + '"';
                                q3 = q3 + " and done = 2";
                                q3 = q3 + ';';
                                PreparedStatement preSat3;
                                preSat3 = connection.prepareStatement(q3);
                                ResultSet rs3 = preSat3.executeQuery();
                                int count_of_waitlist=0;
                                if(rs3.next()){
                                    count_of_waitlist = rs3.getInt("count(*)");  // count of waitlist
                                }
                                tmp_obj.vac.add(count_of_waitlist + " in waitlist ");
                            }else{
                                tmp_obj.vac.add(cnt + " remaining ");
                            }
                        }
                        obj.ans.add(tmp_obj);
                    }
                    op.writeObject(obj);
                    op.flush();
                }else if(m.which_operation == SearchMessage.job.book_slots){
                    Lock lock = new ReentrantLock(true);
                    try{
                        lock.lock();
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
                        q = "Select max(did) from doses;";
                        System.out.println(q);
                        preSat = connection.prepareStatement(q);
                        rs = preSat.executeQuery();
                        int max_did=1;
                        if(rs.next()){
                            max_did = rs.getInt("max(did)");  // maximum did
                        }
                        q = "Select count(*) from doses where username=";
                        q = q + '"';
                        q = q + m.user.getUsername();
                        q = q + '"';
                        q = q + " and done IN (0,2)";  // to get bboked or waitlist doses
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
                        String q3 = "Select * from vaccine_cnt where hid = "; // to find remaining vaccines
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

                        System.out.println("countdone= " + count_done + "needed =" + needed + " not done =" + count_notdone + "days diffneeded"+ days_diff + "remaining " +  remaining + " ");
                        System.out.println(last_vaccine + " " + m.vaccine_name);


                        if(count_done >= needed){
                            Returned_SearchMessage obj = new Returned_SearchMessage(SearchMessage.job.book_slots, "You are already vaccinated", "", m.user);
                            System.out.println(obj.StatusOfBookingOperation);
                            op.writeObject(obj);
                            op.flush();
                        }else if(count_done>0 && days_diff<(diffInDays)){
                            Returned_SearchMessage obj = new Returned_SearchMessage(SearchMessage.job.book_slots, "wait MORE", "",m.user);
                            System.out.println(obj.StatusOfBookingOperation);
                            op.writeObject(obj);
                            op.flush();
                        }
                        else if(count_notdone > 0){
                            Returned_SearchMessage obj = new Returned_SearchMessage(SearchMessage.job.book_slots, "You already have a booking", "",m.user);
                            System.out.println(obj.StatusOfBookingOperation);
                            op.writeObject(obj);
                            op.flush();
                        }
                        else if((count_done>0 && last_vaccine != m.vaccine_name)){
                            Returned_SearchMessage obj = new Returned_SearchMessage(SearchMessage.job.book_slots, "dont take multiple vaccines", "",m.user);
                            System.out.println(obj.StatusOfBookingOperation);
                            op.writeObject(obj);
                            op.flush();
                        }
                        else if(remaining == 0){  // to put in waitlist
                            int count_of_doses=0;
                            String q4 = "Select count(*) from vaccine;"; // to get DID = no. of doses already present + 1
                            PreparedStatement preSat4 = connection.prepareStatement(q4);
                            ResultSet rs4 = preSat4.executeQuery();
                            if(rs4.next()){
                                count_of_doses = rs4.getInt("count(*)");
                            }
                            q4 = "Insert into doses values (?,?,?,?,?,?,?)";
                            preSat4 = connection.prepareStatement(q4);
                            preSat4.setString(1, m.user.getUsername());
                            preSat4.setLong(2, max_did+1);
                            preSat4.setString(3, m.vaccine_name);
                            preSat4.setInt(4, count_done+1);
                            preSat4.setInt(5, m.hid);
                            preSat4.setInt(6, 2);
                            java.sql.Timestamp date2 = new java.sql.Timestamp(new java.util.Date().getTime());
                            preSat4.setTimestamp(7, date2);
                            preSat4.execute();
                            Returned_SearchMessage obj = new Returned_SearchMessage(SearchMessage.job.book_slots, "waitlisted" ,"",m.user);
                            String booking_status = "1 waitlist for" + m.vaccine_name + " on " + date;
                            obj.user.setBooking_status(booking_status);
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
                            preSat4.setLong(2, max_did+1);
                            preSat4.setString(3, m.vaccine_name);
                            preSat4.setInt(4, count_done+1);
                            preSat4.setInt(5, m.hid);
                            preSat4.setInt(6, 0);
                            java.sql.Timestamp date2 = new java.sql.Timestamp(new java.util.Date().getTime());
                            preSat4.setTimestamp(7, date2);
                            preSat4.execute();
                            Returned_SearchMessage obj = new Returned_SearchMessage(SearchMessage.job.book_slots, "done" ,"", m.user);
                            String booking_status = "1 booking for" + m.vaccine_name + " on " + date;
                            obj.user.setBooking_status(booking_status);
                            System.out.println(obj.StatusOfBookingOperation);
                            op.writeObject(obj);
                            op.flush();
                        }
                    }finally {
                        lock.unlock();
                    }

                }else if(m.which_operation == SearchMessage.job.cancel_booking){
                    String url = "jdbc:mysql://localhost:3306/Covid";
                    Connection connection = DriverManager.getConnection(url, "root", "");
                    // getting the hid and vaccine name to update later
                    String q = "Select * from doses where username=";
                    q = q + '"';
                    q = q + m.user.getUsername();
                    q = q + '"';
                    q = q + ';';
                    System.out.println(q);
                    PreparedStatement preSat;
                    preSat = connection.prepareStatement(q);
                    ResultSet rs = preSat.executeQuery();
                    int done=-1, hid=-1;String vaccine_name = "";
                    if(rs.next()){
                        hid = rs.getInt("hid");
                        vaccine_name = rs.getString("vaccine_name");
                        done = rs.getInt("done");
                        if(done == 0){
                            String q4 = "delete from doses where username = ";
                            q4 += '"';
                            q4 += m.user.getUsername();
                            q4 += '"';
                            q4 += " and done = 0";
                            q4 += ';';
                            System.out.println(q4);
                            PreparedStatement preSat4;
                            preSat4 = connection.prepareStatement(q4);
                            preSat4.executeUpdate();
                            // updating the waitlist and making one of the as booked
                            String q2 = "Select * from doses where hid=";
                            q2 = q2 + hid;
                            q2 = q2 + " and vaccine_name = ";
                            q2 = q2 + '"';
                            q2 = q2 + vaccine_name;
                            q2 = q2 + '"';
                            q2 = q2 + " and done = 2 order by date desc";
                            q2 = q2 + ';';
                            System.out.println(q2);
                            PreparedStatement preSat2;
                            preSat2 = connection.prepareStatement(q2);
                            ResultSet rs2 = preSat2.executeQuery();
                            if(rs2.next()){
                                String user_name = rs2.getString("username");
                                System.out.println(user_name);
                                String q3 = "update doses set done = 0 where username = ";
                                q3 += '"';
                                q3 += user_name;
                                q3 += '"';
                                q3 += ';';
                                PreparedStatement preSat3;
                                preSat3 = connection.prepareStatement(q3);
                                preSat3.executeUpdate();
                            }
                            User obj = new User(m.user);
                            obj.setBooking_status("none");
                            op.writeObject(obj);
                            op.flush();
                        }else if(done == 2){
                            String q4 = "delete from doses where username = ";
                            q4 += '"';
                            q4 += m.user.getUsername();
                            q4 += '"';
                            q4 += ';';
                            System.out.println(q4);
                            PreparedStatement preSat4;
                            preSat4 = connection.prepareStatement(q4);
                            preSat4.executeUpdate();
                            User obj = new User(m.user);
                            obj.setBooking_status("none");
                            op.writeObject(obj);
                            op.flush();
                        }
                    }else{
                        User obj = new User(m.user);
                        obj.setBooking_status("none");
                        op.writeObject(obj);
                        op.flush();
                    }
                }else if(m.which_operation == SearchMessage.job.update_vaccine){
                    int increase=m.no_of_vaccines;
                    String url = "jdbc:mysql://localhost:3306/Covid";
                    Connection connection = DriverManager.getConnection(url, "root", "");
                    String q = "Select * from doses where vaccine_name=";
                    q = q + '"';
                    q = q + m.vaccine_name;
                    q = q + '"';
                    q = q + " and hid=";
                    q = q + m.hid;
                    q = q + " and done = 2 order by date desc";
                    q = q + ';';
                    System.out.println(q);
                    PreparedStatement preSat;
                    preSat = connection.prepareStatement(q);
                    ResultSet rs = preSat.executeQuery();
                    Boolean f=false;
                    while(rs.next()){
                        if(increase <= 0) break;
                        f=true;
                        increase--;
                        String username = rs.getString("username");
                        String q3 = "update doses set done = 0 where username = ";
                        q3 += '"';
                        q3 += username;
                        q3 += '"';
                        q3 += ';';
                        System.out.println(q3);
                        PreparedStatement preSat3;
                        preSat3 = connection.prepareStatement(q3);
                        preSat3.executeUpdate();
                    }
                    if(increase>0){
                        String q3 = "update vaccine_cnt set remaining = remaining +  ";
                        q3 += increase;
                        q3 += " where hid = ";
                        q3 += m.hid;
                        q3 += " and vaccine_name = ";
                        q3 += '"';
                        q3 += m.vaccine_name;
                        q3 += '"';
                        q3 += ';';
                        System.out.println(q3);
                        PreparedStatement preSat3;
                        preSat3 = connection.prepareStatement(q3);
                        preSat3.executeUpdate();
                    }
                    Returned_SearchMessage obj = new Returned_SearchMessage(SearchMessage.job.update_vaccine, "done", "", null);
//                     if(f == false) obj.StatusOfBookingOperation = "not done";
                    op.writeObject(obj);
                    op.flush();
                }
                else{
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
                    Returned_SearchMessage obj = new Returned_SearchMessage(SearchMessage.job.initialize, "", "", m.user);
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
