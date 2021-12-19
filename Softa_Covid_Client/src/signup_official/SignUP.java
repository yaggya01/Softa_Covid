package signup_official;

import Message.Message;
import Message.Message_otp;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class SignUP {
    public Button signup_button;
    public TextField username_textfield;
    public TextField number_textfield;
    public TextField email_textfield;
    public TextField password_textfield;
    public Label lable;
    public void signup(ActionEvent actionEvent)throws Exception {
        System.out.println("in sign up official");
        try {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        Message m = new Message(username_textfield.getText(),"",email_textfield.getText(), password_textfield.getText(),0,10);
                        m.num=Long.parseLong(number_textfield.getText());
                        Socket socket = new Socket("localhost",5400);
                        ObjectOutputStream op = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream oi = new ObjectInputStream(socket.getInputStream());
                        op.writeObject(m);
                        sleep(1000);
                        Message_otp mo=(Message_otp) oi.readObject();
                        System.out.println(mo);
                        if(mo.otp==1){
                            System.out.println("in sign up official and this username is already taken");
                            lable.setText("Already Taken So try other username");
                        }
                        else{
                            lable.setText("Successfully official account created");
                            Parent root=null;
                            Stage stage = (Stage) signup_button.getScene().getWindow();
                            try{
                                root = FXMLLoader.load(getClass().getResource("../Sample/sample.fxml"));
                            }
                            catch (IOException e){
                                e.printStackTrace();
                            }
                            stage.setScene(new Scene(root,600, 400));
                        }
                        op.flush();
                        op.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
