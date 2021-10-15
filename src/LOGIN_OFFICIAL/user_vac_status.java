package LOGIN_OFFICIAL;

import Message.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class user_vac_status {
    public Button status_bt;
    public Button updateBT;
    public TextField number;
    public Label staus_vac;
    Message_otp vac;
    Socket socket;
    ObjectOutputStream op;
    ObjectInputStream oi;
    public ChoiceBox UpStat;
    public void BTupdate(ActionEvent actionEvent)throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (UpStat.getValue().equals("First Doze")) {
                        System.out.println("Thread "+UpStat.getValue());
                        op.writeObject(new Message_otp(1));
                        System.out.println("popop");
                    }
                    else if(UpStat.getValue().equals("Second Doze")) {
                        op.writeObject(new Message_otp(2));
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public void btStatus(ActionEvent actionEvent)throws Exception{

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message m = new Message("","","",0,3);
                m.num=Long.parseLong(number.getText());
                try {
                    socket=new Socket("localhost",5400);
                    op= new ObjectOutputStream(socket.getOutputStream());
                    op.writeObject(m);
                    oi = new ObjectInputStream(socket.getInputStream());
                    vac=(Message_otp) oi.readObject();
                    System.out.println(vac);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if(vac.otp==0){
                                staus_vac.setText("Not Vaccinated");
                                String st[]={"First Doze"};
                                UpStat.setItems(FXCollections.observableArrayList(st));
                            }
                            else if(vac.otp==1){
                                staus_vac.setText("Given First Doze");
                                String st[]={"Second Doze"};
                                UpStat.setItems(FXCollections.observableArrayList(st));
                            }
                            else{
                                staus_vac.setText("Fully Vaccinated");
                                String st[]={"Already Vaccinated"};
                                UpStat.setItems(FXCollections.observableArrayList(st));
                            }
                        }

                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }).start();
    }
}
