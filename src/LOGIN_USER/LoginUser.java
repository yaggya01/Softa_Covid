package LOGIN_USER;


import HomePage.HomePage;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Message.*;
import javafx.stage.Stage;

public class LoginUser{
    public Button btotp;
    public Button lgbt;
    public TextField UTF;
    public TextField OTF;
    public TextField PASSTF;
    public Socket socket;
    public Label lb_verified;
    Message_otp m;
    ObjectInputStream oi=null;
    public void lb(javafx.event.ActionEvent actionEvent)throws Exception {
        System.out.println("generating otp");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Thread");
                    socket = new Socket("localhost",5400);
                    ObjectOutputStream op = new ObjectOutputStream(socket.getOutputStream());
                    String username=UTF.getText();
                    String password = PASSTF.getText();
                    op.writeObject(new Message(username,"Name","email",password, 0,0));
                    op.flush();
                    try{
                        oi = new ObjectInputStream(socket.getInputStream());
                        m= (Message_otp) oi.readObject();
                        System.out.println("LoginUser OTP: " + m.otp);
                        System.out.println("LoginUser user: " + m.user);
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
        if(m.otp==Integer.parseInt(OTF.getText())){
            lb_verified.setText("Verified");
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "../HomePage/HomePage.fxml"
                    )
            );

            Stage stage = (Stage) lb_verified.getScene().getWindow();
//            stage.hide();
            stage.setScene(
                    new Scene(loader.load(), 800, 500)
            );

            HomePage controller = loader.getController();
            controller.initHomePageData(m.user);

//            stage.show();
        }
        else{
            lb_verified.setText("Wrong OTP :(");
        }
    }
}