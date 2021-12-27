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
import sample.Controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class SignUP{
    public Button Sbutton;
    public Button backBt;
    public TextField UTF;
    public TextField NTF;
    public TextField ETF;
    public TextField PhTF;
    public TextField PASSTF;
    public Label lable;
    public static boolean isValidMobileNo(String str)
    {
        //(0/91): number starts with (0/91)
        //[7-9]: starting of the number may contain a digit between 0 to 9
        //[0-9]: then contains digits 0 to 9
        Pattern ptrn = Pattern.compile("(0/91)?[7-9][0-9]{9}");
        //the matcher() method creates a matcher that will match the given input against this pattern
        Matcher match = ptrn.matcher(str);
        //returns a boolean value
        return (match.find() && match.group().equals(str));
    }
    public static boolean isValidMobileEmail(String email)
    {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(email);
        //the matcher() method creates a matcher that will match the given input against this pattern
        if(mat.matches()){
            //returns a boolean value
            return true;
        }else{
            //returns a boolean value
            return false;
        }
    }
    public void sAction(ActionEvent actionEvent)throws Exception {
        System.out.println("in sign up");
        try {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(isValidMobileNo(PhTF.getText())&&isValidMobileEmail(ETF.getText())) {
                            Message m = new Message(UTF.getText(), NTF.getText(), ETF.getText(), PASSTF.getText(), 0, 1);
                            m.num = Long.parseLong(PhTF.getText());
                            Socket socket = new Socket("localhost", 5400);
                            ObjectOutputStream op = new ObjectOutputStream(socket.getOutputStream());
                            ObjectInputStream oi = new ObjectInputStream(socket.getInputStream());
                            op.writeObject(m);
                            sleep(1000);
                            Message_otp mo = (Message_otp) oi.readObject();
                            System.out.println(mo);
                            if (mo.otp == 1) {
                                System.out.println("in sign up and this username is already taken");
                                lable.setText("Already Taken So try other username");
                            } else {
                                lable.setText("Successfully account created");
                                Parent root = null;
                                Stage stage = (Stage) Sbutton.getScene().getWindow();
                                try {
                                    root = FXMLLoader.load(getClass().getResource("../Sample/sample.fxml"));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                stage.setScene(new Scene(root, 600, 400));
                            }
                            op.flush();
                            op.close();
                        }
                        else{
                            lable.setText("Enter Valid Email/Number");
                        }
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

    public void backAction(ActionEvent actionEvent) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "../sample/sample.fxml"
                )
        );

        Stage stage = (Stage) backBt.getScene().getWindow();
//            stage.hide();
        stage.setScene(
                new Scene(loader.load(),950, 740)
        );
        Controller controller = loader.getController();
        controller.initSample();
    }
}
