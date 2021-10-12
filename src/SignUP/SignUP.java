package SignUP;

import Message.*;
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

public class SignUP{
    public Button Sbutton;
    public TextField UTF;
    public TextField ETF;
    public TextField PTF;
    public TextField PASSTF;
    public Label lable;
    public void sAction(ActionEvent actionEvent)throws Exception {
        System.out.println("Hello");
        try {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        Message m = new Message(UTF.getText(),PASSTF.getText(),ETF.getText(),0,1);
                        m.num=Long.parseLong(PTF.getText());
                        Socket socket = new Socket("localhost",5400);
                        ObjectOutputStream op = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream oi = new ObjectInputStream(socket.getInputStream());
                        op.writeObject(m);
                        sleep(1000);
                        Message_otp mo=(Message_otp) oi.readObject();
                        System.out.println(mo);
                        if(mo.otp==1){
                            System.out.println("YAAAG");
                            lable.setText("Already Taken");
                        }
                        else{
                            lable.setText("Signed In");
                            Parent root=null;
                            Stage stage = (Stage) Sbutton.getScene().getWindow();
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
