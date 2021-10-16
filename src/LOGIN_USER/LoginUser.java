package LOGIN_USER;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;

import Message.*;
import javafx.stage.Stage;

public class LoginUser{
    public Button btotp;
    public Button lgbt;
    public TextField UTfield;
    public TextField ETfield;
    public TextField OTfield;
    public Socket socket;
    public Label lb_verified;
    Message_otp m;
    ObjectInputStream oi=null;
    public void lb(javafx.event.ActionEvent actionEvent)throws Exception {
        System.out.println("hello");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Thread");
                    socket = new Socket("localhost",5400);
                    ObjectOutputStream op = new ObjectOutputStream(socket.getOutputStream());
                    String name=UTfield.getText();
                    String password = ETfield.getText();
                    op.writeObject(new Message(name,password,"email",0,0));
                    op.flush();
                    try{
                        oi = new ObjectInputStream(socket.getInputStream());
                        m= (Message_otp) oi.readObject();
                        System.out.println("OTP: " + m.otp);
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }).start();
    }

    public void lbt(ActionEvent actionEvent)throws Exception {
        if(m.otp==Integer.parseInt(OTfield.getText())){
            lb_verified.setText("Verified");
            Parent root=null;
            Stage stage = (Stage) lgbt.getScene().getWindow();
            URL url = getClass().getResource("../SearchByUser/SearchByUser.fxml");
            if(url != null){
                try{
                    root = FXMLLoader.load(url);
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                stage.setScene(new Scene(root,600, 400));
            }else{
                System.out.println("NOt LOADED");
            }
        }
        else{
            lb_verified.setText("Wrong OTP");
        }
    }
}